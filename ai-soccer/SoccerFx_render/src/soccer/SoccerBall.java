
package soccer;

import common.D2.Wall;
import java.util.List;
import common.D2.Vector;
import common.D2.Geometry;
import common.D2.Transformation;
import common.Messaging.Telegram;
import common.misc.Utils;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SoccerBall extends MovingEntity {
    
    private Vector oldPos;
    private final List<Wall> pitchBoundary;    
    // Хранилище векторов положения мяча
    private List<Vector> positionList = new ArrayList<>();

    public SoccerBall(Vector pos, double ballSize, double mass, List<Wall> pitchBoundary) {
        super(pos, ballSize, new Vector(0, 0), -1.0, new Vector(0, 1), mass, new Vector(1.0, 1.0), 0, 0);                     
        this.pitchBoundary = pitchBoundary;
        positionList.add(0, pos);
    }

    /**
     * updates the ball physics, tests for any collisions and adjusts the ball's velocity accordingly.
     */
    @Override
    public void update() {
        oldPos = new Vector(position);
        testCollisionWithWalls(pitchBoundary);
        
        if (velocity.lengthSq() > ParamLoader.getInstance().Friction * ParamLoader.getInstance().Friction) {
            velocity.add(velocity.normalizen().muln(ParamLoader.getInstance().Friction));
            position.add(velocity); 
            // добавить новое положение мяча в хранилище 
            positionList.add(new Vector(position));
        }
        else {
            // null - положение не изменилось - требует проверки на null при рендеринге
            positionList.add(null); 
        }                
    }

    /**
     * Renders the ball.
     */
    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillOval(position.x - boundingRadius, position.y - boundingRadius, boundingRadius * 2, boundingRadius * 2);      
    }

    @Override
    public boolean handleMessage(final Telegram msg) {
        return false;
    }

    /**
     * applies a force to the ball in the direction of heading. Truncates
     * the new velocity to make sure it doesn't exceed the max allowable.
     */
    public void kick(Vector direction, double force) {
        //ensure direction is normalized
        direction.normalize();
        //calculate the acceleration: direction*force/mass
        Vector acceleration = direction.muln(force).div(mass);
        //update the velocity
        velocity = acceleration;
    }
    
    /**
     * tests to see if the ball has collided with a ball and reflects the ball's velocity accordingly.
     */
    private void testCollisionWithWalls(final List<Wall> walls) {
        
        Vector velocityNormal = velocity.normalizen();
        Vector intersectionPoint = new Vector();
        Vector collisionPoint = new Vector();

        double distToIntersection = Float.MAX_VALUE;
        Wall closestWall = null;
        /**
         * iterate through each wall and calculate if the ball intersects.
         * If it does then store the index into the closest intersecting wall
         */
        for( Wall wall : walls) {
            //assuming a collision if the ball continued on its current heading 
            //calculate the point on the ball that would hit the wall. This is 
            //simply the wall's normal(inversed) multiplied by the ball's radius
            //and added to the balls center (its position)
            Vector thisCollisionPoint = getPosition().subn(wall.normal().muln(getBoundingRadius()));
            
            //calculate exactly where the collision point will hit the plane    
            if (Geometry.whereIsPoint(thisCollisionPoint, wall.from(), wall.normal()) == Geometry.SpanType.PLANE_BACKSIDE) {
                double distToWall = Geometry.getDistanceToRayPlaneIntersection(
                        thisCollisionPoint,
                        wall.normal(),
                        wall.from(),
                        wall.normal());
                intersectionPoint = wall.normal().muln(distToWall).addn(thisCollisionPoint);
            } 
            else {
                double distToWall = Geometry.getDistanceToRayPlaneIntersection(
                        thisCollisionPoint,
                        velocityNormal,
                        wall.from(),
                        wall.normal());
                intersectionPoint = velocityNormal.muln(distToWall).addn(thisCollisionPoint);
            }
            //check to make sure the intersection point is actually on the line segment
            boolean OnLineSegment = false;

            if (Geometry.lineIntersection2D(
                    wall.from(),
                    wall.to(),
                    thisCollisionPoint.subn(wall.normal().muln(20.0)),
                    thisCollisionPoint.addn(wall.normal().muln(20.0)))) {                    
                OnLineSegment = true;
            }
            //Note, there is no test for collision with the end of a line segment
            //now check to see if the collision point is within range of the
            //velocity vector. [work in dist squared to avoid sqrt] and if it
            //is the closest hit found so far. 
            //If it is that means the ball will collide with the wall sometime
            //between this time step and the next one.
            double distSq = thisCollisionPoint.distSq(intersectionPoint);
            if ((distSq <= velocity.lengthSq()) && (distSq < distToIntersection) && OnLineSegment) {
                distToIntersection = distSq;
                closestWall = wall;
                collisionPoint = intersectionPoint;
            }
        }
        //to prevent having to calculate the exact time of collision we
        //can just check if the velocity is opposite to the wall normal
        //before reflecting it. This prevents the case where there is overshoot
        //and the ball gets reflected back over the line before it has completely
        //reentered the playing area.
        if ((closestWall != null) && velocityNormal.dot(closestWall.normal()) < 0) {
            velocity.reflect(closestWall.normal());
        }
    }

    /**
     * Given a force and a dist to cover given by two vectors, this
     * method calculates how long it will take the ball to travel between the two points
     */
    public double timeToCoverDistance(Vector A, Vector B, double force) {
        //this will be the velocity of the ball in the next time step *if* the player was to make the pass. 
        double speed = force / mass;

        //calculate the velocity at B using the equation v^2 = u^2 + 2as
        double distanceToCover = A.dist(B);
        double velSq = speed * speed + 2.0 * distanceToCover * ParamLoader.getInstance().Friction;

        //if  (u^2 + 2as) is negative it means the ball cannot reach point B.
        if (velSq <= 0.0) {
            return -1.0;
        }
        //it IS possible for the ball to reach B and we know its speed when it
        //gets there, so now it's easy to calculate the time using the equation
        // t = (v-u)/a
        return (Math.sqrt(velSq) - speed) / ParamLoader.getInstance().Friction;
    }

    /**
     * given a time this method returns the ball position at that time in the future
     */
    public Vector futurePosition(double time) {
        //using the equation s = ut + 1/2at^2, where s = dist, a = friction, u=start velocity

        //calculate the ut term, which is a vector
        Vector ut = velocity.muln(time);

        //calculate the 1/2at^2 term, which is scalar
        double half_a_t_squared = 0.5 * ParamLoader.getInstance().Friction * time * time;

        //turn the scalar quantity into a vector by multiplying the value with
        //the normalized velocity vector (because that gives the direction)
        Vector scalarToVector = velocity.normalizen().muln(half_a_t_squared);

        //the predicted position is the balls position plus these two terms
        return getPosition().addn(ut).add(scalarToVector);
    }

    /**
     * this is used by players and goalkeepers to 'trap' a ball -- to stop
     * it dead. That player is then assumed to be in possession of the ball
     * and m_pOwner is adjusted accordingly
     */
    public void trap() {
        velocity.zero();
    }

    /**
     * positions the ball at the desired location and sets the ball's velocity to zero.
     */
    public void placeAtPosition(Vector NewPos) {
        position = new Vector(NewPos);
        oldPos = new Vector(position);
        velocity.zero();
    }

    /**
     *  this can be used to vary the accuracy of a player's kick. Just call it 
     *  prior to kicking the ball using the ball's position and the ball target as parameters.
     */
    public static Vector addNoiseToKick(Vector ballPos, Vector ballTarget) {
        double displacement = (Math.PI - Math.PI * ParamLoader.getInstance().PlayerKickingAccuracy) * Utils.randomClamped();
        Vector toTarget = ballTarget.subn(ballPos);
        Transformation.rotateAroundOrigin(toTarget, displacement);
        return toTarget.add(ballPos);
    }
    
    public Vector getOldPos() {
        return new Vector(oldPos);
    }

    public List<Vector> getPositionList() {
        return positionList;
    }
       
}
