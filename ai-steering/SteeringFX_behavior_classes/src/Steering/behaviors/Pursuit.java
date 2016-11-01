
package Steering.behaviors;

import Steering.Params;
import Steering.Vehicle;
import common.D2.Vector2D;

public class Pursuit extends Behavior {
    
    private Vehicle vehicle;
    private Vehicle targetAgent;
    private Seek seek;
    private final double WEIGHT = Params.PURSUIT_WEIGHT;
    private final double PROBABILITY = Params.PR_PURSUIT;
    
    public Pursuit(Vehicle vehicle) {
        this.vehicle = vehicle;
        seek = new Seek(vehicle);
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
            if (targetAgent == null) {
                throw new NullPointerException("Target agent is null.");
            }  
               
            //if the evader is ahead and facing the agent then we can just seek for the evader's current position.
            Vector2D toEvader = Vector2D.sub(targetAgent.getPos(), vehicle.getPos());
            double relativeHeading = vehicle.getHeading().dot(targetAgent.getHeading());      
            //acos(0.95)=18 degs
            if ((toEvader.dot(vehicle.getHeading()) > 0) && (relativeHeading < -0.95)) {
                seek.setTargetPos(targetAgent.getPos());               
                force = seek.getForce();
            }
            else {
                // время, требуемое для прохождения дистанции до преследуемого с максимальной скоростью
                double time = toEvader.length() / (vehicle.getMaxSpeed() + targetAgent.getSpeed());
                // предполагаемая позиция преследуемого через время time
                Vector2D predicatePos = Vector2D.add(targetAgent.getPos(), Vector2D.mul(targetAgent.getVelocity(), time));       
                // возвращаем вектор скорости до предполагаемой позиции преследуемого
                seek.setTargetPos(predicatePos);               
                force = seek.getForce();
            }         
        }
        catch(NullPointerException ex) {
            ex.printStackTrace();
        }
        return force;    
    }

    public void setTargetAgent(Vehicle targetAgent) {
        this.targetAgent = targetAgent;
    }
        
}
