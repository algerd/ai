package Steering;

import common.D2.Geometry;
import common.D2.Transformation;
import common.D2.Vector2D;
import common.misc.CellSpacePartition;
import common.misc.CppToJava;
import common.misc.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Behaviors {
    
    private Vehicle vehicle;
    // целевая точка движения агента
    private Vector2D targetMove;
    //these can be used to keep track of friends, pursuers, or prey
    private Vehicle targetAgent1; 
    private Vehicle targetAgent2;
    //any offsetPursuit used for formations or offsetPursuit pursuit
    private Vector2D offsetPursuit;
    //pointer to any current path
    private Path path;
    
     
    public Behaviors(Vehicle agent) {
        vehicle = agent;             
    }
       
    /**
     * Given a target, this behavior returns a steering force which will direct the agent towards the target
     */
    public Vector2D seek() {
        return seek(targetMove);
    }
    public Vector2D seek(Vector2D TargetPos) {
        Vector2D DesiredVelocity = Vector2D.mul(
                Vector2D.vec2DNormalize(Vector2D.sub(TargetPos, vehicle.getPos())),
                vehicle.getMaxSpeed());
        return Vector2D.sub(DesiredVelocity, vehicle.getVelocity());
    }
       
    /**
     *  Does the opposite of Seek
     */
    public Vector2D flee() {
        return flee(targetMove);
    }
    public Vector2D flee(Vector2D TargetPos) {
        Vector2D DesiredVelocity = Vector2D.mul(
                Vector2D.vec2DNormalize(Vector2D.sub(vehicle.getPos(), TargetPos)),
                vehicle.getMaxSpeed());
        return Vector2D.sub(DesiredVelocity, vehicle.getVelocity());
    }
      
    /**
     * This behavior is similar to seek but it attempts to arrive at the target with a zero velocity
     * В отличие от seek() метод будет останавливать агента у цели(TargetPos) с заданным замедлением deceleration
     */
    public Vector2D arrive(double deceleration) {
        return arrive(targetMove, deceleration);
    }  
    public Vector2D arrive(Vector2D TargetPos, double deceleration) {       
        Vector2D target = Vector2D.sub(TargetPos, vehicle.getPos());
        double dist = target.length();
        if (dist > 0) {         
            //calculate the speed required to reach the target given the desired deceleration
            //and make sure the velocity does not exceed the max
            double speed = Math.min(dist / deceleration, vehicle.getMaxSpeed());

            //from here proceed just like Seek except we don't need to normalize the ToTarget vector 
            //because we have already gone to the trouble of calculating its length: dist. 
            Vector2D DesiredVelocity = Vector2D.mul(target, speed / dist);
            return Vector2D.sub(DesiredVelocity, vehicle.getVelocity());
        }
        return new Vector2D(0, 0);
    }
    
     /**
     *  this behavior creates a force that steers the agent towards the evader
     */
    public Vector2D pursuit() {
        return pursuit(targetAgent1);
    }
    public Vector2D pursuit(final Vehicle evader) {
        //if the evader is ahead and facing the agent then we can just seek for the evader's current position.
        Vector2D toEvader = Vector2D.sub(evader.getPos(), vehicle.getPos());
        double relativeHeading = vehicle.getHeading().dot(evader.getHeading());      
        //acos(0.95)=18 degs
        if ((toEvader.dot(vehicle.getHeading()) > 0) && (relativeHeading < -0.95)){
            return seek(evader.getPos());
        }

        // время, требуемое для прохождения дистанции до преследуемого с максимальной скоростью
        double time = toEvader.length() / (vehicle.getMaxSpeed() + evader.getSpeed());
        // предполагаемая позиция преследуемого через время time
        Vector2D predicatePos = Vector2D.add(evader.getPos(), Vector2D.mul(evader.getVelocity(), time));       
        // возвращаем вектор скорости до предполагаемой позиции преследуемого
        return seek(predicatePos);
    }
          
    /**
     *  similar to pursuit except the agent Flees from the estimated future position of the pursuer
     */
    public Vector2D evade() {
        return evade(targetAgent1);
    } 
    public Vector2D evade(final Vehicle pursuer) {
        // Not necessary to include the check for facing direction this time
        Vector2D toPursuer = Vector2D.sub(pursuer.getPos(), vehicle.getPos());

        //uncomment the following two lines to have Evade only consider pursuers within a 'threat range'
        final double threatRange = 100.0;
        if (toPursuer.lengthSq() > threatRange * threatRange) {
            return new Vector2D();
        }
        // время, требуемое для прохождения дистанции до преследователя с максимальной скоростью
        double time = toPursuer.length() / (vehicle.getMaxSpeed() + pursuer.getSpeed());
        // предполагаемая позиция преследователя через время time
        Vector2D predicatePos = Vector2D.add(pursuer.getPos(), Vector2D.mul(pursuer.getVelocity(), time));
        // возвращаем вектор скорости до предполагаемой позиции преследователя
        return flee(predicatePos);
    }
    
     /**
     * This behavior makes the agent wander about randomly
     */
    public Vector2D wander() {
        //this behavior is dependent on the update rate, so this line must be included when using time independent framerate.
        double jitterThisTimeSlice = Params.WANDER_JITTER_PER_SEC * vehicle.getTimeElapsed();

        double theta = Utils.RandFloat() * Utils.TwoPi;
        //create a vector to a target position on the wander circle
        Vector2D wanderTarget = new Vector2D(Params.WANDER_RAD * Math.cos(theta), Params.WANDER_RAD * Math.sin(theta));
        
        //first, add a small random vector to the target's position
        wanderTarget.add(new Vector2D(Utils.RandomClamped() * jitterThisTimeSlice, Utils.RandomClamped() * jitterThisTimeSlice));

        //reproject this new vector back on to a unit circle
        wanderTarget.normalize();

        //increase the length of the vector to the same as the radius of the wander circle
        wanderTarget.mul(Params.WANDER_RAD);

        //move the target into a position WanderDist in front of the agent
        Vector2D target = Vector2D.add(wanderTarget, new Vector2D(Params.WANDER_DIST, 0));

        //project the target into world space
        Vector2D transformTarget = Transformation.pointToWorldSpace(
                target,
                vehicle.getHeading(),
                vehicle.getSide(),
                vehicle.getPos());

        //and steer towards it
        return Vector2D.sub(transformTarget, vehicle.getPos());
    }
    
    /**
     *  Given a vector of obstacles, this method returns a steering force
     *  that will prevent the agent colliding with the closest obstacle
     */
    public Vector2D obstacleAvoidance() {
        List<Obstacle> obstacleList = vehicle.getWorld().getObstacleList();
        
        //the detection box length is proportional to the agent's velocity
        double boxLength = Params.MIN_DETECTION_BOX_LENGTH + (vehicle.getSpeed() / vehicle.getMaxSpeed()) * Params.MIN_DETECTION_BOX_LENGTH;

        //tag all obstacles within range of the box for processing
        EntityFunctionTemplates.tagNeighbors(vehicle, obstacleList, boxLength);

        //this will keep track of the closest intersecting obstacle (CIB)
        BaseGameEntity closestIntersectingObstacle = null;

        //this will be used to track the distance to the CIB
        double distToClosestIP = Utils.MaxDouble;

        //this will record the transformed local coordinates of the CIB
        Vector2D localPosOfClosestObstacle = new Vector2D();

        ListIterator<Obstacle> obstacleListIterator = obstacleList.listIterator();

        while (obstacleListIterator.hasNext()) {
            //if the obstacle has been tagged within range proceed
            BaseGameEntity obstacle = obstacleListIterator.next();
            if (obstacle.isTagged()) {
                //calculate this obstacle's position in local space
                Vector2D localPosObstacle = Transformation.pointToLocalSpace(
                    obstacle.getPos(),
                    vehicle.getHeading(),
                    vehicle.getSide(),
                    vehicle.getPos());

                //if the local position has a negative x value then it must lay
                //behind the agent. (in which case it can be ignored)
                if (localPosObstacle.x >= 0) {
                    //if the distance from the x axis to the object's position is less
                    //than its radius + half the width of the detection box then there is a potential intersection.
                    double expandedRadius = obstacle.getBoundingRadius() + vehicle.getBoundingRadius();

                    if (Math.abs(localPosObstacle.y) < expandedRadius) {
                        //now to do a line/circle intersection test. The center of the 
                        //circle is represented by (cX, cY). The intersection points are 
                        //given by the formula x = cX +/-sqrt(r^2-cY^2) for y=0. 
                        //We only need to look at the smallest positive value of x because
                        //that will be the closest point of intersection.
                        double cX = localPosObstacle.x;
                        double cY = localPosObstacle.y;

                        //we only need to calculate the sqrt part of the above equation once
                        double sqrtPart = Math.sqrt(expandedRadius * expandedRadius - cY * cY);
                        double ip = cX - sqrtPart;
                        if (ip <= 0.0) {
                            ip = cX + sqrtPart;
                        }
                        //test to see if this is the closest so far. If it is keep a record of the obstacle and its local coordinates
                        if (ip < distToClosestIP) {
                            distToClosestIP = ip;
                            closestIntersectingObstacle = obstacle;
                            localPosOfClosestObstacle = localPosObstacle;
                        }
                    }
                }
            }
        }
        //if we have found an intersecting obstacle, calculate a steering force away from it
        Vector2D steeringForce = new Vector2D();

        if (closestIntersectingObstacle != null) {
            //the closer the agent is to an object, the stronger the steering force should be
            double multiplier = 1.0 + (boxLength - localPosOfClosestObstacle.x) / boxLength;

            //calculate the lateral force
            steeringForce.y = (closestIntersectingObstacle.getBoundingRadius() - localPosOfClosestObstacle.y) * multiplier;

            //apply a braking force proportional to the obstacles distance from the vehicle. 
            final double brakingWeight = 0.2;
            steeringForce.x = (closestIntersectingObstacle.getBoundingRadius() - localPosOfClosestObstacle.x) * brakingWeight;
        }

        //finally, convert the steering vector from local to world space
        return Transformation.vectorToWorldSpace(steeringForce, vehicle.getHeading(), vehicle.getSide());
    }
    
    /**
     * This returns a steering force that will keep the agent away from any
     *  walls it may encounter
     */
    public Vector2D wallAvoidance() {
        
        List<Wall> wallList = vehicle.getWorld().getWallList();      
        List<Vector2D> feelerList = new ArrayList<>(3);
        
        //feeler pointing straight in front
        feelerList.add(Vector2D.add(vehicle.getPos(), Vector2D.mul(Params.WALL_DETECTION_FEELER_LENGTH, vehicle.getHeading())));

        //feeler to left
        Vector2D temp = new Vector2D(vehicle.getHeading());
        Transformation.vec2DRotateAroundOrigin(temp, Utils.HalfPi * 3.5f);
        feelerList.add(Vector2D.add(vehicle.getPos(), Vector2D.mul(Params.WALL_DETECTION_FEELER_LENGTH / 2.0f, temp)));

        //feeler to right
        temp = new Vector2D(vehicle.getHeading());
        Transformation.vec2DRotateAroundOrigin(temp, Utils.HalfPi * 0.5f);
        feelerList.add(Vector2D.add(vehicle.getPos(), Vector2D.mul(Params.WALL_DETECTION_FEELER_LENGTH / 2.0f, temp)));
   
        double distToThisIP = 0.0;
        double distToClosestIP = Utils.MaxDouble;

        Vector2D steeringForce = new Vector2D();
        Vector2D point = new Vector2D();        //used for storing temporary info
        Vector2D closestPoint = new Vector2D(); //holds the closest intersection point

        Wall closestWall = null;
        for (Vector2D feeler : feelerList) {
            //run through each wall checking for any intersection points
            CppToJava.DoubleRef distToThisIPRef = new CppToJava.DoubleRef(distToThisIP);
            for (Wall wall : wallList) {
                if (Geometry.LineIntersection2D(
                        vehicle.getPos(),
                        feeler,
                        wall.from(),
                        wall.to(),
                        distToThisIPRef,
                        point)) {
                    distToThisIP = distToThisIPRef.toDouble();
                    //is this the closest found so far? If so keep a record
                    if (distToThisIP < distToClosestIP) {
                        distToClosestIP = distToThisIP;
                        closestWall = wall;
                        closestPoint = point;
                    }
                }
            }
            //if an intersection point has been detected, calculate a force that will direct the agent away
            if (closestWall != null) {
                //calculate by what distance the projected position of the agent will overshoot the wall
                Vector2D OverShoot = Vector2D.sub(feeler, closestPoint);
                //create a force in the direction of the wall normal, with a magnitude of the overshoot
                steeringForce = Vector2D.mul(closestWall.normal(), OverShoot.length());
            }
        }
        return steeringForce;
    }
    
    /**
     * Given two agents, this method returns a force that attempts to 
     * position the vehicle between them
     */
    public Vector2D interpose() {
        return interpose(targetAgent1, targetAgent2);
    }
    public Vector2D interpose(final Vehicle AgentA, final Vehicle AgentB) {
        //first we need to figure out where the two agents are going to be at 
        //time T in the future. This is approximated by determining the time
        //taken to reach the mid way point at the current time at at max speed.
        Vector2D midPoint = Vector2D.div(Vector2D.add(AgentA.getPos(), AgentB.getPos()), 2.0);

        double time = Vector2D.vec2DDistance(vehicle.getPos(), midPoint) / vehicle.getMaxSpeed();

        //now we have T, we assume that agent A and agent B will continue on a
        //straight trajectory and extrapolate to get their future positions
        Vector2D APos = Vector2D.add(AgentA.getPos(), Vector2D.mul(AgentA.getVelocity(), time));
        Vector2D BPos = Vector2D.add(AgentB.getPos(), Vector2D.mul(AgentB.getVelocity(), time));

        //calculate the mid point of these predicted positions
        midPoint = Vector2D.div(Vector2D.add(APos, BPos), 2.0);
        //then steer to Arrive at it
        return arrive(midPoint, Params.DECELERATION_FAST);
    }
    
    /**
     * Спрятаться от охотника за объектом-препятствием.
     */
    public Vector2D hide() {
        return hide(targetAgent1);
    }
    public Vector2D hide(final Vehicle hunter) {
           
        double distToClosest = Utils.MaxDouble;
        Vector2D bestHidingSpot = new Vector2D();
        BaseGameEntity closest;
        
        List<Obstacle> obstacleList = vehicle.getWorld().getObstacleList();
        ListIterator<Obstacle> obstacleListIterator = obstacleList.listIterator();
        while (obstacleListIterator.hasNext()) {
            BaseGameEntity obstacle = obstacleListIterator.next();
            //calculate the position of the hiding spot for this obstacle
            //calculate how far away the agent is to be from the chosen obstacle's bounding radius
            double distanceFromBoundary = 30.0;
            double distAway = obstacle.getBoundingRadius() + distanceFromBoundary;

            //calculate the heading toward the object from the hunter
            Vector2D toOb = Vector2D.vec2DNormalize(Vector2D.sub(obstacle.getPos(), hunter.getPos()));

            //scale it to size and add to the obstacles position to get the hiding spot.
            Vector2D HidingSpot = Vector2D.add(Vector2D.mul(toOb, distAway), obstacle.getPos());
                               
            //work in distance-squared space to find the closest hiding spot to the agent
            double dist = Vector2D.vec2DDistanceSq(HidingSpot, vehicle.getPos());
            if (dist < distToClosest) {
                distToClosest = dist;
                bestHidingSpot = HidingSpot;
                closest = obstacle;
            }
        }
        //if no suitable obstacles found then Evade the hunter
        if (distToClosest == Utils.MaxFloat) {
            return evade(hunter);
        }
        //else use Arrive on the hiding spot
        return arrive(bestHidingSpot, Params.DECELERATION_FAST);
    }
    
    /**
     *  Given a series of Vector2Ds, this method produces a force that will
     *  move the agent along the waypoints in order. The agent uses the
     * 'Seek' behavior to move to the next waypoint - unless it is the last
     *  waypoint, in which case it 'Arrives'
     */
    public Vector2D followPath() {
        double waypointSeekDistSq = Params.WAYPOINT_SEEK_DIST * Params.WAYPOINT_SEEK_DIST;
        //Path path = vehicle.getSteering().getPath();
        //move to next target if close enough to current target (working in distance squared space)
        if (Vector2D.vec2DDistanceSq(path.getCurrentWaypoint(), vehicle.getPos()) < waypointSeekDistSq) {
            path.setNextWaypoint();
        }
        return (!path.finished()) ? seek(path.getCurrentWaypoint()) : arrive(path.getCurrentWaypoint(), Params.DECELERATION_NORMAL);
    }
    
    /**
     * Produces a steering force that keeps a vehicle at a specified offsetPursuit from a leader vehicle
     */
    public Vector2D offsetPursuit() {
        return offsetPursuit(targetAgent1, offsetPursuit); 
    }
    public Vector2D offsetPursuit(final Vehicle leader, final Vector2D offset) {
        //calculate the offsetPursuit's position in world space
        Vector2D WorldOffsetPos = Transformation.pointToWorldSpace(
                offset,
                leader.getHeading(),
                leader.getSide(),
                leader.getPos());

        Vector2D toOffset = Vector2D.sub(WorldOffsetPos, vehicle.getPos());

        //the lookahead time is propotional to the distance between the leader
        //and the pursuer; and is inversely proportional to the sum of both agent's velocities
        double time = toOffset.length() / (vehicle.getMaxSpeed() + leader.getSpeed());

        //now arrive at the predicted future position of the offsetPursuit
        return arrive(Vector2D.add(WorldOffsetPos, Vector2D.mul(leader.getVelocity(), time)), Params.DECELERATION_FAST);
    }
    
    /**
     * this calculates a force repelling from the other neighbors
     */
    public Vector2D separation() {
        return separation(true);
    }
    // tagNeighbors - пометить всех агентов в пределах диапазона
    public Vector2D separation(boolean tagNeighbors) {
        tagNeighbors(tagNeighbors);
        
        List<Vehicle> vehicleList = vehicle.getWorld().getVehicleList();                     
        Vector2D steeringForce = new Vector2D();
        for (Vehicle veh : vehicleList) {
            //make sure this agent isn't included in the calculations and that
            //the agent being examined is close enough. ***also make sure it doesn't include the evade target ***
            if ((veh != vehicle) && veh.isTagged() && (veh != targetAgent1)) {
                Vector2D toAgent = Vector2D.sub(vehicle.getPos(), veh.getPos());
                //scale the force inversely proportional to the agents distance from its neighbor.
                steeringForce.add(Vector2D.div(Vector2D.vec2DNormalize(toAgent), toAgent.length()));
            }
        }
        return steeringForce;
    }
    /* NOTE: the next three behaviors are the same as the above three, except
    that they use a cell-space partition to find the neighbors
     */
    /**
     * this calculates a force repelling from the other neighbors
     * USES SPACIAL PARTITIONING
     */
    public Vector2D separationCell() {
        return separationCell(true);
    }
    // calculateNeighbors - пересчитать всех агентов в ячейке агента
    public Vector2D separationCell(boolean calculateNeighbors) {
        calculateNeighbors(calculateNeighbors);
        
        List<Vehicle> vehicleList = vehicle.getWorld().getVehicleList();      
        CellSpacePartition cellSpase = vehicle.getWorld().getCellSpace();
        List<BaseGameEntity> neighborList = cellSpase.getNeighborList();   
        
        Vector2D steeringForce = new Vector2D();
        //iterate through the neighbors and sum up all the position vectors
        for (BaseGameEntity entity : neighborList) {
            //make sure this agent isn't included in the calculations and that the agent being examined is close enough
            if (entity != vehicle) {
                Vector2D toAgent = Vector2D.sub(vehicle.getPos(), entity.getPos());
                //scale the force inversely proportional to the agents distance from its neighbor.
                steeringForce.add(Vector2D.div(Vector2D.vec2DNormalize(toAgent), toAgent.length()));
            }
        }
        return steeringForce;
    }
    
    /**
     * returns a force that attempts to align this agents heading with that of its neighbors
     */
    public Vector2D alignment() {
        return alignment(true);
    }
    // tagNeighbors - пометить всех агентов в пределах диапазона
    public Vector2D alignment(boolean tagNeighbors) {
        tagNeighbors(tagNeighbors);
        List<Vehicle> vehicleList = vehicle.getWorld().getVehicleList();
                
        //used to record the average heading of the neighbors
        Vector2D averageHeading = new Vector2D();
        //used to count the number of vehicles in the neighborhood
        int neighborCount = 0;
        //iterate through all the tagged vehicles and sum their heading vectors
        for (Vehicle veh : vehicleList) {
            //make sure *this* agent isn't included in the calculations and that
            //the agent being examined  is close enough ***also make sure it doesn't include any evade target ***
            if ((veh != vehicle) && veh.isTagged() && (veh != targetAgent1)) {
                averageHeading.add(veh.getHeading());
                ++neighborCount;
            }
        }
        //if the neighborhood contained one or more vehicles, average their heading vectors.
        if (neighborCount > 0) {
            averageHeading.div((double) neighborCount);
            averageHeading.sub(vehicle.getHeading());
        }
        return averageHeading;
    }
    /**
     * returns a force that attempts to align this agents heading with that
     * of its neighbors
     *  USES SPACIAL PARTITIONING
     */
    public Vector2D alignmentCell() {
        return alignmentCell(true);
    }
    // calculateNeighbors - пересчитать всех агентов в ячейке агента
    public Vector2D alignmentCell(boolean calculateNeighbors) {
        calculateNeighbors(calculateNeighbors);
        
        List<Vehicle> vehicleList = vehicle.getWorld().getVehicleList();      
        CellSpacePartition cellSpase = vehicle.getWorld().getCellSpace();
        List<MovingEntity> neighborList = cellSpase.getNeighborList();
          
        //This will record the average heading of the neighbors
        Vector2D averageHeading = new Vector2D();
        //This count the number of vehicles in the neighborhood
        double NeighborCount = 0.0;
        //iterate through the neighbors and sum up all the position vectors
        for (MovingEntity entity : neighborList) {
            //make sure *this* agent isn't included in the calculations and that the agent being examined  is close enough
            if (entity != vehicle) {
                averageHeading.add(entity.getHeading());
                ++NeighborCount;
            }
        }
        //if the neighborhood contained one or more vehicles, average their heading vectors.
        if (NeighborCount > 0.0) {
            averageHeading.div(NeighborCount);
            averageHeading.sub(vehicle.getHeading());
        }
        return averageHeading;
    }
    
    /**
     * returns a steering force that attempts to move the agent towards the
     * center of mass of the agents in its immediate area
     */
    public Vector2D cohesion() {
        return cohesion(true);
    }
    // tagNeighbors - пометить всех агентов в пределах диапазона
    public Vector2D cohesion(boolean tagNeighbors) {
        tagNeighbors(tagNeighbors);
        
        List<Vehicle> vehicleList = vehicle.getWorld().getVehicleList(); 
        //first find the center of mass of all the agents
        Vector2D centerOfMass = new Vector2D(); 
        Vector2D steeringForce = new Vector2D();
        int neighborCount = 0;
        //iterate through the neighbors and sum up all the position vectors
        for (Vehicle veh : vehicleList) {
            //make sure *this* agent isn't included in the calculations and that
            //the agent being examined is close enough ***also make sure it doesn't include the evade target ***
            if ((veh != vehicle) && veh.isTagged() && (veh != targetAgent1)) {
                centerOfMass.add(veh.getPos());
                ++neighborCount;
            }
        }
        if (neighborCount > 0) {
            //the center of mass is the average of the sum of positions
            centerOfMass.div((double)neighborCount);
            //now seek towards that position
            steeringForce = seek(centerOfMass);
        }
        //the magnitude of cohesion is usually much larger than separation or allignment so it usually helps to normalize it.
        return Vector2D.vec2DNormalize(steeringForce);
    }
    /**
     * returns a steering force that attempts to move the agent towards the
     * center of mass of the agents in its immediate area
     * USES SPACIAL PARTITIONING
     */
    public Vector2D cohesionCell() {
        return cohesionCell(true);
    }
    // calculateNeighbors - пересчитать всех агентов в ячейке агента
    public Vector2D cohesionCell(boolean calculateNeighbors) {
        calculateNeighbors(calculateNeighbors);
        
        List<Vehicle> vehicleList = vehicle.getWorld().getVehicleList();      
        CellSpacePartition cellSpase = vehicle.getWorld().getCellSpace();
        List<BaseGameEntity> neighborList = cellSpase.getNeighborList();
          
        //first find the center of mass of all the agents
        Vector2D CenterOfMass = new Vector2D(), steeringForce = new Vector2D();
        int neighborCount = 0;
        //iterate through the neighbors and sum up all the position vectors 
        for (BaseGameEntity entity : neighborList) {
            //make sure *this* agent isn't included in the calculations and that the agent being examined is close enough
            if (entity != vehicle) {
                CenterOfMass.add(entity.getPos());
                ++neighborCount;
            }
        }
        if (neighborCount > 0) {
            //the center of mass is the average of the sum of positions
            CenterOfMass.div((double)neighborCount);
            //now seek towards that position
            steeringForce = seek(CenterOfMass);
        }
        //the magnitude of cohesion is usually much larger than separation or allignment so it usually helps to normalize it.
        return Vector2D.vec2DNormalize(steeringForce);
    }
    
    /**
     * tag all vehicles within range
     */
    private void tagNeighbors(boolean tag) {
        if (tag) {
            EntityFunctionTemplates.tagNeighbors(vehicle, vehicle.getWorld().getVehicleList(), Params.VIEW_DISTANCE);
        }
    }
    /**
     * calculate neighbors in cell-space
     */
    private void calculateNeighbors(boolean calculate) {
       if (calculate) {
           vehicle.getWorld().getCellSpace().calculateNeighbors(vehicle.getPos(), Params.VIEW_DISTANCE);
       }
    }
    
    /**
     ***************************** Getters & Setters *****************************  
     */
    public void setOffsetPursuit(Vector2D offset) {
        this.offsetPursuit = offset;
    }
    public Vector2D getOffsetPursuit() {
        return offsetPursuit;
    }
    
    public void setPath(Path newPath) {
        path = newPath;
    }
    public Path getPath() {
        return path;
    }

    public Vector2D getTargetMove() {
        return targetMove;
    }
    public void setTargetMove(Vector2D targetMove) {
        this.targetMove = targetMove;
    }

    public Vehicle getTargetAgent1() {
        return targetAgent1;
    }
    public void setTargetAgent1(Vehicle targetAgent1) {
        this.targetAgent1 = targetAgent1;
    }

    public Vehicle getTargetAgent2() {
        return targetAgent2;
    }
    public void setTargetAgent2(Vehicle targetAgent2) {
        this.targetAgent2 = targetAgent2;
    }
              
}
