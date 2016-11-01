
package Chapter3Steering;

import common.D2.Vector2D;
import common.D2.Transformation;
import common.misc.Utils;

class SteeringBehavior {
      
    public static enum SummingMethod {
        WEIGHTED_AVERAGE(0), PRIORITIZED(1), DITHERED(2);      
        SummingMethod(int i) {}
    }
    
    //Arrive makes use of these to determine how quickly a vehicle should decelerate to its target
    private enum Deceleration {
        SLOW(3), NORMAL(2), FAST(1);
        private int dec;

        Deceleration(int d) {
            this.dec = d;
        }

        public int value() {
            return dec;
        }
    }
    
    private enum BehaviorType {
        SEEK(0x00002),
        FLEE(0x00004),
        ARRIVE(0x00008),
        WANDER(0x00010),
        EVADE(0x01000),
        FLOCK(0x08000);
        
        private int flag;

        BehaviorType(int flag) {
            this.flag = flag;
        }

        public int getFlag() {
            return this.flag;
        }
    }
      
    //the radius of the constraining circle for the wander behavior
    final double WANDER_RAD = 1.2;
    //distance the wander circle is projected in front of the agent
    final double WANDER_DIST = 2.0;
    //the maximum amount of displacement along the circle each frame
    final double WANDER_JITTER_PER_SEC = 80.0;
    
    //a pointer to the owner of this instance
    private Vehicle vehicle;
    //the steering force created by the combined effect of all the selected behaviors
    private Vector2D steeringForce = new Vector2D(0, 0);
    //these can be used to keep track of friends, pursuers, or prey
    private Vehicle targetAgent;
    //the current position on the wander circle the agent is attempting to steer towards
    private Vector2D wanderTarget;
    //explained above
    private double wanderJitter;
    private double wanderRadius;
    private double wanderDistance;
    //multipliers. These can be adjusted to effect strength of the  
    //appropriate behavior. Useful to get flocking the way you require for example.
    private double weightWander;
    private double weightSeek;
    private double weightFlee;
    private double weightArrive;
    private double weightEvade;
    //binary flags to indicate whether or not a behavior should be active
    private int flags;    
    //default
    private Deceleration deceleration;
    //is cell space partitioning to be used or not?
    private boolean cellSpaceOn;
    //what type of method is used to sum any active behavior
    private SummingMethod summingMethod;


    public SteeringBehavior(Vehicle agent) {
        vehicle = agent;
        weightWander = ParamLoader.prm.WanderWeight;
        deceleration = Deceleration.NORMAL;
        wanderDistance = WANDER_DIST;
        wanderJitter = WANDER_JITTER_PER_SEC;
        wanderRadius = WANDER_RAD;
        weightSeek = ParamLoader.prm.SeekWeight;
        weightFlee = ParamLoader.prm.FleeWeight;
        weightArrive = ParamLoader.prm.ArriveWeight;
        weightEvade = ParamLoader.prm.EvadeWeight;
        summingMethod = SummingMethod.PRIORITIZED;

        //stuff for the wander behavior
        double theta = Utils.RandFloat() * Utils.TwoPi;
        //create a vector to a target position on the wander circle
        wanderTarget = new Vector2D(wanderRadius * Math.cos(theta), wanderRadius * Math.sin(theta));
    }
    
    //this function tests if a specific bit of m_iFlags is set
    private boolean on(BehaviorType bt) {
        return (flags & bt.getFlag()) == bt.getFlag();
    }

    /**
     * This function calculates how much of its max steering force the 
     * vehicle has left to apply and then applies that amount of the force to add.
     */
    private boolean accumulateForce(Vector2D RunningTot, Vector2D ForceToAdd) {
        //calculate how much steering force the vehicle has used so far
        double MagnitudeSoFar = RunningTot.length();
        //calculate how much steering force remains to be used by this vehicle
        double MagnitudeRemaining = vehicle.getMaxForce() - MagnitudeSoFar;
        //return false if there is no more force left to use
        if (MagnitudeRemaining <= 0.0) {
            return false;
        }

        //calculate the magnitude of the force we want to add
        double MagnitudeToAdd = ForceToAdd.length();

        //if the magnitude of the sum of ForceToAdd and the running total
        //does not exceed the maximum force available to this vehicle, just
        //add together. Otherwise add as much of the ForceToAdd vector is
        //possible without going over the max.
        if (MagnitudeToAdd < MagnitudeRemaining) {
            RunningTot.add(ForceToAdd);
        } else {
            //add it to the steering force
            RunningTot.add(Vector2D.mul(Vector2D.vec2DNormalize(ForceToAdd), MagnitudeRemaining));
        }
        return true;
    }

/////////////////////////////////////////////////////////////////////////////// START OF BEHAVIORS
    /**
     * Given a target, this behavior returns a steering force which will
     *  direct the agent towards the target
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
     * This behavior is similar to seek but it attempts to arrive at the
     *  target with a zero velocity
     */
    private Vector2D arrive(Vector2D TargetPos, Deceleration deceleration) {
        Vector2D ToTarget = Vector2D.sub(TargetPos, vehicle.getPos());

        //calculate the distance to the target
        double dist = ToTarget.length();
        if (dist > 0) {
            //because Deceleration is enumerated as an int, this value is required
            //to provide fine tweaking of the deceleration..
            final double DecelerationTweaker = 0.3;

            //calculate the speed required to reach the target given the desired
            //deceleration
            double speed = dist / ((double) deceleration.value() * DecelerationTweaker);

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
     *  similar to pursuit except the agent Flees from the estimated future
     *  position of the pursuer
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
        //this behavior is dependent on the update rate, so this line must
        //be included when using time independent framerate.
        double JitterThisTimeSlice = wanderJitter * vehicle.getTimeElapsed();

        //first, add a small random vector to the target's position
        wanderTarget.add(
                new Vector2D(Utils.RandomClamped() * JitterThisTimeSlice,
                Utils.RandomClamped() * JitterThisTimeSlice));

        //reproject this new vector back on to a unit circle
        wanderTarget.normalize();

        //increase the length of the vector to the same as the radius of the wander circle
        wanderTarget.mul(wanderRadius);

        //move the target into a position WanderDist in front of the agent
        Vector2D target = Vector2D.add(wanderTarget, new Vector2D(wanderDistance, 0));

        //project the target into world space
        Vector2D Target = Transformation.pointToWorldSpace(target,
                vehicle.getHeading(),
                vehicle.getSide(),
                vehicle.getPos());

        //and steer towards it
        return Vector2D.sub(Target, vehicle.getPos());
    }

    /////////////////////////////////////////////////////////////////////////////// CALCULATE METHODS 
    /**
     * calculates the accumulated steering force according to the method set
     *  in m_SummingMethod
     */
    public Vector2D calculate() {
        //reset the steering force
        steeringForce.zero();
        switch (summingMethod) {
            case WEIGHTED_AVERAGE:
                steeringForce = calculateWeightedSum();
                break;
            case PRIORITIZED:
                steeringForce = calculatePrioritized();
                break;
            case DITHERED:
                steeringForce = calculateDithered();
                break;
            default:
                steeringForce = new Vector2D(0, 0);
        }
        return steeringForce;
    }

    /**
     * returns the forward component of the steering force
     */
    public double forwardComponent() {
        return vehicle.getHeading().dot(steeringForce);
    }

    /**
     * returns the side component of the steering force
     */
    public double sideComponent() {
        return vehicle.getSide().dot(steeringForce);
    }

    /**
     *  this method calls each active steering behavior in order of priority
     *  and acumulates their forces until the max steering force magnitude
     *  is reached, at which time the function returns the steering force 
     *  accumulated to that  point
     */
    private Vector2D calculatePrioritized() {
        Vector2D force = new Vector2D();
        if (on(BehaviorType.EVADE)) {
            assert targetAgent != null : "Evade target not assigned";
            force = Vector2D.mul(evade(targetAgent), weightEvade);
            if (!accumulateForce(steeringForce, force)) {
                return steeringForce;
            }
        }
        if (on(BehaviorType.FLEE)) {
            force = Vector2D.mul(flee(vehicle.getWorld().getCrosshair()), weightFlee);
            if (!accumulateForce(steeringForce, force)) {
                return steeringForce;
            }
        }       
        if (on(BehaviorType.SEEK)) {
            force = Vector2D.mul(seek(vehicle.getWorld().getCrosshair()), weightSeek);
            if (!accumulateForce(steeringForce, force)) {
                return steeringForce;
            }
        }
        if (on(BehaviorType.ARRIVE)) {
            force = Vector2D.mul(arrive(vehicle.getWorld().getCrosshair(), deceleration), weightArrive);
            if (!accumulateForce(steeringForce, force)) {
                return steeringForce;
            }
        }
        if (on(BehaviorType.WANDER)) {
            force = Vector2D.mul(wander(), weightWander);
            if (!accumulateForce(steeringForce, force)) {
                return steeringForce;
            }
        }
        return steeringForce;
    }

    /**
     *  this simply sums up all the active behaviors X their weights and 
     *  truncates the result to the max available steering force before 
     *  returning
     */
    private Vector2D calculateWeightedSum() {
        if (on(BehaviorType.EVADE)) {
            steeringForce.add(Vector2D.mul(evade(targetAgent), weightEvade));
        }     
        if (on(BehaviorType.WANDER)) {
            steeringForce.add(Vector2D.mul(wander(), weightWander));
        }
        if (on(BehaviorType.SEEK)) {
            steeringForce.add(Vector2D.mul(seek(vehicle.getWorld().getCrosshair()), weightSeek));
        }
        if (on(BehaviorType.FLEE)) {
            steeringForce.add(Vector2D.mul(flee(vehicle.getWorld().getCrosshair()), weightFlee));
        }
        if (on(BehaviorType.ARRIVE)) {
            steeringForce.add(Vector2D.mul(arrive(vehicle.getWorld().getCrosshair(), deceleration), weightArrive));
        }
        steeringForce.truncate(vehicle.getMaxForce());
        return steeringForce;
    }

    /**
     *  this method sums up the active behaviors by assigning a probabilty
     *  of being calculated to each behavior. It then tests the first priority
     *  to see if it should be calcukated this simulation-step. If so, it
     *  calculates the steering force resulting from this behavior. If it is
     *  more than zero it returns the force. If zero, or if the behavior is
     *  skipped it continues onto the next priority, and so on.
     *
     *  NOTE: Not all of the behaviors have been implemented in this method,
     *        just a few, so you get the general idea
     */
    private Vector2D calculateDithered() {
        //reset the steering force
        steeringForce.zero();  
        if (on(BehaviorType.FLEE) && Utils.RandFloat() < ParamLoader.prm.prFlee) {
            steeringForce.add(Vector2D.mul(flee(vehicle.getWorld().getCrosshair()), weightFlee / ParamLoader.prm.prFlee));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(vehicle.getMaxForce());
                return steeringForce;
            }
        }
        if (on(BehaviorType.EVADE) && Utils.RandFloat() < ParamLoader.prm.prEvade) {
            assert targetAgent != null : "Evade target not assigned";
            steeringForce.add(Vector2D.mul(evade(targetAgent), weightEvade / ParamLoader.prm.prEvade));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(vehicle.getMaxForce());
                return steeringForce;
            }
        }
        if (on(BehaviorType.WANDER) && Utils.RandFloat() < ParamLoader.prm.prWander) {
            steeringForce.add(Vector2D.mul(wander(), weightWander / ParamLoader.prm.prWander));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(vehicle.getMaxForce());
                return steeringForce;
            }
        }
        if (on(BehaviorType.SEEK) && Utils.RandFloat() < ParamLoader.prm.prSeek) {
            steeringForce.add(Vector2D.mul(seek(vehicle.getWorld().getCrosshair()), weightSeek / ParamLoader.prm.prSeek));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(vehicle.getMaxForce());
                return steeringForce;
            }
        }
        if (on(BehaviorType.ARRIVE) && Utils.RandFloat() < ParamLoader.prm.prArrive) {
            steeringForce.add(Vector2D.mul(arrive(vehicle.getWorld().getCrosshair(), deceleration),
                    weightArrive / ParamLoader.prm.prArrive));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(vehicle.getMaxForce());
                return steeringForce;
            }
        }
        return steeringForce;
    }
    
    public boolean isSpacePartitioningOn() {
        return cellSpaceOn;
    }

    public void wanderOn() {
        flags |= BehaviorType.WANDER.getFlag();
    }

    public void evadeOn(Vehicle v) {
        flags |= BehaviorType.EVADE.getFlag();
        targetAgent = v;
    }

    public void flockingOn() {
        wanderOn();
    }

    public void wanderOff() {
        if (on(BehaviorType.WANDER)) {
            flags ^= BehaviorType.WANDER.getFlag();
        }
    }

    public void flockingOff() {
        wanderOff();
    }

}
