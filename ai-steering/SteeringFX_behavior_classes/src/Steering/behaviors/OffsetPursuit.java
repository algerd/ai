
package Steering.behaviors;

import Steering.Params;
import Steering.Vehicle;
import common.D2.Transformation;
import common.D2.Vector2D;

public class OffsetPursuit extends Behavior{
    
    private Vehicle vehicle;
    private Vehicle targetAgent;
    private Vector2D offset;
    private Arrive arrive;
    private double deceleration = Params.DECELERATION_FAST;
    private final double WEIGHT = Params.OFFSET_PURSUIT_WEIGHT;
    private final double PROBABILITY = Params.PR_OFFSET_PURSUIT;
    
    public OffsetPursuit(Vehicle vehicle) {
        this.vehicle = vehicle;
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
    
    /**
     * Produces a steering force that keeps a vehicle at a specified offsetPursuit from a leader vehicle
     */
    @Override
    public Vector2D getForce() {
        Vector2D force = null;
        try {
            if (targetAgent == null) {
                throw new NullPointerException("Target agent is null.");
            } 
            if (offset == null) {
                throw new NullPointerException("Offset is null.");
            }
            //calculate the offsetPursuit's position in world space
            Vector2D WorldOffsetPos = Transformation.pointToWorldSpace(
                    offset,
                    targetAgent.getHeading(),
                    targetAgent.getSide(),
                    targetAgent.getPos());

            Vector2D toOffset = Vector2D.sub(WorldOffsetPos, vehicle.getPos());

            //the lookahead time is propotional to the distance between the leader
            //and the pursuer; and is inversely proportional to the sum of both agent's velocities
            double time = toOffset.length() / (vehicle.getMaxSpeed() + targetAgent.getSpeed());
            Vector2D targetPos = Vector2D.add(WorldOffsetPos, Vector2D.mul(targetAgent.getVelocity(), time));
            
            //now arrive at the predicted future position of the offsetPursuit
            arrive.setDeceleration(Params.DECELERATION_FAST);
            arrive.setTargetPos(targetPos);
            force = arrive.getForce();
        }
        catch(NullPointerException ex) {
            ex.printStackTrace();
        }
        return force;
    }

    public void setTargetAgent(Vehicle targetAgent) {
        this.targetAgent = targetAgent;
    }

    public void setOffset(Vector2D offset) {
        this.offset = offset;
    }

    public void setDeceleration(double deceleration) {
        this.deceleration = deceleration;
    }
       
}
