
package generator;

import utils.Geometry;
import utils.Vector;

/**
 * The goal is defined by two 2D vectors representing the left and right posts.
 * Each time-step the method Scored should be called to determine if a goal has been scored.
 */
public class Goal {
    
    public enum GoalSide {
        LEFT, RIGHT
    }
    private int countGoals;     //each time Scored() detects a goal this is incremented
    private final Vector leftPost = new Vector();
    private final Vector rightPost = new Vector();  // вектор xz
    private final Vector topPost = new Vector();
    private final Vector facing = new Vector();      //a vector representing the facing direction of the goal  
    private final Vector center = new Vector();      //the position of the center of the goal line   

    public Goal(GoalSide side) {
        if (side == GoalSide.LEFT) {
            leftPost.set(0, (Field.WIDTH_FIELD - Field.WIDTH_GATE) / 2);
            rightPost.set(0, Field.WIDTH_FIELD - (Field.WIDTH_FIELD - Field.WIDTH_GATE) / 2);
            facing.set(1, 0);  
        }
        if (side == GoalSide.RIGHT) {
            leftPost.set(Field.LENGTH_FIELD, (Field.WIDTH_FIELD - Field.WIDTH_GATE) / 2);
            rightPost.set(Field.LENGTH_FIELD, Field.WIDTH_FIELD - (Field.WIDTH_FIELD - Field.WIDTH_GATE) / 2);
            facing.set(-1, 0);
        }
        topPost.set(0, Field.HEIGHT_GATE);   
        center.set(leftPost.addn(rightPost).divn(2.0));
    }
    /**
     * Given the current ball position and the previous ball position. 
     * @returns true if the ball has crossed the goal line and increments m_iNumGoalsScored
     */
    public boolean scored(final Ball ball) {
        if (Geometry.lineIntersection2D(ball.getPos().getVectorXY(), ball.getPrevPos().getVectorXY(), leftPost, rightPost) &&
            Geometry.lineIntersection2D(ball.getPos().getVectorXZ(), ball.getPrevPos().getVectorXZ(), new Vector(0, 0), topPost)) 
        {
            ++countGoals;
            return true;
        }
        return false;
    }
    
    public void resetGoalsScored() {
        countGoals = 0;
    }

    /************************** Getters & Setters. ******************************/
    
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
    
    public Vector getTopPost() {
        return new Vector(topPost);
    }

    public int getNumGoalsScored() {
        return countGoals;
    }
   
}
