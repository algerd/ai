package Steering.behaviors;

import Steering.Params;
import Steering.Vehicle;
import common.D2.Vector2D;

public class Interpose extends Behavior {
    
    private Vehicle vehicle;
    private Vehicle targetAgent1;
    private Vehicle targetAgent2;
    private Arrive arrive;
    private double deceleration = Params.DECELERATION_FAST;
    private final double WEIGHT = Params.INTERPOSE_WEIGHT;
    private final double PROBABILITY = Params.PR_INTERPOSE;
    
    public Interpose(Vehicle vehicle) {
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
     * Given two agents, this method returns a force that attempts to position the vehicle between them.
     */
    @Override
    public Vector2D getForce() {
         Vector2D force = null;
        try {
            if (targetAgent1 == null || targetAgent2 == null) {
                throw new NullPointerException("Target agents are nulls.");
            }       
            //first we need to figure out where the two agents are going to be at 
            //time T in the future. This is approximated by determining the time
            //taken to reach the mid way point at the current time at at max speed.
            Vector2D midPoint = Vector2D.div(Vector2D.add(targetAgent1.getPos(), targetAgent2.getPos()), 2.0);

            double time = Vector2D.vec2DDistance(vehicle.getPos(), midPoint) / vehicle.getMaxSpeed();

            //now we have T, we assume that agent A and agent B will continue on a
            //straight trajectory and extrapolate to get their future positions
            Vector2D APos = Vector2D.add(targetAgent1.getPos(), Vector2D.mul(targetAgent1.getVelocity(), time));
            Vector2D BPos = Vector2D.add(targetAgent2.getPos(), Vector2D.mul(targetAgent2.getVelocity(), time));

            //calculate the mid point of these predicted positions
            midPoint = Vector2D.div(Vector2D.add(APos, BPos), 2.0);
            //then steer to Arrive at it
            arrive.setTargetPos(midPoint);
            arrive.setDeceleration(deceleration);
            force = arrive.getForce();           
        }
        catch(NullPointerException ex) {
            ex.printStackTrace();
        }
        return force;
    }

    public void setTargetAgent1(Vehicle targetAgent1) {
        this.targetAgent1 = targetAgent1;
    }

    public void setTargetAgent2(Vehicle targetAgent2) {
        this.targetAgent2 = targetAgent2;
    }

    public void setDeceleration(double deceleration) {
        this.deceleration = deceleration;
    }
         
}
