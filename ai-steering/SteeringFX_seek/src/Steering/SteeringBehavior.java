
package Steering;

import common.D2.Vector2D;
import common.D2.Transformation;
import common.misc.Utils;

class SteeringBehavior {
      
    public static enum CalculateForceMethod {
        WEIGHTED_AVERAGE, PRIORITIZED, DITHERED;      
    }
    //what type of method is used to calculate steering force
    private CalculateForceMethod calculateForceMethod = CalculateForceMethod.PRIORITIZED;         
  
    private Vehicle vehicle;
    //the steering force created by the combined effect of all the selected behaviors
    private Vector2D steeringForce = new Vector2D(0, 0);
    //these can be used to keep track of friends, pursuers, or prey
    private Vehicle targetAgent;   
    // накопительный флаг включенных поведенческих состояний
    private BehaviorFlag behavFlag = new BehaviorFlag();
       
    public SteeringBehavior(Vehicle agent) {
        vehicle = agent;             
    }
    
    //////////////START OF BEHAVIORS
    /**
     * Given a target, this behavior returns a steering force which will direct the agent towards the target
     */
    private Vector2D seek(Vector2D TargetPos) {
        Vector2D DesiredVelocity = Vector2D.mul(
                Vector2D.vec2DNormalize(Vector2D.sub(TargetPos, vehicle.getPos())),
                vehicle.getMaxSpeed());
        return Vector2D.sub(DesiredVelocity, vehicle.getVelocity());
    }

    /**
     *  Does the opposite of Seek
     */
    private Vector2D flee(Vector2D TargetPos) {
        Vector2D DesiredVelocity = Vector2D.mul(
                Vector2D.vec2DNormalize(Vector2D.sub(vehicle.getPos(), TargetPos)),
                vehicle.getMaxSpeed());
        return Vector2D.sub(DesiredVelocity, vehicle.getVelocity());
    }

    /**
     * This behavior is similar to seek but it attempts to arrive at the target with a zero velocity
     */
    private Vector2D arrive(Vector2D TargetPos, double deceleration) {
        Vector2D ToTarget = Vector2D.sub(TargetPos, vehicle.getPos());

        //calculate the distance to the target
        double dist = ToTarget.length();
        if (dist > 0) {
            //because Deceleration is enumerated as an int, this value is required to provide fine tweaking of the deceleration..
            final double DecelerationTweaker = 0.3;

            //calculate the speed required to reach the target given the desired deceleration
            double speed = dist / (deceleration * DecelerationTweaker);

            //make sure the velocity does not exceed the max
            speed = Math.min(speed, vehicle.getMaxSpeed());

            //from here proceed just like Seek except we don't need to normalize 
            //the ToTarget vector because we have already gone to the trouble
            //of calculating its length: dist. 
            Vector2D DesiredVelocity = Vector2D.mul(ToTarget, speed / dist);
            return Vector2D.sub(DesiredVelocity, vehicle.getVelocity());
        }
        return new Vector2D(0, 0);
    }

    /**
     *  similar to pursuit except the agent Flees from the estimated future position of the pursuer
     */
    private Vector2D evade(final Vehicle pursuer) {
        // Not necessary to include the check for facing direction this time
        Vector2D ToPursuer = Vector2D.sub(pursuer.getPos(), vehicle.getPos());

        //uncomment the following two lines to have Evade only consider pursuers within a 'threat range'
        final double ThreatRange = 100.0;
        if (ToPursuer.lengthSq() > ThreatRange * ThreatRange) {
            return new Vector2D();
        }
        //the lookahead time is propotional to the distance between the pursuer
        //and the pursuer; and is inversely proportional to the sum of the agents' velocities
        double LookAheadTime = ToPursuer.length() / (vehicle.getMaxSpeed() + pursuer.getSpeed());
        //now flee away from predicted future position of the pursuer
        return flee(Vector2D.add(pursuer.getPos(), Vector2D.mul(pursuer.getVelocity(), LookAheadTime)));
    }

    /**
     * This behavior makes the agent wander about randomly
     */
    private Vector2D wander() {
        //this behavior is dependent on the update rate, so this line must be included when using time independent framerate.
        double JitterThisTimeSlice = Params.WANDER_JITTER_PER_SEC * vehicle.getTimeElapsed();

        double theta = Utils.RandFloat() * Utils.TwoPi;
        //create a vector to a target position on the wander circle
        Vector2D wanderTarget = new Vector2D(Params.WANDER_RAD * Math.cos(theta), Params.WANDER_RAD * Math.sin(theta));
        
        //first, add a small random vector to the target's position
        wanderTarget.add(
                new Vector2D(Utils.RandomClamped() * JitterThisTimeSlice,
                Utils.RandomClamped() * JitterThisTimeSlice));

        //reproject this new vector back on to a unit circle
        wanderTarget.normalize();

        //increase the length of the vector to the same as the radius of the wander circle
        wanderTarget.mul(Params.WANDER_RAD);

        //move the target into a position WanderDist in front of the agent
        Vector2D target = Vector2D.add(wanderTarget, new Vector2D(Params.WANDER_DIST, 0));

        //project the target into world space
        Vector2D Target = Transformation.pointToWorldSpace(target,
                vehicle.getHeading(),
                vehicle.getSide(),
                vehicle.getPos());

        //and steer towards it
        return Vector2D.sub(Target, vehicle.getPos());
    }

    /////////////////////////////////////////////// CALCULATE FORCE METHODS //////////////////////////////////
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
        if (behavFlag.isEvadeOn()) {    
            force = Vector2D.mul(evade(targetAgent), Params.EVADE_WEIGHT);
            if (!accumulateForce(force)) {
                return;
            }
        }
        if (behavFlag.isFleeOn()) {    
            force = Vector2D.mul(flee(vehicle.getWorld().getTargetMove()), Params.FLEE_WEIGHT);
            if (!accumulateForce(force)) {
                return;
            }
        }       
        if (behavFlag.isSeekOn()) {
            force = Vector2D.mul(seek(vehicle.getWorld().getTargetMove()), Params.SEEK_WEIGHT);
            if (!accumulateForce(force)) {
                return;
            }
        }
        if (behavFlag.isArriveOn()) {
            force = Vector2D.mul(arrive(vehicle.getWorld().getTargetMove(), Params.DECELERATION_NORMAL), Params.ARRIVE_WEIGHT);
            if (!accumulateForce(force)) {
                return;
            }
        }
        if (behavFlag.isWanderOn()) {
            force = Vector2D.mul(wander(), Params.WANDER_WEIGHT);
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
        double remainingForce = vehicle.getMaxForce() - steeringForce.length();
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
        if (behavFlag.isEvadeOn()) {    
            steeringForce.add(Vector2D.mul(evade(targetAgent), Params.EVADE_WEIGHT));
        }     
        if (behavFlag.isWanderOn()) {
            steeringForce.add(Vector2D.mul(wander(), Params.WANDER_WEIGHT));
        }
        if (behavFlag.isSeekOn()) {
            steeringForce.add(Vector2D.mul(seek(vehicle.getWorld().getTargetMove()), Params.SEEK_WEIGHT));
        }
        if (behavFlag.isFleeOn()) {
            steeringForce.add(Vector2D.mul(flee(vehicle.getWorld().getTargetMove()), Params.FLEE_WEIGHT));
        }
        if (behavFlag.isArriveOn()) {
            steeringForce.add(Vector2D.mul(arrive(vehicle.getWorld().getTargetMove(), Params.DECELERATION_NORMAL), Params.ARRIVE_WEIGHT));
        }
        steeringForce.truncate(vehicle.getMaxForce());
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
        if (behavFlag.isFleeOn() && Utils.RandFloat() < Params.PR_FLEE) {
            steeringForce.add(Vector2D.mul(flee(vehicle.getWorld().getTargetMove()), Params.FLEE_WEIGHT / Params.PR_FLEE));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(vehicle.getMaxForce());
                return;
            }
        }
        if (behavFlag.isEvadeOn() && Utils.RandFloat() < Params.PR_EVADE) {
            steeringForce.add(Vector2D.mul(evade(targetAgent), Params.EVADE_WEIGHT / Params.PR_EVADE));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(vehicle.getMaxForce());
                return;
            }
        }
        if (behavFlag.isWanderOn() && Utils.RandFloat() < Params.PR_WANDER) {
            steeringForce.add(Vector2D.mul(wander(), Params.WANDER_WEIGHT / Params.PR_WANDER));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(vehicle.getMaxForce());
                return;
            }
        }
        if (behavFlag.isSeekOn() && Utils.RandFloat() < Params.PR_SEEK) {
            steeringForce.add(Vector2D.mul(seek(vehicle.getWorld().getTargetMove()), Params.EVADE_WEIGHT / Params.PR_SEEK));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(vehicle.getMaxForce());
                return;
            }
        }
        if (behavFlag.isArriveOn() && Utils.RandFloat() < Params.PR_ARRIVE) {
            steeringForce.add(Vector2D.mul(arrive(vehicle.getWorld().getTargetMove(), Params.DECELERATION_NORMAL), Params.ARRIVE_WEIGHT / Params.PR_ARRIVE));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(vehicle.getMaxForce());
                return;
            }
        }
        return;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public BehaviorFlag getBehavFlag() {
        return behavFlag;
    }
    
    public void setTergetAgent(Vehicle v) {
        targetAgent = v;
    }

    public void setCalculateForceMethod(CalculateForceMethod method) {
        this.calculateForceMethod = method;
    }  

}
