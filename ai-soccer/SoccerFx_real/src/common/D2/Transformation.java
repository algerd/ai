
package common.D2;

import java.util.List;
import java.util.ArrayList;

/**
 *  Functions for converting 2D vectors between World and Local space.
 */
public class Transformation {
    
    /**
     * this function transforms the 2D vectors into the object's world space.
     */
    public static List<Vector> worldTransform(List<Vector> points, Vector pos, Vector heading) {
        //copy the original vertices
        List<Vector> tranVector2Ds = new ArrayList<>();
        for (Vector point : points){
            tranVector2Ds.add(new Vector(point));
        }       
        //create a transformation matrix
        Matrix matTransform = new Matrix();
        matTransform.rotate(heading, heading.perp());
        matTransform.translate(pos.x, pos.y);
        //now transform the object's vertices
        matTransform.transformVector2Ds(tranVector2Ds);
        return tranVector2Ds;
    }

    /**
     * Point To Local Space.
     */
    public static Vector pointToLocalSpace(Vector point, Vector heading, Vector position) {
        Vector side = heading.perp();
        //make a copy of the point
        Vector transPoint = new Vector(point);
        //create a transformation matrix
        Matrix matTransform = new Matrix();
        double tx = -position.dot(heading);
        double ty = -position.dot(side);

        //create the transformation matrix
        matTransform._11(heading.x);   matTransform._12(side.x);
        matTransform._21(heading.y);   matTransform._22(side.y);
        matTransform._31(tx);          matTransform._32(ty);

        //now transform the vertices
        matTransform.transformVector2Ds(transPoint);
        return transPoint;
    }
    
    /**
     * rotates a vector around the origin.
     */
    public static void rotateAroundOrigin(Vector v, double ang) {
        Matrix mat = new Matrix();
        mat.rotate(ang);
        //now transform the object's vertices
        mat.transformVector2Ds(v);
    }
   
}
