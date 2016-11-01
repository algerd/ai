
package Steering.behaviors;

import Steering.BaseGameEntity;
import Steering.Obstacle;
import Steering.Params;
import Steering.Vehicle;
import common.D2.Vector2D;
import common.misc.Utils;
import java.util.List;
import java.util.ListIterator;

/**
 * Спрятаться от охотника за объектом-препятствием.
 */
public class Hide extends Behavior {
    
    private Vehicle vehicle;
    private Vehicle hunter;
    private Evade evade;
    private Arrive arrive;
    private double deceleration = Params.DECELERATION_FAST;
    private Vector2D targetPos;
    private final double WEIGHT = Params.HIDE_WEIGHT;
    private final double PROBABILITY = Params.PR_HIDE;
    
    public Hide(Vehicle vehicle) {
        this.vehicle = vehicle;
        evade = new Evade(vehicle);
        arrive = new Arrive(vehicle);
    }
      
    @Override
    public Vector2D getWeightForce() {
        return Vector2D.mul(getForce(), WEIGHT);
    }
    
    @Override
    public Vector2D getDitheredForce() {
        return Vector2D.mul(getForce(), WEIGHT/PROBABILITY);
    }
        
    @Override
    public Vector2D getForce() {
        Vector2D force = null;
        try {
            if (hunter == null) {
                throw new NullPointerException("Hunter is null.");
            }          
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
                evade.setTargetAgent(hunter);
                force = evade.getForce();
            }
            //else use Arrive on the hiding spot
            else {             
                arrive.setTargetPos(bestHidingSpot);
                arrive.setDeceleration(deceleration);
                force = arrive.getForce();
            }           
        }
        catch(NullPointerException ex) {
            ex.printStackTrace();
        }
        return force;    
    }

    public void setHunter(Vehicle hunter) {
        this.hunter = hunter;
    }

    public void setDeceleration(double deceleration) {
        this.deceleration = deceleration;
    }
           
}
