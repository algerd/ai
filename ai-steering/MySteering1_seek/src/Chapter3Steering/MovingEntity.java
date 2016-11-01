/**
 *  Desc:   A base class defining an entity that moves. The entity has 
 *          a local coordinate system and members for defining its
 *          mass and velocity.
 */ 
package Chapter3Steering;

import common.D2.Vector2D;
import common.D2.Matrix2D;

public class MovingEntity extends BaseGameEntity {

    protected Vector2D velocity;
    //a normalized vector pointing in the direction the entity is heading. 
    protected Vector2D heading;
    //a vector perpendicular to the heading vector
    protected Vector2D side;
    protected double mass;
    //the maximum speed this entity may travel at.
    protected double maxSpeed;
    //the maximum force this entity can produce to power itself (think rockets and thrust)
    protected double maxForce;
    //the maximum rate (radians per second)this vehicle can rotate         
    protected double maxTurnRate;

    public MovingEntity(Vector2D position,
            double radius,
            Vector2D velocity,
            double maxSpeed,
            Vector2D heading,
            double mass,
            Vector2D scale,
            double turnRate,
            double maxForce) {
        super(0, position, radius);
        this.heading = new Vector2D(heading);
        this.velocity = new Vector2D(velocity);
        this.mass = mass;
        this.side = this.heading.perp();
        this.maxSpeed = maxSpeed;
        this.maxTurnRate = turnRate;
        this.maxForce = maxForce;
        this.scale = new Vector2D(scale);
    }
    
    /**
     *  given a target position, this method rotates the entity's heading and
     *  side vectors by an amount not greater than m_dMaxTurnRate until it
     *  directly faces the target.
     *  @return true when the heading is facing in the desired direction
     */
    public boolean rotateHeadingToFacePosition(Vector2D target) {
        Vector2D toTarget = Vector2D.vec2DNormalize(Vector2D.sub(target, pos));

        //first determine the angle between the heading vector and the target
        double angle = Math.acos(heading.dot(toTarget));
        if(Double.isNaN(angle))  {
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
        Matrix2D RotationMatrix = new Matrix2D();

        //notice how the direction of rotation has to be determined when creating the rotation matrix
        RotationMatrix.rotate(angle * heading.sign(toTarget));
        RotationMatrix.transformVector2Ds(heading);
        RotationMatrix.transformVector2Ds(velocity);

        //finally recreate side
        side = heading.perp();
        return false;
    }

    public Vector2D getVelocity() {
        return new Vector2D(velocity);
    }

    public void setVelocity(Vector2D newVel) {
        velocity = newVel;
    }

    public double getMass() {
        return mass;
    }

    public Vector2D getSide() {
        return side;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double new_speed) {
        maxSpeed = new_speed;
    }

    public double getMaxForce() {
        return maxForce;
    }

    public void setMaxForce(double mf) {
        maxForce = mf;
    }

    public boolean isSpeedMaxedOut() {
        return maxSpeed * maxSpeed >= velocity.lengthSq();
    }

    public double getSpeed() {
        return velocity.length();
    }

    public double grtSpeedSq() {
        return velocity.lengthSq();
    }

    public Vector2D getHeading() {
        return heading;
    }

    double getMaxTurnRate() {
        return maxTurnRate;
    }

    void setMaxTurnRate(double val) {
        maxTurnRate = val;
    }

    /**
     *  first checks that the given heading is not a vector of zero length. If the
     *  new heading is valid this fumction sets the entity's heading and side vectors accordingly
     */
    public void setHeading(Vector2D new_heading) {
        //assert ((new_heading.lengthSq() - 1.0) < 0.00001);
        heading = new_heading;
        //the side vector must always be perpendicular to the heading
        side = heading.perp();
    }
}
