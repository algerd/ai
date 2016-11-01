
package Steering.behaviors;

import Steering.Params;
import Steering.Vehicle;
import common.D2.Vector2D;

public class Seek {
    
    private Vehicle vehicle;
    // целевая точка движения агента
    private Vector2D targetMove;
    private double SEEK_WEIGHT = Params.SEEK_WEIGHT;
    private double PR_SEEK = Params.PR_SEEK;
    private boolean on;
      
    public Vector2D getWeightForce() {
        return Vector2D.mul(getForce(), SEEK_WEIGHT);
    }
    
    public Vector2D getDitheredForce() {
        return Vector2D.mul(getForce(), SEEK_WEIGHT/PR_SEEK);
    }
    
    public Vector2D getForce() {
        return getForce(targetMove);
    }
    
    public Vector2D getForce(Vector2D targetPos) {
        Vector2D force = null;
        try {
            if (targetPos == null) {
                throw new NullPointerException("Some error has occurred.");
            }
            Vector2D desiredVelocity = Vector2D.mul(
                    Vector2D.vec2DNormalize(Vector2D.sub(targetPos, vehicle.getPos())),
                    vehicle.getMaxSpeed());
            force = Vector2D.sub(desiredVelocity, vehicle.getVelocity());
        }
        catch(NullPointerException ex) {
            ex.printStackTrace();
        }
        return force;
    }
    
    public boolean isOn() {
        return on;
    }
    
    public void on() {
        this.on = true;
    }
    
    public void off() {
       this.on = false;
    }

    public void setTargetMove(Vector2D targetMove) {
        this.targetMove = targetMove;
    }
       
}
