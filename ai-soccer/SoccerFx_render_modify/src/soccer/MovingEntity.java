
package soccer;

import common.D2.Vector;
import common.D2.Matrix;

/**
 * A base class defining an entity that moves. 
 * The entity has a local coordinate system and members for defining its mass and velocity.
 */
abstract public class MovingEntity extends BaseGameEntity {

    protected Vector velocity;
    //a normalized vector pointing in the direction the entity is heading. 
    protected Vector heading;
    protected double mass;
    protected double maxSpeed;
    protected double maxForce;
    //the maximum rate (radians per second)this vehicle can rotate         
    protected double maxTurnRate;

    public MovingEntity(
            Vector position,
            double radius,
            Vector velocity,
            double maxSpeed,
            Vector heading,
            double mass,
            double turnRate,
            double maxForce) 
    {      
        this.heading = new Vector(heading);
        this.position = new Vector(position);
        this.velocity = new Vector(velocity);
        this.mass = mass;
        this.maxSpeed = maxSpeed;
        this.maxTurnRate = turnRate;
        this.maxForce = maxForce;           
        this.boundingRadius = radius;       
    }
    
    /**
     *  given a target position, this method rotates the entity's heading and
     *  side vectors by an amount not greater than m_dMaxTurnRate until it 
     *  directly faces the target.
     *  @return true when the heading is facing in the desired direction
     */
    public boolean rotateHeadingToFacePosition(Vector target) {
        Vector toTarget = target.subn(position).normalizen();

        //first determine the angle between the heading vector and the target
        double angle = Math.acos(heading.dot(toTarget));
        
        //sometimes m_vHeading.Dot(toTarget) == 1.000000002
        if(Double.isNaN(angle)) { 
            angle = 0;
        }
        //return true if the player is facing the target
        if (angle < 0.00001) {
            return true;
        }
        //clamp the amount to turn to the max turn rate
        if (angle > maxTurnRate) {
            angle = maxTurnRate;
        }
        //The next few lines use a rotation matrix to rotate the player's heading vector accordingly
        Matrix rotationMatrix = new Matrix();

        //notice how the direction of rotation has to be determined when creating the rotation matrix
        rotationMatrix.rotate(angle * heading.sign(toTarget));
        rotationMatrix.transformVector2Ds(heading);
        rotationMatrix.transformVector2Ds(velocity);
        return false;
    } 

    /**
     ************************ Getters & Setters. *****************************  
     */
    public Vector getVelocity() {
        return new Vector(velocity);
    }

    public void setVelocity(Vector newVel) {
        velocity = newVel;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double newSpeed) {
        maxSpeed = newSpeed;
    }

    public double getMaxForce() {
        return maxForce;
    }

    public Vector getHeading() {
        return heading;
    }
    
    public void setHeading(Vector newHeading) {       
        heading = newHeading;
    }

    double getMaxTurnRate() {
        return maxTurnRate;
    }
   
}
