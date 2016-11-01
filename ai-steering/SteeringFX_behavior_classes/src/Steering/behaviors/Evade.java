
package Steering.behaviors;

import Steering.Params;
import Steering.Vehicle;
import common.D2.Vector2D;

public class Evade extends Behavior{
    
    private Vehicle vehicle;
    private Vehicle targetAgent;
    private Flee flee;
    private final double WEIGHT = Params.EVADE_WEIGHT;
    private final double PROBABILITY = Params.PR_EVADE;
    private final double THREAT_RANGE = 100.0;
    
    public Evade(Vehicle vehicle) {
        this.vehicle = vehicle;
        flee = new Flee(vehicle);
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
            // Not necessary to include the check for facing direction this time
            Vector2D toPursuer = Vector2D.sub(targetAgent.getPos(), vehicle.getPos());

            if (toPursuer.lengthSq() > THREAT_RANGE * THREAT_RANGE) {
                force = new Vector2D();
            }
            else {
                // время, требуемое для прохождения дистанции до преследователя с максимальной скоростью
                double time = toPursuer.length() / (vehicle.getMaxSpeed() + targetAgent.getSpeed());
                // предполагаемая позиция преследователя через время time
                Vector2D predicatePos = Vector2D.add(targetAgent.getPos(), Vector2D.mul(targetAgent.getVelocity(), time));
                // возвращаем вектор скорости, направленный против предполагаемой позиции преследователя (используем поведение Flee - бегство)               
                flee.setTargetPos(predicatePos);               
                force = flee.getForce();
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

    public Vehicle getTargetAgent() {
        return targetAgent;
    }
          
}
