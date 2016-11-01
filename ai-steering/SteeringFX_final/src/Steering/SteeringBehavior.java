
package Steering;

import common.D2.Vector2D;
import common.misc.Utils;

/**
 * Вычислить вектор силы(steeringForce), учитывающий возможные варианты "поведения"(behavFlag)
 */
public class SteeringBehavior {
      
    public static enum CalculateForceMethod {
        WEIGHTED_AVERAGE, PRIORITIZED, DITHERED;      
    }
    // Обёртка поведений
    private Behaviors behaviors;
    // накопительный флаг включенных поведенческих состояний
    private BehaviorFlag behavFlag;
    // the steering force created by the combined effect of all the selected behaviors
    private Vector2D steeringForce;   
    // what type of method is used to calculate steering force
    private CalculateForceMethod calculateForceMethod;         
    // max force of vehicle
    private double maxForce;
    private boolean cellSpaceOn;
            
    public SteeringBehavior(Vehicle vehicle) {        
        behaviors = new Behaviors(vehicle);
        behavFlag = new BehaviorFlag();
        steeringForce = new Vector2D(0, 0);
        calculateForceMethod = CalculateForceMethod.PRIORITIZED;
        maxForce = vehicle.getMaxForce();
    }
    
    /**
     * calculates the accumulated steering force according to the method set in calculateForceMethod
     */
    public Vector2D calculateForce() {
        //reset the steering force
        steeringForce.zero();
       
        switch (calculateForceMethod) {
            case WEIGHTED_AVERAGE:
                calculateWeightedSum();
                break;
            case PRIORITIZED:
                calculatePrioritized();
                break;
            case DITHERED:
                calculateDithered();
                break;
        }
        return steeringForce;
    }

    /**
     *  this method calls each active steering behavior in order of priority
     *  and acumulates their forces until the max steering force magnitude
     *  is reached, at which time the function returns the steering force 
     *  accumulated to that  point
     */
    private void calculatePrioritized() {
        Vector2D force;
           
        if (behavFlag.isWallAvoidanceOn()) {
            force = Vector2D.mul(behaviors.wallAvoidance(), Params.WALL_AVOIDANCE_WEIGHT);
            if (!accumulateForce(force)) {
                return;
            }
        }
        if (behavFlag.isObstacleAvoidanceOn()) {
            force = Vector2D.mul(behaviors.obstacleAvoidance(), Params.OBSTACLE_AVOIDANCE_WEIGHT);
            if (!accumulateForce(force)) {
                return;
            }
        }
        if (behaviors.getTargetAgent1() != null) {
            if (behavFlag.isEvadeOn()) {    
                force = Vector2D.mul(behaviors.evade(), Params.EVADE_WEIGHT);
                if (!accumulateForce(force)) {
                    return;
                }
            }
            if (behavFlag.isPursuitOn()) {
                force = Vector2D.mul(behaviors.pursuit(), Params.PURSUIT_WEIGHT);
                if (!accumulateForce(force)) {
                    return;
                }
            }
        }
        
        //these next three can be combined for flocking behavior (wander is also a good behavior to add into this mix)
        if (!isSpacePartitioningOn()) {
            if (behavFlag.isSeparationOn()) {
                force = Vector2D.mul(behaviors.separation(), Params.SEPARATION_WEIGHT);
                if (!accumulateForce(force)) {
                    return;
                }
            }
            if (behavFlag.isAlignmentOn()) {
                force = Vector2D.mul(behaviors.alignment(), Params.ALIGNMENT_WEIGHT);
                if (!accumulateForce(force)) {
                    return;
                }
            }
            if (behavFlag.isCohesionOn()) {
                force = Vector2D.mul(behaviors.cohesion(), Params.COHESION_WEIGHT);
                if (!accumulateForce(force)) {
                    return;
                }
            }
        } 
        else {
            if (behavFlag.isSeparationOn()) {
                force = Vector2D.mul(behaviors.separationCell(), Params.SEPARATION_WEIGHT);
                if (!accumulateForce(force)) {
                    return;
                }
            }
            if (behavFlag.isAlignmentOn()) {
                force = Vector2D.mul(behaviors.alignmentCell(), Params.ALIGNMENT_WEIGHT);
                if (!accumulateForce(force)) {
                    return;
                }
            }
            if (behavFlag.isCohesionOn()) {
                force = Vector2D.mul(behaviors.cohesionCell(), Params.COHESION_WEIGHT);
                if (!accumulateForce(force)) {
                    return;
                }
            }
        }
        
        if (behavFlag.isFleeOn()) {    
            force = Vector2D.mul(behaviors.flee(), Params.FLEE_WEIGHT);
            if (!accumulateForce(force)) {
                return;
            }
        }       
        if (behavFlag.isSeekOn()) {
            force = Vector2D.mul(behaviors.seek(), Params.SEEK_WEIGHT);
            if (!accumulateForce(force)) {
                return;
            }
        }
        if (behavFlag.isArriveOn()) {
            force = Vector2D.mul(behaviors.arrive(Params.DECELERATION_NORMAL), Params.ARRIVE_WEIGHT);
            if (!accumulateForce(force)) {
                return;
            }
        }
        if (behavFlag.isWanderOn()) {
            force = Vector2D.mul(behaviors.wander(), Params.WANDER_WEIGHT);
            if (!accumulateForce(force)) {
                return;
            }
        }
        if (behavFlag.isOffsetPursuitOn()) {
            if (behaviors.getTargetAgent1() != null && getBehaviors().getOffsetPursuit() != null) {
                force = behaviors.offsetPursuit();
                if (!accumulateForce(force)) {
                    return;
                }
            }
        }
        if (behavFlag.isInterposeOn()) {
            if (behaviors.getTargetAgent1() != null && behaviors.getTargetAgent2() != null) {
                force = Vector2D.mul(behaviors.interpose(), Params.INTERPOSE_WEIGHT);
                if (!accumulateForce(force)) {
                    return;
                }
            }
        }
        if (behavFlag.isHideOn() && behaviors.getTargetAgent1() != null) {
            force = Vector2D.mul(behaviors.hide(), Params.HIDE_WEIGHT);
            if (!accumulateForce(force)) {
                return;
            }
        }
        if (behavFlag.isFollowPathOn()) {
            force = Vector2D.mul(behaviors.followPath(), Params.FOLLOW_PATH_WEIGHT);
            if (!accumulateForce(force)) {
                return;
            }
        }
        
    }
    
    /**
     * This function calculates how much of its max steering force the 
     * vehicle has left to apply and then applies that amount of the force to add.
     */
    private boolean accumulateForce(Vector2D plusForce) {
        //calculate how much steering force remains to be used by this vehicle
        double remainingForce = maxForce - steeringForce.length();
        if (remainingForce <= 0.0) {
            return false;
        }

        if (plusForce.length() < remainingForce) {
            steeringForce.add(plusForce);
        } else {
            //add as much of the plusForce vector is possible without going over the max.
            steeringForce.add(Vector2D.mul(Vector2D.vec2DNormalize(plusForce), remainingForce));
        }
        return true;
    }

    /**
     *  this simply sums up all the active behaviors X their weights and 
     *  truncates the result to the max available steering force before  returning
     */
    private void calculateWeightedSum() {
             
        if (behavFlag.isWallAvoidanceOn()) {
            steeringForce.add(Vector2D.mul(behaviors.wallAvoidance(), Params.WALL_AVOIDANCE_WEIGHT));
        }
        if (behavFlag.isObstacleAvoidanceOn()) {
            steeringForce.add(Vector2D.mul(behaviors.obstacleAvoidance(), Params.OBSTACLE_AVOIDANCE_WEIGHT));
        }
        if (behaviors.getTargetAgent1() != null) {
            if (behavFlag.isEvadeOn()) { 
                steeringForce.add(Vector2D.mul(behaviors.evade(), Params.EVADE_WEIGHT));        
            }
            if (behavFlag.isPursuitOn()) {
                steeringForce.add(Vector2D.mul(behaviors.pursuit(), Params.PURSUIT_WEIGHT));
            }
        }
              
        //these next three can be combined for flocking behavior (wander is also a good behavior to add into this mix)
        if (!isSpacePartitioningOn()) {
            if (behavFlag.isSeparationOn()) {
                steeringForce.add(Vector2D.mul(behaviors.separation(), Params.SEPARATION_WEIGHT));
            }
            if (behavFlag.isAlignmentOn()) {
                steeringForce.add(Vector2D.mul(behaviors.alignment(), Params.ALIGNMENT_WEIGHT));
            }
            if (behavFlag.isCohesionOn()) {
                steeringForce.add(Vector2D.mul(behaviors.cohesion(), Params.COHESION_WEIGHT));
            }
        }
        else {
            if (behavFlag.isSeparationOn()) {
                steeringForce.add(Vector2D.mul(behaviors.separationCell(), Params.SEPARATION_WEIGHT));
            }
            if (behavFlag.isAlignmentOn()) {
                steeringForce.add(Vector2D.mul(behaviors.alignmentCell(), Params.ALIGNMENT_WEIGHT));
            }
            if (behavFlag.isCohesionOn()) {
                steeringForce.add(Vector2D.mul(behaviors.cohesionCell(), Params.COHESION_WEIGHT));
            }
        }     
        
        if (behavFlag.isWanderOn()) {
            steeringForce.add(Vector2D.mul(behaviors.wander(), Params.WANDER_WEIGHT));
        }
        if (behavFlag.isSeekOn()) {
            steeringForce.add(Vector2D.mul(behaviors.seek(), Params.SEEK_WEIGHT));
        }
        if (behavFlag.isFleeOn()) {
            steeringForce.add(Vector2D.mul(behaviors.flee(), Params.FLEE_WEIGHT));
        }
        if (behavFlag.isArriveOn()) {
            steeringForce.add(Vector2D.mul(behaviors.arrive(Params.DECELERATION_NORMAL), Params.ARRIVE_WEIGHT));
        }  
        if (behavFlag.isOffsetPursuitOn()) {
            if (behaviors.getTargetAgent1() != null && getBehaviors().getOffsetPursuit() != null) {
                steeringForce.add(Vector2D.mul(behaviors.offsetPursuit(), Params.OFFSET_PURSUIT_WEIGHT));
            }    
        }
        if (behavFlag.isInterposeOn()) {
            if (behaviors.getTargetAgent1() != null && behaviors.getTargetAgent2() != null) {
                steeringForce.add(Vector2D.mul(behaviors.interpose(), Params.INTERPOSE_WEIGHT));
            }
        }
        if (behavFlag.isHideOn() && behaviors.getTargetAgent1() != null) {
            steeringForce.add(Vector2D.mul(behaviors.hide(), Params.HIDE_WEIGHT));  
        }
        if (behavFlag.isFollowPathOn()) {
            steeringForce.add(Vector2D.mul(behaviors.followPath(), Params.FOLLOW_PATH_WEIGHT));
        }
         
        steeringForce.truncate(maxForce);
    }
    /**
     *  this method sums up the active behaviors by assigning a probability
     *  of being calculated to each behavior. It then tests the first priority
     *  to see if it should be calculated this simulation-step. If so, it
     *  calculates the steering force resulting from this behavior. If it is
     *  more than zero it returns the force. If zero, or if the behavior is
     *  skipped it continues onto the next priority, and so on.
     *
     *  NOTE: Not all of the behaviors have been implemented in this method,
     *        just a few, so you get the general idea
     */
    private void calculateDithered() {
        //reset the steering force
        steeringForce.zero();
        
        if (!isSpacePartitioningOn()) {
            if (behavFlag.isSeparationOn() && Utils.RandFloat() < Params.PR_ALIGNMENT) {
                steeringForce.add(Vector2D.mul(behaviors.separation(), Params.SEPARATION_WEIGHT / Params.PR_SEPARATION));
                if (!steeringForce.isZero()) {
                    steeringForce.truncate(maxForce);
                    return;
                }
            }
            if (behavFlag.isAlignmentOn() && Utils.RandFloat() < Params.PR_ALIGNMENT) {
                steeringForce.add(Vector2D.mul(behaviors.alignment(), Params.ALIGNMENT_WEIGHT / Params.PR_ALIGNMENT));
                if (!steeringForce.isZero()) {
                    steeringForce.truncate(maxForce);
                    return;
                }
            }
            if (behavFlag.isCohesionOn() && Utils.RandFloat() < Params.PR_COHESION) {
                steeringForce.add(Vector2D.mul(behaviors.cohesion(), Params.COHESION_WEIGHT / Params.PR_COHESION));
                if (!steeringForce.isZero()) {
                    steeringForce.truncate(maxForce);
                    return;
                }
            }
        }
        else {
            if (behavFlag.isSeparationOn() && Utils.RandFloat() < Params.PR_ALIGNMENT) {
                steeringForce.add(Vector2D.mul(behaviors.separationCell(), Params.SEPARATION_WEIGHT / Params.PR_SEPARATION));
                if (!steeringForce.isZero()) {
                    steeringForce.truncate(maxForce);
                    return;
                }
            }
            if (behavFlag.isAlignmentOn() && Utils.RandFloat() < Params.PR_ALIGNMENT) {
                steeringForce.add(Vector2D.mul(behaviors.alignmentCell(), Params.ALIGNMENT_WEIGHT / Params.PR_ALIGNMENT));
                if (!steeringForce.isZero()) {
                    steeringForce.truncate(maxForce);
                    return;
                }
            }
            if (behavFlag.isCohesionOn() && Utils.RandFloat() < Params.PR_COHESION) {
                steeringForce.add(Vector2D.mul(behaviors.cohesionCell(), Params.COHESION_WEIGHT / Params.PR_COHESION));
                if (!steeringForce.isZero()) {
                    steeringForce.truncate(maxForce);
                    return;
                }
            }
        }
        
        if (behavFlag.isWallAvoidanceOn() && Utils.RandFloat() < Params.PR_WALL_AVOIDANCE) {
            steeringForce = Vector2D.mul(behaviors.wallAvoidance(), Params.WALL_AVOIDANCE_WEIGHT/ Params.PR_WALL_AVOIDANCE);
            if (!steeringForce.isZero()) {
                steeringForce.truncate(maxForce);
                return;
            }
        }
        if (behavFlag.isObstacleAvoidanceOn() && Utils.RandFloat() < Params.PR_OBSTACLE_AVOIDANCE) {
            steeringForce.add(Vector2D.mul(behaviors.obstacleAvoidance(), Params.OBSTACLE_AVOIDANCE_WEIGHT / Params.PR_OBSTACLE_AVOIDANCE));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(maxForce);
                return;
            }
        }
        if (behavFlag.isFleeOn() && Utils.RandFloat() < Params.PR_FLEE) {
            steeringForce.add(Vector2D.mul(behaviors.flee(), Params.FLEE_WEIGHT / Params.PR_FLEE));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(maxForce);
                return;
            }
        }
        if (behavFlag.isEvadeOn() && Utils.RandFloat() < Params.PR_EVADE) {
            steeringForce.add(Vector2D.mul(behaviors.evade(), Params.EVADE_WEIGHT / Params.PR_EVADE));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(maxForce);
                return;
            }
        }
        if (behavFlag.isWanderOn() && Utils.RandFloat() < Params.PR_WANDER) {
            steeringForce.add(Vector2D.mul(behaviors.wander(), Params.WANDER_WEIGHT / Params.PR_WANDER));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(maxForce);
                return;
            }
        }
        if (behavFlag.isSeekOn() && Utils.RandFloat() < Params.PR_SEEK) {
            steeringForce.add(Vector2D.mul(behaviors.seek(), Params.SEEK_WEIGHT / Params.PR_SEEK));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(maxForce);
                return;
            }
        }
        if (behavFlag.isArriveOn() && Utils.RandFloat() < Params.PR_ARRIVE) {
            steeringForce.add(Vector2D.mul(behaviors.arrive(Params.DECELERATION_NORMAL), Params.ARRIVE_WEIGHT / Params.PR_ARRIVE));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(maxForce);
                return;
            }
        }
    }
    
    public void spacePartitioningOn() {
        cellSpaceOn = true;
    }
    
    public void spacePartitioningOff() {
        cellSpaceOn = false;
    }

    public boolean isSpacePartitioningOn() {
        return cellSpaceOn;
    }

    /**
     ******************************** Getters & Setters ******************************* 
     */
    public Behaviors getBehaviors() {
        return behaviors;
    }
    public BehaviorFlag getBehavFlag() {
        return behavFlag;
    }    
    public void setCalculateForceMethod(CalculateForceMethod method) {
        this.calculateForceMethod = method;
    }

}
