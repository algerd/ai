
package SimpleSoccer;

import common.D2.Vector;
import common.D2.Matrix;

/**
 * A base class defining an entity that moves. The entity has 
 * a local coordinate system and members for defining its mass and velocity.
 */
abstract public class MovingEntity extends BaseGameEntity {

    protected Vector velocity;
    //a normalized vector pointing in the direction the entity is heading. 
    protected Vector heading;
    //a vector perpendicular to the heading vector
    protected Vector side;
    protected double mass;
    //the maximum speed this entity may travel at.
    protected double maxSpeed;
    //the maximum force this entity can produce to power itself 
    protected double maxForce;
    //the maximum rate (radians per second)this vehicle can rotate         
    protected double maxTurnRate;

    public MovingEntity(Vector position,
            double radius,
            Vector velocity,
            double maxSpeed,
            Vector heading,
            double mass,
            Vector scale,
            double turnRate,
            double maxForce) 
    {      
        this.heading = new Vector(heading);
        this.velocity = new Vector(velocity);
        this.mass = mass;
        this.side = this.heading.perp();
        this.maxSpeed = maxSpeed;
        this.maxTurnRate = turnRate;
        this.maxForce = maxForce;     
        this.position = new Vector(position);
        this.boundingRadius = radius; 
        this.scale = new Vector(scale);
    }
    
    /**
     *  given a target position, this method rotates the entity's heading and
     *  side vectors by an amount not greater than m_dMaxTurnRate until it 
     *  directly faces the target.
     *  @return true when the heading is facing in the desired direction
     */
    public boolean rotateHeadingToFacePosition(Vector target) {
        Vector toTarget = Vector.normalize(Vector.sub(target, position));

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

        side = heading.perp();
        return false;
    }
    
    public boolean isSpeedMaxedOut() {
        return maxSpeed * maxSpeed >= velocity.lengthSq();
    }

    public double speed() {
        return velocity.length();
    }

    public double speedSq() {
        return velocity.lengthSq();
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

    public double getMass() {
        return mass;
    }

    public Vector getSide() {
        return side;
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

    public void setMaxForce(double mf) {
        maxForce = mf;
    }

    public Vector getHeading() {
        return heading;
    }
    
    public void setHeading(Vector new_heading) {       
        heading = new_heading;
        //the side vector must always be perpendicular to the heading
        side = heading.perp();
    }

    double getMaxTurnRate() {
        return maxTurnRate;
    }

    void setMaxTurnRate(double val) {
        maxTurnRate = val;
    }
   
}
