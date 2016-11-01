
package soccer;

import common.D2.Vector;
import common.D2.Matrix;

/**
 * A base class defining an entity that moves. 
 * The entity has a local coordinate system and members for defining its mass and velocity.
 */
abstract public class MovingEntity extends BaseGameEntity {

    protected Vector velocity = new Vector(); 
    protected Vector heading = new Vector(1, 1);   //a normalized vector 
    protected Vector position = new Vector();
    protected double mass;
    protected double maxSpeed;
    protected double maxAcceleration;
    protected double massForce;       
    protected double maxTurnRate;  
    protected double boundingRadius;

    public MovingEntity(double radius, double maxSpeed, double mass, double turnRate, double maxAcceleration) {      
        this.mass = mass;
        this.maxSpeed = maxSpeed;
        this.maxTurnRate = turnRate;
        this.maxAcceleration = maxAcceleration;           
        this.boundingRadius = radius; 
        this.massForce = Params.PLAYER_COEFF_FORCE / mass;
    }
    
    public MovingEntity() {}
        
    /**
     *  given a target position, this method rotates the entity's heading vector 
     *  by an amount not greater than m_dMaxTurnRate until it directly faces the target.
     *  @return boolean player rotated
     */
    public boolean rotateHeading(Vector target) {
        Vector toTarget = target.subn(position).normalizen();
        double angle = Math.acos(heading.dot(toTarget));
        
        if (Double.isNaN(angle) || angle < 0.0001) { 
            return false;
        }
        if (angle > maxTurnRate) {
            angle = maxTurnRate;
        }        
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.rotate(angle * heading.sign(toTarget));
        rotationMatrix.transformVector2Ds(heading);
        rotationMatrix.transformVector2Ds(velocity);
        return true;
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

    public double getMaxAcceleration() {
        return maxAcceleration;
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

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public double getBoundingRadius() {
        return boundingRadius;
    }

    public void setBoundingRadius(double boundingRadius) {
        this.boundingRadius = boundingRadius;
    }
      
}
