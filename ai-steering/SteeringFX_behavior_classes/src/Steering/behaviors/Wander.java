
package Steering.behaviors;

import Steering.Params;
import Steering.Vehicle;
import common.D2.Transformation;
import common.D2.Vector2D;
import common.misc.Utils;

public class Wander extends Behavior {
    
    private Vehicle vehicle;
    private final double WEIGHT = Params.SEEK_WEIGHT;
    private final double PROBABILITY = Params.PR_SEEK;
    private final double JITTER = Params.WANDER_JITTER_PER_SEC;
    private final double RAD = Params.WANDER_RAD;
    private final double DIST = Params.WANDER_DIST; 
    
    public Wander(Vehicle vehicle) {
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
     * This behavior makes the agent wander about randomly
     */
    @Override
    public Vector2D getForce() {
        //this behavior is dependent on the update rate, so this line must be included when using time independent framerate.
        double jitterThisTimeSlice = JITTER * vehicle.getTimeElapsed();

        double theta = Utils.RandFloat() * Utils.TwoPi;
        //create a vector to a target position on the wander circle
        Vector2D wanderTarget = new Vector2D(RAD * Math.cos(theta), RAD * Math.sin(theta));
        
        //first, add a small random vector to the target's position
        wanderTarget.add(new Vector2D(Utils.RandomClamped() * jitterThisTimeSlice, Utils.RandomClamped() * jitterThisTimeSlice));

        //reproject this new vector back on to a unit circle
        wanderTarget.normalize();

        //increase the length of the vector to the same as the radius of the wander circle
        wanderTarget.mul(RAD);

        //move the target into a position WanderDist in front of the agent
        Vector2D target = Vector2D.add(wanderTarget, new Vector2D(DIST, 0));

        //project the target into world space
        Vector2D transformTarget = Transformation.pointToWorldSpace(
                target,
                vehicle.getHeading(),
                vehicle.getSide(),
                vehicle.getPos());

        //and steer towards it
        return Vector2D.sub(transformTarget, vehicle.getPos());
    }
  
}
