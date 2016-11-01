
package soccer;

import common.D2.Vector;
import common.D2.Geometry;

/**
 * Class to define a goal for a soccer pitch. 
 * The goal is defined by two 2D vectors representing the left and right posts.
 * Each time-step the method Scored should be called to determine if a goal has been scored.
 */
public class Goal {

    private Vector leftPost;
    private Vector rightPost;
    //a vector representing the facing direction of the goal
    private Vector facing;
    //the position of the center of the goal line
    private Vector center;
    //each time Scored() detects a goal this is incremented
    private int numGoalsScored;

    public Goal(Vector left, Vector right, Vector facing) {
        this.leftPost = left;
        this.rightPost = right;
        this.center = left.addn(right).divn(2.0);
        this.facing = facing;
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
