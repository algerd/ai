
package soccer;

import java.util.List;
import common.D2.Vector;
import common.D2.Geometry;
import common.D2.Transformation;
import common.Messaging.Telegram;
import common.Game.RandomUtil;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import render.ParamsRender;

public class SoccerBall extends MovingEntity {
    
    private Vector oldPos;
    private final List<Wall> wallList;    
    private List<Vector> positionList = new ArrayList<>();

    public SoccerBall(List<Wall> walls) {
        setBoundingRadius(Params.BALL_SIZE);
        setPosition(new Vector(Field.LENGTH_FIELD_CENTER, Field.WIDTH_FIELD_CENTER));
        wallList = walls;
        positionList.add(0, position);
    }

    /**
     * updates the ball physics, tests for any collisions and adjusts the ball's velocity accordingly.
     */
    @Override
    public void update() {
        oldPos = new Vector(position);
        testCollisionWithWalls(wallList);
        
        if (velocity.lengthSq() > Params.BALL_DECELERATION_TACT * Params.BALL_DECELERATION_TACT) {
            velocity.add(velocity.normalizen().muln(Params.BALL_DECELERATION_TACT));
            position.add(velocity); 
            positionList.add(new Vector(position));
        }
        else {
            // null - положение не изменилось - требует проверки на null при рендеринге
            positionList.add(null); 
        }                
    }

    /**
     * Установить начальную скорость мяча (м/такт) после удара в указанном направлении с указанной величиной.
     */
    public void setInitialVelocity(Vector direction, double initialVelocity) {
        velocity = direction.normalizen().mul(initialVelocity);
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
     * Given a force and a dist to cover given by two vectors, this method calculates how long it will take the ball to travel between the two points.
     * @param double initialVelocity - initial velocity of ball (m/tact)
     * @return double - number of tacts
     */
    public double timeToCoverDistance(Vector A, Vector B, double initialVelocity) {
        //calculate the velocity at B using the equation v^2 = u^2 + 2as
        double distAB = A.dist(B);
        double velocitySq = initialVelocity * initialVelocity + 2.0 * distAB * Params.BALL_DECELERATION_TACT;
        //if  (u^2 + 2as) is negative it means the ball cannot reach point B.
        if (velocitySq <= 0.0) {
            return -1.0;
        }
        // t = (v-u)/a
        return (Math.sqrt(velocitySq) - initialVelocity) / Params.BALL_DECELERATION_TACT;
    }

    /**
     * future position s = s0 + v*t + 1/2*a*t^2.
     */
    public Vector getFuturePosition(int timeTact) {
        Vector vt = velocity.muln(timeTact);
        double temp = Params.BALL_DECELERATION_TACT * timeTact * timeTact / 2;
        Vector scalarToVector = velocity.normalizen().muln(temp);
        return getPosition().addn(vt).add(scalarToVector); 
    }

    /**
     * this is used by players and goalkeepers to 'trap' a ball -- to stop it dead.
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
        double displacement = (Math.PI - Math.PI * Params.PLAYER_KICKING_ACCURACY) * RandomUtil.randomClamped();
        Vector toTarget = ballTarget.subn(ballPos);
        Transformation.rotateAroundOrigin(toTarget, displacement);
        return toTarget.add(ballPos);
    }
    
    /**
     * Renders the ball.
     */
    @Override
    public void render(GraphicsContext gc) {
        Vector posBallRender = position.muln(ParamsRender.SCALE_ANIMATION);
        double radiusRender = 3;
        gc.setFill(Color.BLACK);
        gc.fillOval(posBallRender.x - radiusRender, posBallRender.y - radiusRender, radiusRender * 2, radiusRender * 2);      
    }

    @Override
    public boolean handleMessage(final Telegram msg) {
        return false;
    }
    
    public Vector getOldPos() {
        return new Vector(oldPos);
    }

    public List<Vector> getPositionList() {
        return positionList;
    }

    public List<Wall> getWallList() {
        return wallList;
    }
          
}
