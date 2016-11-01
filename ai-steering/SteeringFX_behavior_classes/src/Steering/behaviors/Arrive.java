
package Steering.behaviors;

import Steering.Params;
import Steering.Vehicle;
import common.D2.Vector2D;

public class Arrive extends Behavior {
    
    private Vehicle vehicle;
    // целевая точка движения агента
    private Vector2D targetPos;
    private double deceleration = Params.DECELERATION_NORMAL;
    private final double WEIGHT = Params.ARRIVE_WEIGHT;
    private final double PROBABILITY = Params.PR_ARRIVE;
    
    public Arrive(Vehicle vehicle) {
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
    
    @Override
    public Vector2D getForce() {
        Vector2D force = null;
        try {
            if (targetPos == null) {
                throw new NullPointerException("Target position is null.");
            }        
            Vector2D target = Vector2D.sub(targetPos, vehicle.getPos());           
            double dist = target.length();
            if (dist > 0) {         
                //calculate the speed required to reach the target given the desired deceleration
                //and make sure the velocity does not exceed the max
                double speed = Math.min(dist / deceleration, vehicle.getMaxSpeed());

                //from here proceed just like Seek except we don't need to normalize the ToTarget vector 
                //because we have already gone to the trouble of calculating its length: dist. 
                Vector2D desiredVelocity = Vector2D.mul(target, speed / dist);
                force = Vector2D.sub(desiredVelocity, vehicle.getVelocity());
            }
            else {
                force = new Vector2D(0, 0);
            }
        }
        catch(NullPointerException ex) {
            ex.printStackTrace();
        }
        return force;
    }
       
    public void setTargetPos(Vector2D targetPos) {
        this.targetPos = targetPos;
    }

    public void setDeceleration(double deceleration) {
        this.deceleration = deceleration;
    }
        
}
