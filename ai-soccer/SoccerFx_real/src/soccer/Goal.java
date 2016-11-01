
package soccer;

import common.D2.Vector;
import common.D2.Geometry;

/**
 * Class to define a goal for a soccer pitch. 
 * The goal is defined by two 2D vectors representing the left and right posts.
 * Each time-step the method Scored should be called to determine if a goal has been scored.
 */
public class Goal {
    
    public enum GoalSide {
        LEFT, RIGHT
    }

    private Vector leftPost;
    private Vector rightPost;
    //a vector representing the facing direction of the goal
    private Vector facing;
    //the position of the center of the goal line
    private Vector center;
    //each time Scored() detects a goal this is incremented
    private int numGoalsScored;

    public Goal(GoalSide side) {
        if (side == GoalSide.LEFT) {
            this.leftPost = new Vector(0, (Field.WIDTH_FIELD - Field.WIDTH_GATE) / 2);
            this.rightPost = new Vector(0, Field.WIDTH_FIELD - (Field.WIDTH_FIELD - Field.WIDTH_GATE) / 2);
            this.facing = new Vector(1, 0);  
        }
        if (side == GoalSide.RIGHT) {
            this.leftPost = new Vector(Field.LENGTH_FIELD, (Field.WIDTH_FIELD - Field.WIDTH_GATE) / 2);
            this.rightPost = new Vector(Field.LENGTH_FIELD, Field.WIDTH_FIELD - (Field.WIDTH_FIELD - Field.WIDTH_GATE) / 2);
            this.facing = new Vector(-1, 0);
        }
        this.center = leftPost.addn(rightPost).divn(2.0);
    }
    /**
     * Given the current ball position and the previous ball position,
     * this method returns true if the ball has crossed the goal line 
     * and increments m_iNumGoalsScored
     */
    public boolean scored(final SoccerBall ball) {
        if (Geometry.lineIntersection2D(ball.getPosition(), ball.getOldPos(), leftPost, rightPost)) {
            ++numGoalsScored;
            return true;
        }
        return false;
    }
    
    public void resetGoalsScored() {
        numGoalsScored = 0;
    }

    /**
     ************************ Getters & Setters. *****************************  
     */
    public Vector getCenter() {
        return new Vector(center);
    }

    public Vector getFacing() {
        return new Vector(facing);
    }

    public Vector getLeftPost() {
        return new Vector(leftPost);
    }

    public Vector getRightPost() {
        return new Vector(rightPost);
    }

    public int getNumGoalsScored() {
        return numGoalsScored;
    }
   
}
