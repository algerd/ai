/**
 * Desc:   class to encapsulate steering behaviors for a Vehicle
  */
package Chapter3Steering;

import common.D2.Vector2D;
import common.D2.Transformation;
import common.D2.Geometry;
import common.D2.Wall2D;
import common.misc.Cgdi;
import common.misc.Utils;
import common.misc.StreamUtilityFunction;
import common.misc.CppToJava;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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
        NONE(0x00000),
        SEEK(0x00002),
        FLEE(0x00004),
        ARRIVE(0x00008),
        WANDER(0x00010),
        COHESIAN(0x00020),
        SEPARATION(0x00040),
        ALLIGNMENT(0x00080),
        OBSTACLE_AVOIDANCE(0x00100),
        WALL_AVOIDANCE(0x00200),
        FPLLOW_PATH(0x00400),
        PURSUIT(0x00800),
        EVADE(0x01000),
        INTERPOSE(0x02000),
        HIDE(0x04000),
        FLOCK(0x08000),
        OFFSET_PURSUIT(0x10000);
        
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
    //used in path following
    final double WAYPOINT_SEEK_DIST = 20;
      
    //a pointer to the owner of this instance
    private Vehicle vehicle;
    //the steering force created by the combined effect of all the selected behaviors
    private Vector2D steeringForce = new Vector2D(0, 0);
    //these can be used to keep track of friends, pursuers, or prey
    private Vehicle targetAgent1;
    private Vehicle targetAgent2;
    //the current target
    private Vector2D target = new Vector2D(0, 0);
    //length of the 'detection box' utilized in obstacle avoidance
    private double boxLength;
    //a vertex buffer to contain the feelers rqd for wall avoidance  
    private List<Vector2D> feelers;
    //the length of the 'feeler/s' used in wall detection
    private double wallDetectionFeelerLength;
    //the current position on the wander circle the agent is attempting to steer towards
    private Vector2D wanderTarget;
    //explained above
    private double wanderJitter;
    private double wanderRadius;
    private double wanderDistance;
    //multipliers. These can be adjusted to effect strength of the  
    //appropriate behavior. Useful to get flocking the way you require for example.
    private double weightSeparation;
    private double weightCohesion;
    private double weightAlignment;
    private double weightWander;
    private double weightObstacleAvoidance;
    private double weightWallAvoidance;
    private double weightSeek;
    private double weightFlee;
    private double weightArrive;
    private double weightPursuit;
    private double weightOffsetPursuit;
    private double weightInterpose;
    private double weightHide;
    private double weightEvade;
    private double weightFollowPath;
    //how far the agent can 'see'
    private double viewDistance;
    //pointer to any current path
    private Path path;
    //the distance (squared) a vehicle has to be from a path waypoint befor it starts seeking to the next waypoint
    private double waypointSeekDistSq;
    //any offset used for formations or offset pursuit
    private Vector2D offset;
    //binary flags to indicate whether or not a behavior should be active
    private int flags;    
    //default
    private Deceleration deceleration;
    //is cell space partitioning to be used or not?
    private boolean cellSpaceOn;
    //what type of method is used to sum any active behavior
    private SummingMethod summingMethod;
    //a vertex buffer rqd for drawing the detection box
    static List<Vector2D> box = new ArrayList(4);

    public SteeringBehavior(Vehicle agent) {
        vehicle = agent;
        boxLength = ParamLoader.prm.MinDetectionBoxLength;
        weightCohesion = ParamLoader.prm.CohesionWeight;
        weightAlignment = ParamLoader.prm.AlignmentWeight;
        weightSeparation = ParamLoader.prm.SeparationWeight;
        weightObstacleAvoidance = ParamLoader.prm.ObstacleAvoidanceWeight;
        weightWander = ParamLoader.prm.WanderWeight;
        weightWallAvoidance = ParamLoader.prm.WallAvoidanceWeight;
        viewDistance = ParamLoader.prm.ViewDistance;
        wallDetectionFeelerLength = ParamLoader.prm.WallDetectionFeelerLength;
        feelers = new ArrayList<Vector2D>(3);
        deceleration = Deceleration.NORMAL;
        wanderDistance = WANDER_DIST;
        wanderJitter = WANDER_JITTER_PER_SEC;
        wanderRadius = WANDER_RAD;
        waypointSeekDistSq = WAYPOINT_SEEK_DIST * WAYPOINT_SEEK_DIST;
        weightSeek = ParamLoader.prm.SeekWeight;
        weightFlee = ParamLoader.prm.FleeWeight;
        weightArrive = ParamLoader.prm.ArriveWeight;
        weightPursuit = ParamLoader.prm.PursuitWeight;
        weightOffsetPursuit = ParamLoader.prm.OffsetPursuitWeight;
        weightInterpose = ParamLoader.prm.InterposeWeight;
        weightHide = ParamLoader.prm.HideWeight;
        weightEvade = ParamLoader.prm.EvadeWeight;
        weightFollowPath = ParamLoader.prm.FollowPathWeight;
        summingMethod = SummingMethod.PRIORITIZED;

        //stuff for the wander behavior
        double theta = Utils.RandFloat() * Utils.TwoPi;
        //create a vector to a target position on the wander circle
        wanderTarget = new Vector2D(wanderRadius * Math.cos(theta), wanderRadius * Math.sin(theta));

        //create a Path
        path = new Path();
        path.LoopOn();
    }
    
    //this function tests if a specific bit of m_iFlags is set
    private boolean on(BehaviorType bt) {
        return (flags & bt.getFlag()) == bt.getFlag();
    }

    /**
     * This function calculates how much of its max steering force the 
     * vehicle has left to apply and then applies that amount of the force to add.
     */
    private boolean AccumulateForce(Vector2D RunningTot, Vector2D ForceToAdd) {
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

    /**
     *  Creates the antenna utilized by WallAvoidance
     */
    private void CreateFeelers() {
        feelers.clear();
        //feeler pointing straight in front
        feelers.add(Vector2D.add(vehicle.getPos(), Vector2D.mul(wallDetectionFeelerLength, vehicle.getHeading())));

        //feeler to left
        Vector2D temp = new Vector2D(vehicle.getHeading());
        Transformation.Vec2DRotateAroundOrigin(temp, Utils.HalfPi * 3.5f);
        feelers.add(Vector2D.add(vehicle.getPos(), Vector2D.mul(wallDetectionFeelerLength / 2.0f, temp)));

        //feeler to right
        temp = new Vector2D(vehicle.getHeading());
        Transformation.Vec2DRotateAroundOrigin(temp, Utils.HalfPi * 0.5f);
        feelers.add(Vector2D.add(vehicle.getPos(), Vector2D.mul(wallDetectionFeelerLength / 2.0f, temp)));
    }

/////////////////////////////////////////////////////////////////////////////// START OF BEHAVIORS
    /**
     * Given a target, this behavior returns a steering force which will
     *  direct the agent towards the target
     */
    private Vector2D Seek(Vector2D TargetPos) {
        Vector2D DesiredVelocity = Vector2D.mul(
                Vector2D.vec2DNormalize(Vector2D.sub(TargetPos, vehicle.getPos())),
                vehicle.getMaxSpeed());
        return Vector2D.sub(DesiredVelocity, vehicle.getVelocity());
    }

    /**
     *  Does the opposite of Seek
     */
    private Vector2D Flee(Vector2D TargetPos) {
        Vector2D DesiredVelocity = Vector2D.mul(
                Vector2D.vec2DNormalize(Vector2D.sub(vehicle.getPos(), TargetPos)),
                vehicle.getMaxSpeed());
        return Vector2D.sub(DesiredVelocity, vehicle.getVelocity());
    }

    /**
     * This behavior is similar to seek but it attempts to arrive at the
     *  target with a zero velocity
     */
    private Vector2D Arrive(Vector2D TargetPos, Deceleration deceleration) {
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
     *  this behavior creates a force that steers the agent towards the 
     *  evader
     */
    private Vector2D Pursuit(final Vehicle evader) {
        //if the evader is ahead and facing the agent then we can just seek
        //for the evader's current position.
        Vector2D ToEvader = Vector2D.sub(evader.getPos(), vehicle.getPos());

        double RelativeHeading = vehicle.getHeading().dot(evader.getHeading());

        if ((ToEvader.dot(vehicle.getHeading()) > 0)
                && (RelativeHeading < -0.95)) //acos(0.95)=18 degs
        {
            return Seek(evader.getPos());
        }

        //Not considered ahead so we predict where the evader will be.
        //the lookahead time is propotional to the distance between the evader
        //and the pursuer; and is inversely proportional to the sum of the
        //agent's velocities
        double LookAheadTime = ToEvader.length() / (vehicle.getMaxSpeed() + evader.getSpeed());
        //now seek to the predicted future position of the evader
        return Seek(Vector2D.add(evader.getPos(), Vector2D.mul(evader.getVelocity(), LookAheadTime)));
    }

    /**
     *  similar to pursuit except the agent Flees from the estimated future
     *  position of the pursuer
     */
    private Vector2D Evade(final Vehicle pursuer) {
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
        return Flee(Vector2D.add(pursuer.getPos(), Vector2D.mul(pursuer.getVelocity(), LookAheadTime)));
    }

    /**
     * This behavior makes the agent wander about randomly
     */
    private Vector2D Wander() {
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
        Vector2D Target = Transformation.PointToWorldSpace(target,
                vehicle.getHeading(),
                vehicle.getSide(),
                vehicle.getPos());

        //and steer towards it
        return Vector2D.sub(Target, vehicle.getPos());
    }

    /**
     *  Given a vector of obstacles, this method returns a steering force
     *  that will prevent the agent colliding with the closest obstacle
     */
    private Vector2D ObstacleAvoidance(List<BaseGameEntity> obstacles) {
        //the detection box length is proportional to the agent's velocity
        boxLength = ParamLoader.prm.MinDetectionBoxLength + (vehicle.getSpeed() / vehicle.getMaxSpeed()) * ParamLoader.prm.MinDetectionBoxLength;

        //tag all obstacles within range of the box for processing
        vehicle.getWorld().tagObstaclesWithinViewRange(vehicle, boxLength);

        //this will keep track of the closest intersecting obstacle (CIB)
        BaseGameEntity ClosestIntersectingObstacle = null;

        //this will be used to track the distance to the CIB
        double DistToClosestIP = Utils.MaxDouble;

        //this will record the transformed local coordinates of the CIB
        Vector2D LocalPosOfClosestObstacle = new Vector2D();

        ListIterator<BaseGameEntity> it = obstacles.listIterator();

        while (it.hasNext()) {
            //if the obstacle has been tagged within range proceed
            BaseGameEntity curOb = it.next();
            if (curOb.isTagged()) {
                //calculate this obstacle's position in local space
                Vector2D LocalPos = Transformation.PointToLocalSpace(curOb.getPos(),
                        vehicle.getHeading(),
                        vehicle.getSide(),
                        vehicle.getPos());

                //if the local position has a negative x value then it must lay
                //behind the agent. (in which case it can be ignored)
                if (LocalPos.x >= 0) {
                    //if the distance from the x axis to the object's position is less
                    //than its radius + half the width of the detection box then there
                    //is a potential intersection.
                    double ExpandedRadius = curOb.getBoundingRadius() + vehicle.getBoundingRadius();

                    if (Math.abs(LocalPos.y) < ExpandedRadius) {
                        //now to do a line/circle intersection test. The center of the 
                        //circle is represented by (cX, cY). The intersection points are 
                        //given by the formula x = cX +/-sqrt(r^2-cY^2) for y=0. 
                        //We only need to look at the smallest positive value of x because
                        //that will be the closest point of intersection.
                        double cX = LocalPos.x;
                        double cY = LocalPos.y;

                        //we only need to calculate the sqrt part of the above equation once
                        double SqrtPart = Math.sqrt(ExpandedRadius * ExpandedRadius - cY * cY);
                        double ip = cX - SqrtPart;
                        if (ip <= 0.0) {
                            ip = cX + SqrtPart;
                        }
                        //test to see if this is the closest so far. If it is keep a
                        //record of the obstacle and its local coordinates
                        if (ip < DistToClosestIP) {
                            DistToClosestIP = ip;
                            ClosestIntersectingObstacle = curOb;
                            LocalPosOfClosestObstacle = LocalPos;
                        }
                    }
                }
            }
        }

        //if we have found an intersecting obstacle, calculate a steering force away from it
        Vector2D SteeringForce = new Vector2D();

        if (ClosestIntersectingObstacle != null) {
            //the closer the agent is to an object, the stronger the 
            //steering force should be
            double multiplier = 1.0 + (boxLength - LocalPosOfClosestObstacle.x) / boxLength;

            //calculate the lateral force
            SteeringForce.y = (ClosestIntersectingObstacle.getBoundingRadius() - LocalPosOfClosestObstacle.y) * multiplier;

            //apply a braking force proportional to the obstacles distance from the vehicle. 
            final double BrakingWeight = 0.2;

            SteeringForce.x = (ClosestIntersectingObstacle.getBoundingRadius() - LocalPosOfClosestObstacle.x) * BrakingWeight;
        }

        //finally, convert the steering vector from local to world space
        return Transformation.VectorToWorldSpace(SteeringForce, vehicle.getHeading(), vehicle.getSide());
    }

    /**
     * This returns a steering force that will keep the agent away from any
     *  walls it may encounter
     */
    private Vector2D WallAvoidance(final List<Wall2D> walls) {
        //the feelers are contained in a std::vector, m_Feelers
        CreateFeelers();
        double DistToThisIP = 0.0;
        double DistToClosestIP = Utils.MaxDouble;
        //this will hold an index into the vector of walls
        int ClosestWall = -1;

        Vector2D SteeringForce = new Vector2D();
        Vector2D point = new Vector2D(); //used for storing temporary info
        Vector2D ClosestPoint = new Vector2D();  //holds the closest intersection point

        //examine each feeler in turn
        for (int flr = 0; flr < feelers.size(); ++flr) {
            //run through each wall checking for any intersection points
            CppToJava.DoubleRef DistToThisIPRef = new CppToJava.DoubleRef(DistToThisIP);
            for (int w = 0; w < walls.size(); ++w) {
                if (Geometry.LineIntersection2D(vehicle.getPos(),
                        feelers.get(flr),
                        walls.get(w).From(),
                        walls.get(w).To(),
                        DistToThisIPRef,
                        point)) {
                    DistToThisIP = DistToThisIPRef.toDouble();
                    //is this the closest found so far? If so keep a record
                    if (DistToThisIP < DistToClosestIP) {
                        DistToClosestIP = DistToThisIP;
                        ClosestWall = w;
                        ClosestPoint = point;
                    }
                }
            }//next wall
            //if an intersection point has been detected, calculate a force that will direct the agent away
            if (ClosestWall >= 0) {
                //calculate by what distance the projected position of the agent will overshoot the wall
                Vector2D OverShoot = Vector2D.sub(feelers.get(flr), ClosestPoint);
                //create a force in the direction of the wall normal, with a magnitude of the overshoot
                SteeringForce = Vector2D.mul(walls.get(ClosestWall).Normal(), OverShoot.length());
            }
        }//next feeler
        return SteeringForce;
    }

    /**
     * this calculates a force repelling from the other neighbors
     */
    Vector2D Separation(final List<Vehicle> neighbors) {
        Vector2D SteeringForce = new Vector2D();
        for (int a = 0; a < neighbors.size(); ++a) {
            //make sure this agent isn't included in the calculations and that
            //the agent being examined is close enough. ***also make sure it doesn't include the evade target ***
            if ((neighbors.get(a) != vehicle) && neighbors.get(a).isTagged() && (neighbors.get(a) != targetAgent1)) {
                Vector2D ToAgent = Vector2D.sub(vehicle.getPos(), neighbors.get(a).getPos());
                //scale the force inversely proportional to the agents distance from its neighbor.
                SteeringForce.add(Vector2D.div(Vector2D.vec2DNormalize(ToAgent), ToAgent.length()));
            }
        }
        return SteeringForce;
    }

    /**
     * returns a force that attempts to align this agents heading with that
     * of its neighbors
     */
    private Vector2D Alignment(final List<Vehicle> neighbors) {
        //used to record the average heading of the neighbors
        Vector2D AverageHeading = new Vector2D();
        //used to count the number of vehicles in the neighborhood
        int NeighborCount = 0;
        //iterate through all the tagged vehicles and sum their heading vectors  
        for (int a = 0; a < neighbors.size(); ++a) {
            //make sure *this* agent isn't included in the calculations and that
            //the agent being examined  is close enough ***also make sure it doesn't include any evade target ***
            if ((neighbors.get(a) != vehicle) && neighbors.get(a).isTagged()
                    && (neighbors.get(a) != targetAgent1)) {
                AverageHeading.add(neighbors.get(a).getHeading());
                ++NeighborCount;
            }
        }
        //if the neighborhood contained one or more vehicles, average their heading vectors.
        if (NeighborCount > 0) {
            AverageHeading.div((double) NeighborCount);
            AverageHeading.sub(vehicle.getHeading());
        }
        return AverageHeading;
    }

    /**
     * returns a steering force that attempts to move the agent towards the
     * center of mass of the agents in its immediate area
     */
    private Vector2D Cohesion(final List<Vehicle> neighbors) {
        //first find the center of mass of all the agents
        Vector2D CenterOfMass = new Vector2D(), SteeringForce = new Vector2D();
        int NeighborCount = 0;
        //iterate through the neighbors and sum up all the position vectors
        for (int a = 0; a < neighbors.size(); ++a) {
            //make sure *this* agent isn't included in the calculations and that
            //the agent being examined is close enough ***also make sure it doesn't include the evade target ***
            if ((neighbors.get(a) != vehicle) && neighbors.get(a).isTagged()
                    && (neighbors.get(a) != targetAgent1)) {
                CenterOfMass.add(neighbors.get(a).getPos());
                ++NeighborCount;
            }
        }

        if (NeighborCount > 0) {
            //the center of mass is the average of the sum of positions
            CenterOfMass.div((double) NeighborCount);
            //now seek towards that position
            SteeringForce = Seek(CenterOfMass);
        }
        //the magnitude of cohesion is usually much larger than separation or allignment so it usually helps to normalize it.
        return Vector2D.vec2DNormalize(SteeringForce);
    }

    /* NOTE: the next three behaviors are the same as the above three, except
    that they use a cell-space partition to find the neighbors
     */
    /**
     * this calculates a force repelling from the other neighbors
     * USES SPACIAL PARTITIONING
     */
    private Vector2D SeparationPlus(final List<Vehicle> neighbors) {
        Vector2D SteeringForce = new Vector2D();

        //iterate through the neighbors and sum up all the position vectors
        for (BaseGameEntity pV = vehicle.getWorld().getCellSpace().begin();
                !vehicle.getWorld().getCellSpace().end();
                pV = vehicle.getWorld().getCellSpace().next()) {
            //make sure this agent isn't included in the calculations and that the agent being examined is close enough
            if (pV != vehicle) {
                Vector2D ToAgent = Vector2D.sub(vehicle.getPos(), pV.getPos());
                //scale the force inversely proportional to the agents distance from its neighbor.
                SteeringForce.add(Vector2D.div(Vector2D.vec2DNormalize(ToAgent), ToAgent.length()));
            }
        }
        return SteeringForce;
    }

    /**
     * returns a force that attempts to align this agents heading with that
     * of its neighbors
     *  USES SPACIAL PARTITIONING
     */
    private Vector2D AlignmentPlus(final List<Vehicle> neighbors) {
        //This will record the average heading of the neighbors
        Vector2D AverageHeading = new Vector2D();
        //This count the number of vehicles in the neighborhood
        double NeighborCount = 0.0;
        //iterate through the neighbors and sum up all the position vectors
        for (MovingEntity pV = vehicle.getWorld().getCellSpace().begin();
                !vehicle.getWorld().getCellSpace().end();
                pV = vehicle.getWorld().getCellSpace().next()) {
            //make sure *this* agent isn't included in the calculations and that the agent being examined  is close enough
            if (pV != vehicle) {
                AverageHeading.add(pV.getHeading());
                ++NeighborCount;
            }
        }
        //if the neighborhood contained one or more vehicles, average their heading vectors.
        if (NeighborCount > 0.0) {
            AverageHeading.div(NeighborCount);
            AverageHeading.sub(vehicle.getHeading());
        }
        return AverageHeading;
    }

    /**
     * returns a steering force that attempts to move the agent towards the
     * center of mass of the agents in its immediate area
     * USES SPACIAL PARTITIONING
     */
    private Vector2D CohesionPlus(final List<Vehicle> neighbors) {
        //first find the center of mass of all the agents
        Vector2D CenterOfMass = new Vector2D(), SteeringForce = new Vector2D();
        int NeighborCount = 0;
        //iterate through the neighbors and sum up all the position vectors
        for (BaseGameEntity pV = vehicle.getWorld().getCellSpace().begin();
                !vehicle.getWorld().getCellSpace().end();
                pV = vehicle.getWorld().getCellSpace().next()) {
            //make sure *this* agent isn't included in the calculations and that the agent being examined is close enough
            if (pV != vehicle) {
                CenterOfMass.add(pV.getPos());
                ++NeighborCount;
            }
        }
        if (NeighborCount > 0) {
            //the center of mass is the average of the sum of positions
            CenterOfMass.div((double) NeighborCount);
            //now seek towards that position
            SteeringForce = Seek(CenterOfMass);
        }
        //the magnitude of cohesion is usually much larger than separation or allignment so it usually helps to normalize it.
        return Vector2D.vec2DNormalize(SteeringForce);
    }

    /**
     * Given two agents, this method returns a force that attempts to 
     * position the vehicle between them
     */
    private Vector2D Interpose(final Vehicle AgentA, final Vehicle AgentB) {
        //first we need to figure out where the two agents are going to be at 
        //time T in the future. This is approximated by determining the time
        //taken to reach the mid way point at the current time at at max speed.
        Vector2D MidPoint = Vector2D.div(Vector2D.add(AgentA.getPos(), AgentB.getPos()), 2.0);

        double TimeToReachMidPoint = Vector2D.vec2DDistance(vehicle.getPos(), MidPoint) / vehicle.getMaxSpeed();

        //now we have T, we assume that agent A and agent B will continue on a
        //straight trajectory and extrapolate to get their future positions
        Vector2D APos = Vector2D.add(AgentA.getPos(), Vector2D.mul(AgentA.getVelocity(), TimeToReachMidPoint));
        Vector2D BPos = Vector2D.add(AgentB.getPos(), Vector2D.mul(AgentB.getVelocity(), TimeToReachMidPoint));

        //calculate the mid point of these predicted positions
        MidPoint = Vector2D.div(Vector2D.add(APos, BPos), 2.0);
        //then steer to Arrive at it
        return Arrive(MidPoint, Deceleration.FAST);
    }

    private Vector2D Hide(final Vehicle hunter, final List<BaseGameEntity> obstacles) {
        double DistToClosest = Utils.MaxDouble;
        Vector2D BestHidingSpot = new Vector2D();
        ListIterator<BaseGameEntity> it = obstacles.listIterator();
        BaseGameEntity closest;

        while (it.hasNext()) {
            BaseGameEntity curOb = it.next();
            //calculate the position of the hiding spot for this obstacle
            Vector2D HidingSpot = GetHidingPosition(curOb.getPos(),
                    curOb.getBoundingRadius(),
                    hunter.getPos());

            //work in distance-squared space to find the closest hiding spot to the agent
            double dist = Vector2D.vec2DDistanceSq(HidingSpot, vehicle.getPos());
            if (dist < DistToClosest) {
                DistToClosest = dist;
                BestHidingSpot = HidingSpot;
                closest = curOb;
            }
        }
        //if no suitable obstacles found then Evade the hunter
        if (DistToClosest == Utils.MaxFloat) {
            return Evade(hunter);
        }
        //else use Arrive on the hiding spot
        return Arrive(BestHidingSpot, Deceleration.FAST);
    }

    /**
     *  Given the position of a hunter, and the position and radius of
     *  an obstacle, this method calculates a position DistanceFromBoundary 
     *  away from its bounding radius and directly opposite the hunter
     */
    private Vector2D GetHidingPosition(final Vector2D posOb,
            final double radiusOb,
            final Vector2D posHunter) {
        //calculate how far away the agent is to be from the chosen obstacle's bounding radius
        final double DistanceFromBoundary = 30.0;
        double DistAway = radiusOb + DistanceFromBoundary;

        //calculate the heading toward the object from the hunter
        Vector2D ToOb = Vector2D.vec2DNormalize(Vector2D.sub(posOb, posHunter));

        //scale it to size and add to the obstacles position to get the hiding spot.
        return Vector2D.add(Vector2D.mul(ToOb, DistAway), posOb);
    }

    /**
     *  Given a series of Vector2Ds, this method produces a force that will
     *  move the agent along the waypoints in order. The agent uses the
     * 'Seek' behavior to move to the next waypoint - unless it is the last
     *  waypoint, in which case it 'Arrives'
     */
    private Vector2D FollowPath() {
        //move to next target if close enough to current target (working in distance squared space)
        if (Vector2D.vec2DDistanceSq(path.CurrentWaypoint(), vehicle.getPos()) < waypointSeekDistSq) {
            path.SetNextWaypoint();
        }
        return (!path.Finished()) ? Seek(path.CurrentWaypoint()) : Arrive(path.CurrentWaypoint(), Deceleration.NORMAL);
    }

    /**
     * Produces a steering force that keeps a vehicle at a specified offset from a leader vehicle
     */
    private Vector2D OffsetPursuit(final Vehicle leader,
            final Vector2D offset) {
        //calculate the offset's position in world space
        Vector2D WorldOffsetPos = Transformation.PointToWorldSpace(offset,
                leader.getHeading(),
                leader.getSide(),
                leader.getPos());

        Vector2D ToOffset = Vector2D.sub(WorldOffsetPos, vehicle.getPos());

        //the lookahead time is propotional to the distance between the leader
        //and the pursuer; and is inversely proportional to the sum of both agent's velocities
        double LookAheadTime = ToOffset.length() / (vehicle.getMaxSpeed() + leader.getSpeed());

        //now Arrive at the predicted future position of the offset
        return Arrive(Vector2D.add(WorldOffsetPos, Vector2D.mul(leader.getVelocity(), LookAheadTime)), Deceleration.FAST);
    }

    /**
     * renders visual aids and info for seeing how each behavior is calculated
     */
    public void RenderAids() {
        Cgdi.gdi.TransparentText();
        Cgdi.gdi.TextColor(Cgdi.grey);
        int NextSlot = Cgdi.gdi.fontHeight();
        int SlotSize = 20;

        if (CppToJava.KEYDOWN(KeyEvent.VK_INSERT)) {
            vehicle.setMaxForce(vehicle.getMaxForce() + 1000.0f * vehicle.getTimeElapsed());
        }
        if (CppToJava.KEYDOWN(KeyEvent.VK_DELETE)) {
            if (vehicle.getMaxForce() > 0.2f) {
                vehicle.setMaxForce(vehicle.getMaxForce() - 1000.0f * vehicle.getTimeElapsed());
            }
        }
        if (CppToJava.KEYDOWN(KeyEvent.VK_HOME)) {
            vehicle.setMaxSpeed(vehicle.getMaxSpeed() + 50.0f * vehicle.getTimeElapsed());
        }
        if (CppToJava.KEYDOWN(KeyEvent.VK_END)) {
            if (vehicle.getMaxSpeed() > 0.2f) {
                vehicle.setMaxSpeed(vehicle.getMaxSpeed() - 50.0f * vehicle.getTimeElapsed());
            }
        }
        if (vehicle.getMaxForce() < 0) {
            vehicle.setMaxForce(0.0f);
        }
        if (vehicle.getMaxSpeed() < 0) {
            vehicle.setMaxSpeed(0.0f);
        }        
        if (vehicle.getId() == 0) {
            Cgdi.gdi.TextAtPos(5, NextSlot, "MaxForce(Ins/Del):");
            Cgdi.gdi.TextAtPos(160, NextSlot, StreamUtilityFunction.ttos(vehicle.getMaxForce() / ParamLoader.prm.SteeringForceTweaker));
            NextSlot += SlotSize;
        }
        if (vehicle.getId() == 0) {
            Cgdi.gdi.TextAtPos(5, NextSlot, "MaxSpeed(Home/End):");
            Cgdi.gdi.TextAtPos(160, NextSlot, StreamUtilityFunction.ttos(vehicle.getMaxSpeed()));
            NextSlot += SlotSize;
        }
        //render the steering force
        if (vehicle.getWorld().isShowSteeringForce()) {
            Cgdi.gdi.RedPen();
            Vector2D F = Vector2D.mul((Vector2D.div(steeringForce, ParamLoader.prm.SteeringForceTweaker)), ParamLoader.prm.VehicleScale);
            Cgdi.gdi.Line(vehicle.getPos(), Vector2D.add(vehicle.getPos(), F));
        }
        //render wander stuff if relevant
        if (on(BehaviorType.WANDER) && vehicle.getWorld().isShowWanderCircle()) {
            if (CppToJava.KEYDOWN('F')) {
                wanderJitter += 1.0f * vehicle.getTimeElapsed();
                wanderJitter = Utils.clamp(wanderJitter, 0.0, 100.0);
            }
            if (CppToJava.KEYDOWN('V')) {
                wanderJitter -= 1.0f * vehicle.getTimeElapsed();
                wanderJitter = Utils.clamp(wanderJitter, 0.0, 100.0);
            }
            if (CppToJava.KEYDOWN('G')) {
                wanderDistance += 2.0f * vehicle.getTimeElapsed();
                wanderDistance = Utils.clamp(wanderDistance, 0.0, 50.0);
            }
            if (CppToJava.KEYDOWN('B')) {
                wanderDistance -= 2.0f * vehicle.getTimeElapsed();
                wanderDistance = Utils.clamp(wanderDistance, 0.0, 50.0);
            }
            if (CppToJava.KEYDOWN('H')) {
                wanderRadius += 2.0f * vehicle.getTimeElapsed();
                wanderRadius = Utils.clamp(wanderRadius, 0.0, 100.0);
            }
            if (CppToJava.KEYDOWN('N')) {
                wanderRadius -= 2.0f * vehicle.getTimeElapsed();
                wanderRadius = Utils.clamp(wanderRadius, 0.0, 100.0);
            }
            if (vehicle.getId() == 0) {
                Cgdi.gdi.TextAtPos(5, NextSlot, "Jitter(F/V): ");
                Cgdi.gdi.TextAtPos(160, NextSlot, StreamUtilityFunction.ttos(wanderJitter));
                NextSlot += SlotSize;
            }
            if (vehicle.getId() == 0) {
                Cgdi.gdi.TextAtPos(5, NextSlot, "Distance(G/B): ");
                Cgdi.gdi.TextAtPos(160, NextSlot, StreamUtilityFunction.ttos(wanderDistance));
                NextSlot += SlotSize;
            }
            if (vehicle.getId() == 0) {
                Cgdi.gdi.TextAtPos(5, NextSlot, "Radius(H/N): ");
                Cgdi.gdi.TextAtPos(160, NextSlot, StreamUtilityFunction.ttos(wanderRadius));
                NextSlot += SlotSize;
            }
            //calculate the center of the wander circle
            Vector2D m_vTCC = Transformation.PointToWorldSpace(new Vector2D(wanderDistance * vehicle.getBoundingRadius(), 0),
                    vehicle.getHeading(),
                    vehicle.getSide(),
                    vehicle.getPos());
            //draw the wander circle
            Cgdi.gdi.GreenPen();
            Cgdi.gdi.HollowBrush();
            Cgdi.gdi.Circle(m_vTCC, wanderRadius * vehicle.getBoundingRadius());

            //draw the wander target
            Cgdi.gdi.RedPen();
            Cgdi.gdi.Circle(Transformation.PointToWorldSpace(Vector2D.mul(Vector2D.add(wanderTarget, new Vector2D(wanderDistance, 0)), vehicle.getBoundingRadius()),
                    vehicle.getHeading(),
                    vehicle.getSide(),
                    vehicle.getPos()), 3);
        }
        //render the detection box if relevant
        if (vehicle.getWorld().isShowDetectionBox()) {
            Cgdi.gdi.GreyPen();
            double length = ParamLoader.prm.MinDetectionBoxLength
                    + (vehicle.getSpeed() / vehicle.getMaxSpeed())
                    * ParamLoader.prm.MinDetectionBoxLength;

            //verts for the detection box buffer
            box.clear();
            box.add(new Vector2D(0, vehicle.getBoundingRadius()));
            box.add(new Vector2D(length, vehicle.getBoundingRadius()));
            box.add(new Vector2D(length, -vehicle.getBoundingRadius()));
            box.add(new Vector2D(0, -vehicle.getBoundingRadius()));
            if (!vehicle.isSmoothingOn()) {
                box = Transformation.WorldTransform(box, vehicle.getPos(), vehicle.getHeading(), vehicle.getSide());
                Cgdi.gdi.ClosedShape(box);
            } else {
                box = Transformation.WorldTransform(box, vehicle.getPos(), vehicle.getSmoothedHeading(), vehicle.getSmoothedHeading().perp());
                Cgdi.gdi.ClosedShape(box);
            }
            //////////////////////////////////////////////////////////////////////////
            //the detection box length is proportional to the agent's velocity
            boxLength = ParamLoader.prm.MinDetectionBoxLength
                    + (vehicle.getSpeed() / vehicle.getMaxSpeed())
                    * ParamLoader.prm.MinDetectionBoxLength;

            //tag all obstacles within range of the box for processing
            vehicle.getWorld().tagObstaclesWithinViewRange(vehicle, boxLength);

            //this will keep track of the closest intersecting obstacle (CIB)
            BaseGameEntity ClosestIntersectingObstacle = null;

            //this will be used to track the distance to the CIB
            double DistToClosestIP = Utils.MaxDouble;

            //this will record the transformed local coordinates of the CIB
            Vector2D LocalPosOfClosestObstacle = new Vector2D();

            ListIterator<BaseGameEntity> it = vehicle.getWorld().getObstacles().listIterator();

            while (it.hasNext()) {
                BaseGameEntity curOb = it.next();
                //if the obstacle has been tagged within range proceed
                if (curOb.isTagged()) {
                    //calculate this obstacle's position in local space
                    Vector2D LocalPos = Transformation.PointToLocalSpace(curOb.getPos(),
                            vehicle.getHeading(),
                            vehicle.getSide(),
                            vehicle.getPos());

                    //if the local position has a negative x value then it must lay
                    //behind the agent. (in which case it can be ignored)
                    if (LocalPos.x >= 0) {
                        //if the distance from the x axis to the object's position is less
                        //than its radius + half the width of the detection box then there
                        //is a potential intersection.
                        if (Math.abs(LocalPos.y) < (curOb.getBoundingRadius() + vehicle.getBoundingRadius())) {
                            Cgdi.gdi.ThickRedPen();
                            Cgdi.gdi.ClosedShape(box);
                        }
                    }
                }
            }
        }

        //render the wall avoidnace feelers
        if (on(BehaviorType.WALL_AVOIDANCE) && vehicle.getWorld().isShowFeelers()) {
            Cgdi.gdi.OrangePen();
            for (int flr = 0; flr < feelers.size(); ++flr) {
                Cgdi.gdi.Line(vehicle.getPos(), feelers.get(flr));
            }
        }
        //render path info
        if (on(BehaviorType.FPLLOW_PATH) && vehicle.getWorld().isShowPath()) {
            path.Render();
        }
        if (on(BehaviorType.SEPARATION)) {
            if (vehicle.getId() == 0) {
                Cgdi.gdi.TextAtPos(5, NextSlot, "Separation(S/X):");
                Cgdi.gdi.TextAtPos(160, NextSlot, StreamUtilityFunction.ttos(weightSeparation / ParamLoader.prm.SteeringForceTweaker));
                NextSlot += SlotSize;
            }
            if (CppToJava.KEYDOWN('S')) {
                weightSeparation += 200 * vehicle.getTimeElapsed();
                weightSeparation = Utils.clamp(weightSeparation, 0.0, 50.0 * ParamLoader.prm.SteeringForceTweaker);
            }
            if (CppToJava.KEYDOWN('X')) {
                weightSeparation -= 200 * vehicle.getTimeElapsed();
                weightSeparation = Utils.clamp(weightSeparation, 0.0, 50.0 * ParamLoader.prm.SteeringForceTweaker);
            }
        }
        if (on(BehaviorType.ALLIGNMENT)) {
            if (vehicle.getId() == 0) {
                Cgdi.gdi.TextAtPos(5, NextSlot, "Alignment(A/Z):");
                Cgdi.gdi.TextAtPos(160, NextSlot, StreamUtilityFunction.ttos(weightAlignment / ParamLoader.prm.SteeringForceTweaker));
                NextSlot += SlotSize;
            }

            if (CppToJava.KEYDOWN('A')) {
                weightAlignment += 200 * vehicle.getTimeElapsed();
                weightAlignment = Utils.clamp(weightAlignment, 0.0, 50.0 * ParamLoader.prm.SteeringForceTweaker);
            }
            if (CppToJava.KEYDOWN('Z')) {
                weightAlignment -= 200 * vehicle.getTimeElapsed();
                weightAlignment = Utils.clamp(weightAlignment, 0.0, 50.0 * ParamLoader.prm.SteeringForceTweaker);
            }
        }
        if (on(BehaviorType.COHESIAN)) {
            if (vehicle.getId() == 0) {
                Cgdi.gdi.TextAtPos(5, NextSlot, "Cohesion(D/C):");
                Cgdi.gdi.TextAtPos(160, NextSlot, StreamUtilityFunction.ttos(weightCohesion / ParamLoader.prm.SteeringForceTweaker));
                NextSlot += SlotSize;
            }
            if (CppToJava.KEYDOWN('D')) {
                weightCohesion += 200 * vehicle.getTimeElapsed();
                weightCohesion = Utils.clamp(weightCohesion, 0.0, 50.0 * ParamLoader.prm.SteeringForceTweaker);
            }
            if (CppToJava.KEYDOWN('C')) {
                weightCohesion -= 200 * vehicle.getTimeElapsed();
                weightCohesion = Utils.clamp(weightCohesion, 0.0, 50.0 * ParamLoader.prm.SteeringForceTweaker);
            }
        }
        if (on(BehaviorType.FPLLOW_PATH)) {
            double sd = Math.sqrt(waypointSeekDistSq);
            if (vehicle.getId() == 0) {
                Cgdi.gdi.TextAtPos(5, NextSlot, "SeekDistance(D/C):");
                Cgdi.gdi.TextAtPos(160, NextSlot, StreamUtilityFunction.ttos(sd));
                NextSlot += SlotSize;
            }
            if (CppToJava.KEYDOWN('D')) {
                waypointSeekDistSq += 1.0;
            }
            if (CppToJava.KEYDOWN('C')) {
                waypointSeekDistSq -= 1.0;
                waypointSeekDistSq = Utils.clamp(waypointSeekDistSq, 0.0, 400.0);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////// CALCULATE METHODS 
    /**
     * calculates the accumulated steering force according to the method set
     *  in m_SummingMethod
     */
    public Vector2D calculate() {
        //reset the steering force
        steeringForce.zero();

        //use space partitioning to calculate the neighbours of this vehicle if switched on. If not, use the standard tagging system
        if (!isSpacePartitioningOn()) {
            //tag neighbors if any of the following 3 group behaviors are switched on
            if (on(BehaviorType.SEPARATION) || on(BehaviorType.ALLIGNMENT) || on(BehaviorType.COHESIAN)) {
                vehicle.getWorld().tagVehiclesWithinViewRange(vehicle, viewDistance);
            }
        } else {
            //calculate neighbours in cell-space if any of the following 3 group behaviors are switched on
            if (on(BehaviorType.SEPARATION) || on(BehaviorType.ALLIGNMENT) || on(BehaviorType.COHESIAN)) {
                vehicle.getWorld().getCellSpace().calculateNeighbors(vehicle.getPos(), viewDistance);
            }
        }

        switch (summingMethod) {
            case WEIGHTED_AVERAGE:
                steeringForce = CalculateWeightedSum();
                break;
            case PRIORITIZED:
                steeringForce = CalculatePrioritized();
                break;
            case DITHERED:
                steeringForce = CalculateDithered();
                break;
            default:
                steeringForce = new Vector2D(0, 0);
        }
        return steeringForce;
    }

    /**
     * returns the forward component of the steering force
     */
    public double ForwardComponent() {
        return vehicle.getHeading().dot(steeringForce);
    }

    /**
     * returns the side component of the steering force
     */
    public double SideComponent() {
        return vehicle.getSide().dot(steeringForce);
    }

    /**
     *  this method calls each active steering behavior in order of priority
     *  and acumulates their forces until the max steering force magnitude
     *  is reached, at which time the function returns the steering force 
     *  accumulated to that  point
     */
    private Vector2D CalculatePrioritized() {
        Vector2D force = new Vector2D();
        if (on(BehaviorType.WALL_AVOIDANCE)) {
            force = Vector2D.mul(WallAvoidance(vehicle.getWorld().getWalls()), weightWallAvoidance);
            if (!AccumulateForce(steeringForce, force)) {
                return steeringForce;
            }
        }
        if (on(BehaviorType.OBSTACLE_AVOIDANCE)) {
            force = Vector2D.mul(ObstacleAvoidance(vehicle.getWorld().getObstacles()), weightObstacleAvoidance);
            if (!AccumulateForce(steeringForce, force)) {
                return steeringForce;
            }
        }
        if (on(BehaviorType.EVADE)) {
            assert targetAgent1 != null : "Evade target not assigned";
            force = Vector2D.mul(Evade(targetAgent1), weightEvade);
            if (!AccumulateForce(steeringForce, force)) {
                return steeringForce;
            }
        }
        if (on(BehaviorType.FLEE)) {
            force = Vector2D.mul(Flee(vehicle.getWorld().getCrosshair()), weightFlee);
            if (!AccumulateForce(steeringForce, force)) {
                return steeringForce;
            }
        }
        //these next three can be combined for flocking behavior (wander is also a good behavior to add into this mix)
        if (!isSpacePartitioningOn()) {
            if (on(BehaviorType.SEPARATION)) {
                force = Vector2D.mul(Separation(vehicle.getWorld().getVehicles()), weightSeparation);
                if (!AccumulateForce(steeringForce, force)) {
                    return steeringForce;
                }
            }
            if (on(BehaviorType.ALLIGNMENT)) {
                force = Vector2D.mul(Alignment(vehicle.getWorld().getVehicles()), weightAlignment);
                if (!AccumulateForce(steeringForce, force)) {
                    return steeringForce;
                }
            }
            if (on(BehaviorType.COHESIAN)) {
                force = Vector2D.mul(Cohesion(vehicle.getWorld().getVehicles()), weightCohesion);
                if (!AccumulateForce(steeringForce, force)) {
                    return steeringForce;
                }
            }
        } else {
            if (on(BehaviorType.SEPARATION)) {
                force = Vector2D.mul(SeparationPlus(vehicle.getWorld().getVehicles()), weightSeparation);
                if (!AccumulateForce(steeringForce, force)) {
                    return steeringForce;
                }
            }
            if (on(BehaviorType.ALLIGNMENT)) {
                force = Vector2D.mul(AlignmentPlus(vehicle.getWorld().getVehicles()), weightAlignment);
                if (!AccumulateForce(steeringForce, force)) {
                    return steeringForce;
                }
            }
            if (on(BehaviorType.COHESIAN)) {
                force = Vector2D.mul(CohesionPlus(vehicle.getWorld().getVehicles()), weightCohesion);
                if (!AccumulateForce(steeringForce, force)) {
                    return steeringForce;
                }
            }
        }
        if (on(BehaviorType.SEEK)) {
            force = Vector2D.mul(Seek(vehicle.getWorld().getCrosshair()), weightSeek);
            if (!AccumulateForce(steeringForce, force)) {
                return steeringForce;
            }
        }
        if (on(BehaviorType.ARRIVE)) {
            force = Vector2D.mul(Arrive(vehicle.getWorld().getCrosshair(), deceleration), weightArrive);
            if (!AccumulateForce(steeringForce, force)) {
                return steeringForce;
            }
        }
        if (on(BehaviorType.WANDER)) {
            force = Vector2D.mul(Wander(), weightWander);
            if (!AccumulateForce(steeringForce, force)) {
                return steeringForce;
            }
        }
        if (on(BehaviorType.PURSUIT)) {
            assert targetAgent1 != null : "pursuit target not assigned";
            force = Vector2D.mul(Pursuit(targetAgent1), weightPursuit);
            if (!AccumulateForce(steeringForce, force)) {
                return steeringForce;
            }
        }
        if (on(BehaviorType.OFFSET_PURSUIT)) {
            assert targetAgent1 != null : "pursuit target not assigned";
            assert !offset.isZero() : "No offset assigned";
            force = OffsetPursuit(targetAgent1, offset);
            if (!AccumulateForce(steeringForce, force)) {
                return steeringForce;
            }
        }
        if (on(BehaviorType.INTERPOSE)) {
            assert targetAgent1 != null && targetAgent2 != null : "Interpose agents not assigned";
            force = Vector2D.mul(Interpose(targetAgent1, targetAgent2), weightInterpose);
            if (!AccumulateForce(steeringForce, force)) {
                return steeringForce;
            }
        }
        if (on(BehaviorType.HIDE)) {
            assert targetAgent1 != null : "Hide target not assigned";
            force = Vector2D.mul(Hide(targetAgent1, vehicle.getWorld().getObstacles()), weightHide);
            if (!AccumulateForce(steeringForce, force)) {
                return steeringForce;
            }
        }
        if (on(BehaviorType.FPLLOW_PATH)) {
            force = Vector2D.mul(FollowPath(), weightFollowPath);
            if (!AccumulateForce(steeringForce, force)) {
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
    private Vector2D CalculateWeightedSum() {
        if (on(BehaviorType.WALL_AVOIDANCE)) {
            steeringForce.add(Vector2D.mul(WallAvoidance(vehicle.getWorld().getWalls()),
                    weightWallAvoidance));
        }
        if (on(BehaviorType.OBSTACLE_AVOIDANCE)) {
            steeringForce.add(Vector2D.mul(ObstacleAvoidance(vehicle.getWorld().getObstacles()),
                    weightObstacleAvoidance));
        }
        if (on(BehaviorType.EVADE)) {
            assert targetAgent1 != null : "Evade target not assigned";

            steeringForce.add(Vector2D.mul(Evade(targetAgent1), weightEvade));
        }
        //these next three can be combined for flocking behavior (wander is also a good behavior to add into this mix)
        if (!isSpacePartitioningOn()) {
            if (on(BehaviorType.SEPARATION)) {
                steeringForce.add(Vector2D.mul(Separation(vehicle.getWorld().getVehicles()), weightSeparation));
            }
            if (on(BehaviorType.ALLIGNMENT)) {
                steeringForce.add(Vector2D.mul(Alignment(vehicle.getWorld().getVehicles()), weightAlignment));
            }
            if (on(BehaviorType.COHESIAN)) {
                steeringForce.add(Vector2D.mul(Cohesion(vehicle.getWorld().getVehicles()), weightCohesion));
            }
        } else {
            if (on(BehaviorType.SEPARATION)) {
                steeringForce.add(Vector2D.mul(SeparationPlus(vehicle.getWorld().getVehicles()), weightSeparation));
            }
            if (on(BehaviorType.ALLIGNMENT)) {
                steeringForce.add(Vector2D.mul(AlignmentPlus(vehicle.getWorld().getVehicles()), weightAlignment));
            }
            if (on(BehaviorType.COHESIAN)) {
                steeringForce.add(Vector2D.mul(CohesionPlus(vehicle.getWorld().getVehicles()), weightCohesion));
            }
        }

        if (on(BehaviorType.WANDER)) {
            steeringForce.add(Vector2D.mul(Wander(), weightWander));
        }
        if (on(BehaviorType.SEEK)) {
            steeringForce.add(Vector2D.mul(Seek(vehicle.getWorld().getCrosshair()), weightSeek));
        }
        if (on(BehaviorType.FLEE)) {
            steeringForce.add(Vector2D.mul(Flee(vehicle.getWorld().getCrosshair()), weightFlee));
        }
        if (on(BehaviorType.ARRIVE)) {
            steeringForce.add(Vector2D.mul(Arrive(vehicle.getWorld().getCrosshair(), deceleration), weightArrive));
        }
        if (on(BehaviorType.PURSUIT)) {
            assert targetAgent1 != null : "pursuit target not assigned";
            steeringForce.add(Vector2D.mul(Pursuit(targetAgent1), weightPursuit));
        }
        if (on(BehaviorType.OFFSET_PURSUIT)) {
            assert targetAgent1 != null : "pursuit target not assigned";
            assert !offset.isZero() : "No offset assigned";
            steeringForce.add(Vector2D.mul(OffsetPursuit(targetAgent1, offset), weightOffsetPursuit));
        }
        if (on(BehaviorType.INTERPOSE)) {
            assert targetAgent1 != null && targetAgent2 != null : "Interpose agents not assigned";
            steeringForce.add(Vector2D.mul(Interpose(targetAgent1, targetAgent2), weightInterpose));
        }
        if (on(BehaviorType.HIDE)) {
            assert targetAgent1 != null : "Hide target not assigned";
            steeringForce.add(Vector2D.mul(Hide(targetAgent1, vehicle.getWorld().getObstacles()), weightHide));
        }
        if (on(BehaviorType.FPLLOW_PATH)) {
            steeringForce.add(Vector2D.mul(FollowPath(), weightFollowPath));
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
    private Vector2D CalculateDithered() {
        //reset the steering force
        steeringForce.zero();

        if (on(BehaviorType.WALL_AVOIDANCE) && Utils.RandFloat() < ParamLoader.prm.prWallAvoidance) {
            steeringForce = Vector2D.mul(WallAvoidance(vehicle.getWorld().getWalls()),
                    weightWallAvoidance / ParamLoader.prm.prWallAvoidance);
            if (!steeringForce.isZero()) {
                steeringForce.truncate(vehicle.getMaxForce());
                return steeringForce;
            }
        }

        if (on(BehaviorType.OBSTACLE_AVOIDANCE) && Utils.RandFloat() < ParamLoader.prm.prObstacleAvoidance) {
            steeringForce.add(Vector2D.mul(ObstacleAvoidance(vehicle.getWorld().getObstacles()),
                    weightObstacleAvoidance / ParamLoader.prm.prObstacleAvoidance));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(vehicle.getMaxForce());
                return steeringForce;
            }
        }

        if (!isSpacePartitioningOn()) {
            if (on(BehaviorType.SEPARATION) && Utils.RandFloat() < ParamLoader.prm.prSeparation) {
                steeringForce.add(Vector2D.mul(Separation(vehicle.getWorld().getVehicles()),
                        weightSeparation / ParamLoader.prm.prSeparation));
                if (!steeringForce.isZero()) {
                    steeringForce.truncate(vehicle.getMaxForce());
                    return steeringForce;
                }
            }
        } else {
            if (on(BehaviorType.SEPARATION) && Utils.RandFloat() < ParamLoader.prm.prSeparation) {
                steeringForce.add(Vector2D.mul(SeparationPlus(vehicle.getWorld().getVehicles()),
                        weightSeparation / ParamLoader.prm.prSeparation));
                if (!steeringForce.isZero()) {
                    steeringForce.truncate(vehicle.getMaxForce());
                    return steeringForce;
                }
            }
        }


        if (on(BehaviorType.FLEE) && Utils.RandFloat() < ParamLoader.prm.prFlee) {
            steeringForce.add(Vector2D.mul(Flee(vehicle.getWorld().getCrosshair()), weightFlee / ParamLoader.prm.prFlee));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(vehicle.getMaxForce());
                return steeringForce;
            }
        }

        if (on(BehaviorType.EVADE) && Utils.RandFloat() < ParamLoader.prm.prEvade) {
            assert targetAgent1 != null : "Evade target not assigned";
            steeringForce.add(Vector2D.mul(Evade(targetAgent1), weightEvade / ParamLoader.prm.prEvade));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(vehicle.getMaxForce());
                return steeringForce;
            }
        }

        if (!isSpacePartitioningOn()) {
            if (on(BehaviorType.ALLIGNMENT) && Utils.RandFloat() < ParamLoader.prm.prAlignment) {
                steeringForce.add(Vector2D.mul(Alignment(vehicle.getWorld().getVehicles()),
                        weightAlignment / ParamLoader.prm.prAlignment));
                if (!steeringForce.isZero()) {
                    steeringForce.truncate(vehicle.getMaxForce());
                    return steeringForce;
                }
            }

            if (on(BehaviorType.COHESIAN) && Utils.RandFloat() < ParamLoader.prm.prCohesion) {
                steeringForce.add(Vector2D.mul(Cohesion(vehicle.getWorld().getVehicles()),
                        weightCohesion / ParamLoader.prm.prCohesion));
                if (!steeringForce.isZero()) {
                    steeringForce.truncate(vehicle.getMaxForce());
                    return steeringForce;
                }
            }
        } else {
            if (on(BehaviorType.ALLIGNMENT) && Utils.RandFloat() < ParamLoader.prm.prAlignment) {
                steeringForce.add(Vector2D.mul(AlignmentPlus(vehicle.getWorld().getVehicles()),
                        weightAlignment / ParamLoader.prm.prAlignment));
                if (!steeringForce.isZero()) {
                    steeringForce.truncate(vehicle.getMaxForce());
                    return steeringForce;
                }
            }

            if (on(BehaviorType.COHESIAN) && Utils.RandFloat() < ParamLoader.prm.prCohesion) {
                steeringForce.add(Vector2D.mul(CohesionPlus(vehicle.getWorld().getVehicles()),
                        weightCohesion / ParamLoader.prm.prCohesion));
                if (!steeringForce.isZero()) {
                    steeringForce.truncate(vehicle.getMaxForce());
                    return steeringForce;
                }
            }
        }

        if (on(BehaviorType.WANDER) && Utils.RandFloat() < ParamLoader.prm.prWander) {
            steeringForce.add(Vector2D.mul(Wander(), weightWander / ParamLoader.prm.prWander));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(vehicle.getMaxForce());
                return steeringForce;
            }
        }

        if (on(BehaviorType.SEEK) && Utils.RandFloat() < ParamLoader.prm.prSeek) {
            steeringForce.add(Vector2D.mul(Seek(vehicle.getWorld().getCrosshair()), weightSeek / ParamLoader.prm.prSeek));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(vehicle.getMaxForce());
                return steeringForce;
            }
        }

        if (on(BehaviorType.ARRIVE) && Utils.RandFloat() < ParamLoader.prm.prArrive) {
            steeringForce.add(Vector2D.mul(Arrive(vehicle.getWorld().getCrosshair(), deceleration),
                    weightArrive / ParamLoader.prm.prArrive));
            if (!steeringForce.isZero()) {
                steeringForce.truncate(vehicle.getMaxForce());
                return steeringForce;
            }
        }
        return steeringForce;
    }

    public void SetTarget(final Vector2D t) {
        target = new Vector2D(t);
    }

    public void SetTargetAgent1(Vehicle Agent) {
        targetAgent1 = Agent;
    }

    public void SetTargetAgent2(Vehicle Agent) {
        targetAgent2 = Agent;
    }

    public void SetOffset(final Vector2D offset) {
        this.offset = offset;
    }

    public Vector2D GetOffset() {
        return offset;
    }

    public void SetPath(List<Vector2D> new_path) {
        path.Set(new_path);
    }

    public void CreateRandomPath(int num_waypoints, int mx, int my, int cx, int cy) {
        path.CreateRandomPath(num_waypoints, mx, my, cx, cy);
    }

    public Vector2D Force() {
        return steeringForce;
    }

    public void ToggleSpacePartitioningOnOff() {
        cellSpaceOn = !cellSpaceOn;
    }

    public boolean isSpacePartitioningOn() {
        return cellSpaceOn;
    }

    public void SetSummingMethod(SummingMethod sm) {
        summingMethod = sm;
    }

    public void FleeOn() {
        flags |= BehaviorType.FLEE.getFlag();
    }

    public void SeekOn() {
        flags |= BehaviorType.SEEK.getFlag();
    }

    public void ArriveOn() {
        flags |= BehaviorType.ARRIVE.getFlag();
    }

    public void WanderOn() {
        flags |= BehaviorType.WANDER.getFlag();
    }

    public void PursuitOn(Vehicle v) {
        flags |= BehaviorType.PURSUIT.getFlag();
        targetAgent1 = v;
    }

    public void EvadeOn(Vehicle v) {
        flags |= BehaviorType.EVADE.getFlag();
        targetAgent1 = v;
    }

    public void CohesionOn() {
        flags |= BehaviorType.COHESIAN.getFlag();
    }

    public void SeparationOn() {
        flags |= BehaviorType.SEPARATION.getFlag();
    }

    public void AlignmentOn() {
        flags |= BehaviorType.ALLIGNMENT.getFlag();
    }

    public void ObstacleAvoidanceOn() {
        flags |= BehaviorType.OBSTACLE_AVOIDANCE.getFlag();
    }

    public void WallAvoidanceOn() {
        flags |= BehaviorType.WALL_AVOIDANCE.getFlag();
    }

    public void FollowPathOn() {
        flags |= BehaviorType.FPLLOW_PATH.getFlag();
    }

    public void InterposeOn(Vehicle v1, Vehicle v2) {
        flags |= BehaviorType.INTERPOSE.getFlag();
        targetAgent1 = v1;
        targetAgent2 = v2;
    }

    public void HideOn(Vehicle v) {
        flags |= BehaviorType.HIDE.getFlag();
        targetAgent1 = v;
    }

    public void OffsetPursuitOn(Vehicle v1, final Vector2D offset) {
        flags |= BehaviorType.OFFSET_PURSUIT.getFlag();
        this.offset = offset;
        targetAgent1 = v1;
    }

    public void FlockingOn() {
        CohesionOn();
        AlignmentOn();
        SeparationOn();
        WanderOn();
    }

    public void FleeOff() {
        if (on(BehaviorType.FLEE)) {
            flags ^= BehaviorType.FLEE.getFlag();
        }
    }

    public void SeekOff() {
        if (on(BehaviorType.SEEK)) {
            flags ^= BehaviorType.SEEK.getFlag();
        }
    }

    public void ArriveOff() {
        if (on(BehaviorType.ARRIVE)) {
            flags ^= BehaviorType.ARRIVE.getFlag();
        }
    }

    public void WanderOff() {
        if (on(BehaviorType.WANDER)) {
            flags ^= BehaviorType.WANDER.getFlag();
        }
    }

    public void PursuitOff() {
        if (on(BehaviorType.PURSUIT)) {
            flags ^= BehaviorType.PURSUIT.getFlag();
        }
    }

    public void EvadeOff() {
        if (on(BehaviorType.EVADE)) {
            flags ^= BehaviorType.EVADE.getFlag();
        }
    }

    public void CohesionOff() {
        if (on(BehaviorType.COHESIAN)) {
            flags ^= BehaviorType.COHESIAN.getFlag();
        }
    }

    public void SeparationOff() {
        if (on(BehaviorType.SEPARATION)) {
            flags ^= BehaviorType.SEPARATION.getFlag();
        }
    }

    public void AlignmentOff() {
        if (on(BehaviorType.ALLIGNMENT)) {
            flags ^= BehaviorType.ALLIGNMENT.getFlag();
        }
    }

    public void ObstacleAvoidanceOff() {
        if (on(BehaviorType.OBSTACLE_AVOIDANCE)) {
            flags ^= BehaviorType.OBSTACLE_AVOIDANCE.getFlag();
        }
    }

    public void WallAvoidanceOff() {
        if (on(BehaviorType.WALL_AVOIDANCE)) {
            flags ^= BehaviorType.WALL_AVOIDANCE.getFlag();
        }
    }

    public void FollowPathOff() {
        if (on(BehaviorType.FPLLOW_PATH)) {
            flags ^= BehaviorType.FPLLOW_PATH.getFlag();
        }
    }

    public void InterposeOff() {
        if (on(BehaviorType.INTERPOSE)) {
            flags ^= BehaviorType.INTERPOSE.getFlag();
        }
    }

    public void HideOff() {
        if (on(BehaviorType.HIDE)) {
            flags ^= BehaviorType.HIDE.getFlag();
        }
    }

    public void OffsetPursuitOff() {
        if (on(BehaviorType.OFFSET_PURSUIT)) {
            flags ^= BehaviorType.OFFSET_PURSUIT.getFlag();
        }
    }

    public void FlockingOff() {
        CohesionOff();
        AlignmentOff();
        SeparationOff();
        WanderOff();
    }

    public boolean isFleeOn() {
        return on(BehaviorType.FLEE);
    }

    public boolean isSeekOn() {
        return on(BehaviorType.SEEK);
    }

    public boolean isArriveOn() {
        return on(BehaviorType.ARRIVE);
    }

    public boolean isWanderOn() {
        return on(BehaviorType.WANDER);
    }

    public boolean isPursuitOn() {
        return on(BehaviorType.PURSUIT);
    }

    public boolean isEvadeOn() {
        return on(BehaviorType.EVADE);
    }

    public boolean isCohesionOn() {
        return on(BehaviorType.COHESIAN);
    }

    public boolean isSeparationOn() {
        return on(BehaviorType.SEPARATION);
    }

    public boolean isAlignmentOn() {
        return on(BehaviorType.ALLIGNMENT);
    }

    public boolean isObstacleAvoidanceOn() {
        return on(BehaviorType.OBSTACLE_AVOIDANCE);
    }

    public boolean isWallAvoidanceOn() {
        return on(BehaviorType.WALL_AVOIDANCE);
    }

    public boolean isFollowPathOn() {
        return on(BehaviorType.FPLLOW_PATH);
    }

    public boolean isInterposeOn() {
        return on(BehaviorType.INTERPOSE);
    }

    public boolean isHideOn() {
        return on(BehaviorType.HIDE);
    }

    public boolean isOffsetPursuitOn() {
        return on(BehaviorType.OFFSET_PURSUIT);
    }

    public double DBoxLength() {
        return boxLength;
    }

    public List<Vector2D> GetFeelers() {
        return feelers;
    }

    public double WanderJitter() {
        return wanderJitter;
    }

    public double WanderDistance() {
        return wanderDistance;
    }

    public double WanderRadius() {
        return wanderRadius;
    }

    public double SeparationWeight() {
        return weightSeparation;
    }

    public double AlignmentWeight() {
        return weightAlignment;
    }

    public double CohesionWeight() {
        return weightCohesion;
    }
}
