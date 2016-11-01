
package Steering.behaviors;

import Steering.Params;
import Steering.Vehicle;
import Steering.Wall;
import common.D2.Geometry;
import common.D2.Transformation;
import common.D2.Vector2D;
import common.misc.CppToJava;
import common.misc.Utils;
import java.util.ArrayList;
import java.util.List;

public class WallAvoidance extends Behavior{
    
    private Vehicle vehicle;
    private final double WEIGHT = Params.WALL_AVOIDANCE_WEIGHT;
    private final double PROBABILITY = Params.PR_WALL_AVOIDANCE;
    private final double DETECTION_FEELER_LENGTH = Params.WALL_DETECTION_FEELER_LENGTH;
    
    
    public WallAvoidance(Vehicle vehicle) {
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
     * This returns a steering force that will keep the agent away from any
     *  walls it may encounter
     */
    @Override
    public Vector2D getForce() {
        
        List<Wall> wallList = vehicle.getWorld().getWallList();      
        List<Vector2D> feelerList = new ArrayList<>(3);
        
        //feeler pointing straight in front
        feelerList.add(Vector2D.add(vehicle.getPos(), Vector2D.mul(DETECTION_FEELER_LENGTH, vehicle.getHeading())));

        //feeler to left
        Vector2D temp = new Vector2D(vehicle.getHeading());
        Transformation.vec2DRotateAroundOrigin(temp, Utils.HalfPi * 3.5f);
        feelerList.add(Vector2D.add(vehicle.getPos(), Vector2D.mul(DETECTION_FEELER_LENGTH / 2.0f, temp)));

        //feeler to right
        temp = new Vector2D(vehicle.getHeading());
        Transformation.vec2DRotateAroundOrigin(temp, Utils.HalfPi * 0.5f);
        feelerList.add(Vector2D.add(vehicle.getPos(), Vector2D.mul(DETECTION_FEELER_LENGTH / 2.0f, temp)));
   
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
}
