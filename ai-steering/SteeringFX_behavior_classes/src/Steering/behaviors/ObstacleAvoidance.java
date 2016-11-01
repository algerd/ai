
package Steering.behaviors;

import Steering.BaseGameEntity;
import Steering.EntityFunctionTemplates;
import Steering.Obstacle;
import Steering.Params;
import Steering.Vehicle;
import common.D2.Transformation;
import common.D2.Vector2D;
import common.misc.Utils;
import java.util.List;
import java.util.ListIterator;

public class ObstacleAvoidance extends Behavior {
    
    private Vehicle vehicle;
    private final double WEIGHT = Params.OBSTACLE_AVOIDANCE_WEIGHT;
    private final double PROBABILITY = Params.PR_OBSTACLE_AVOIDANCE;
    private final double DETECTION_LENGTH = Params.MIN_DETECTION_BOX_LENGTH;
    
    public ObstacleAvoidance(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
    
    @Override
    public Vector2D getWeightForce() {
        return Vector2D.mul(getForce(), WEIGHT);
    }
    
    @Override
    public Vector2D getDitheredForce() {
        return Vector2D.mul(getForce(), WEIGHT/PROBABILITY);
    }
    
    /**
     *  Given a vector of obstacles, this method returns a steering force
     *  that will prevent the agent colliding with the closest obstacle
     */
    @Override
    public Vector2D getForce() {
        List<Obstacle> obstacleList = vehicle.getWorld().getObstacleList();
        
        //the detection box length is proportional to the agent's velocity
        double boxLength = DETECTION_LENGTH + (vehicle.getSpeed() / vehicle.getMaxSpeed()) * DETECTION_LENGTH;

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
}
