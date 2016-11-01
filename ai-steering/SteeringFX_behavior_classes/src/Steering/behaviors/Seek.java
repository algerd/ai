
package Steering.behaviors;

import Steering.Params;
import Steering.Vehicle;
import common.D2.Vector2D;

public class Seek extends Behavior {
    
    private Vehicle vehicle;
    // целевая точка движения агента
    private Vector2D targetPos;
    private final double WEIGHT = Params.SEEK_WEIGHT;
    private final double PROBABILITY = Params.PR_SEEK;
    
    public Seek(Vehicle vehicle) {
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
            Vector2D desiredVelocity = Vector2D.mul(Vector2D.vec2DNormalize(Vector2D.sub(targetPos, vehicle.getPos())),
                    vehicle.getMaxSpeed());
            force = Vector2D.sub(desiredVelocity, vehicle.getVelocity());
        }
        catch(NullPointerException ex) {
            ex.printStackTrace();
        }
        return force;
    }
    
    public void setTargetPos(Vector2D targetPos) {
        this.targetPos = targetPos;
    }
       
}
