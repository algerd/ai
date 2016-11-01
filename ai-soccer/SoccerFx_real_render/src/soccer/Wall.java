
package soccer;

import common.D2.Vector;
import java.util.ArrayList;
import java.util.List;

public class Wall {

    protected Vector vA = new Vector();
    protected Vector vB = new Vector();
    protected Vector vN = new Vector();

    public Wall(Vector A, Vector B) {
        vA = A;
        vB = B;
        calculateNormal();
    }

    public Wall(Vector A, Vector B, Vector N) {
        vA = A;
        vB = B;
        vN = N;
    }
  
    protected void calculateNormal() {
        Vector temp = vB.subn(vA).normalize();
        vN.x = -temp.y;
        vN.y = temp.x;
    }
    
    public static List<Wall> createWalls (Goal leftGoal, Goal rightGoal) {      
        List<Wall> wallList = new ArrayList<>();
        
        Vector topLeft = new Vector(0, 0);
        Vector topRight = new Vector(Field.LENGTH_FIELD, 0);
        Vector bottomRight = new Vector(Field.LENGTH_FIELD, Field.WIDTH_FIELD);
        Vector bottomLeft = new Vector(0, Field.WIDTH_FIELD);

        wallList.add(new Wall(bottomLeft, leftGoal.getRightPost()));
        wallList.add(new Wall(leftGoal.getLeftPost(), topLeft));
        wallList.add(new Wall(topLeft, topRight));
        wallList.add(new Wall(topRight, rightGoal.getLeftPost()));
        wallList.add(new Wall(rightGoal.getRightPost(), bottomRight));
        wallList.add(new Wall(bottomRight, bottomLeft));        
        return wallList;     
    }
   
    public Vector from() {
        return vA;
    }

    public Vector to() {
        return vB;
    }

    public Vector normal() {
        return vN;
    }

    public Vector center() {
        return vA.addn(vB).div(2.0);
    }

}
