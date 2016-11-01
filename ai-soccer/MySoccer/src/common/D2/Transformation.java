
package common.D2;

import java.util.List;
import common.misc.CppToJava;
import java.util.ArrayList;

/**
 *  Functions for converting 2D vectors between World and Local space.
 */
public class Transformation {
    
    /**
     * given a std::vector of 2D vectors, a position, orientation and scale,
     * this function transforms the 2D vectors into the object's world space.
     */
    public static List<Vector> worldTransform(
            List<Vector> points,
            Vector pos,
            Vector forward,
            Vector side,
            Vector scale) 
    {
        //copy the original vertices into the buffer about to be transformed
        List<Vector> tranVector2Ds = CppToJava.clone(points);

        //create a transformation matrix
        Matrix matTransform = new Matrix();

        //scale
        if ((scale.x != 1.0) || (scale.y != 1.0)) {
            matTransform.scale(scale.x, scale.y);
        }
        //rotate
        matTransform.rotate(forward, side);
        //and translate
        matTransform.translate(pos.x, pos.y);
        //now transform the object's vertices
        matTransform.transformVector2Ds(tranVector2Ds);
        return tranVector2Ds;
    }

    /**
     *  given a std::vector of 2D vectors, a position and  orientation
     *  this function transforms the 2D vectors into the object's world space.
     */
    public static List<Vector> worldTransform(
            List<Vector> points,
            Vector pos,
            Vector forward,
            Vector side) 
    {
        //copy the original vertices into the buffer about to be transformed
        List<Vector> tranVector2Ds = CppToJava.clone(points);
        for(Vector v: points) {
            tranVector2Ds.add(v);
        }
        //create a transformation matrix
        Matrix matTransform = new Matrix();
        //rotate
        matTransform.rotate(forward, side);
        //and translate
        matTransform.translate(pos.x, pos.y);
        //now transform the object's vertices
        matTransform.transformVector2Ds(tranVector2Ds);
        return tranVector2Ds;
    }

    /**
     * Transforms a point from the agent's local space into world space.
     */
    public static Vector pointToWorldSpace(
            Vector point,
            Vector AgentHeading,
            Vector AgentSide,
            Vector AgentPosition) 
    {
        //make a copy of the point
        Vector TransPoint = new Vector(point);
        //create a transformation matrix
        Matrix matTransform = new Matrix();
        //rotate
        matTransform.rotate(AgentHeading, AgentSide);
        //and translate
        matTransform.translate(AgentPosition.x, AgentPosition.y);
        //now transform the vertices
        matTransform.transformVector2Ds(TransPoint);
        return TransPoint;
    }

    /**
     * Transforms a vector from the agent's local space into world space.
     */ 
    public static Vector vectorToWorldSpace(Vector vec, Vector agentHeading, Vector agentSide) {
        //make a copy of the point
        Vector transVec = new Vector(vec);
        //create a transformation matrix
        Matrix matTransform = new Matrix();
        //rotate
        matTransform.rotate(agentHeading, agentSide);
        //now transform the vertices
        matTransform.transformVector2Ds(transVec);
        return transVec;
    }

    /**
     * Point To Local Space.
     */
    public static Vector pointToLocalSpace(
            Vector point,
            Vector AgentHeading,
            Vector AgentSide,
            Vector AgentPosition) 
    {
        //make a copy of the point
        Vector TransPoint = new Vector(point);
        //create a transformation matrix
        Matrix matTransform = new Matrix();
        double Tx = -AgentPosition.dot(AgentHeading);
        double Ty = -AgentPosition.dot(AgentSide);

        //create the transformation matrix
        matTransform._11(AgentHeading.x);   matTransform._12(AgentSide.x);
        matTransform._21(AgentHeading.y);   matTransform._22(AgentSide.y);
        matTransform._31(Tx);               matTransform._32(Ty);

        //now transform the vertices
        matTransform.transformVector2Ds(TransPoint);
        return TransPoint;
    }
    
    /**
     * Vector To Local Space.
     */
    public static Vector vectorToLocalSpace(Vector vec, Vector AgentHeading, Vector AgentSide) {

        //make a copy of the point
        Vector TransPoint = new Vector(vec);
        //create a transformation matrix
        Matrix matTransform = new Matrix();
        //create the transformation matrix
        matTransform._11(AgentHeading.x);   matTransform._12(AgentSide.x);
        matTransform._21(AgentHeading.y);   matTransform._22(AgentSide.y);

        //now transform the vertices
        matTransform.transformVector2Ds(TransPoint);
        return TransPoint;
    }

    /**
     * rotates a vector ang rads around the origin.
     */
    public static void rotateAroundOrigin(Vector v, double ang) {
        //create a transformation matrix
        Matrix mat = new Matrix();
        //rotate
        mat.rotate(ang);
        //now transform the object's vertices
        mat.transformVector2Ds(v);
    }

    //  given an origin, a facing direction, a 'field of view' describing the 
    //  limit of the outer whiskers, a whisker length and the number of whiskers
    //  this method returns a vector containing the end positions of a series
    //  of whiskers radiating away from the origin and with equal dist between
    //  them. (like the spokes of a wheel clipped to a specific segment size)
    public static List<Vector> createWhiskers(int NumWhiskers,
            double WhiskerLength,
            double fov,
            Vector facing,
            Vector origin) 
    {
        //this is the magnitude of the angle separating each whisker
        double SectorSize = fov / (double) (NumWhiskers - 1);
        List<Vector> whiskers = new ArrayList<Vector>(NumWhiskers);
        Vector temp;
        double angle = -fov * 0.5;

        for (int w = 0; w < NumWhiskers; ++w) {
            //create the whisker extending outwards at this angle
            temp = facing;
            rotateAroundOrigin(temp, angle);
            whiskers.add(Vector.add(origin, Vector.mul(WhiskerLength, temp)));

            angle += SectorSize;
        }
        return whiskers;
    }
    
}