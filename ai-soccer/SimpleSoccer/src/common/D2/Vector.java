
package common.D2;

import common.misc.Utils;
import java.util.Scanner;
import java.io.InputStream;
import common.Windows;
import common.misc.StreamUtilityFunction;

public class Vector {

    public double x;
    public double y;
    public static final int clockwise = 1;
    public static final int anticlockwise = -1;

    public Vector() {
        x = 0.0;
        y = 0.0;
    }

    public Vector(double a, double b) {
        x = a;
        y = b;
    }

    public Vector(Vector v) {
        super();
        this.set(v);
    }

    public Vector set(Vector v) {
        this.x = v.x;
        this.y = v.y;
        return this;
    }
    
    //sets x and y to zero
    public void zero() {
        x = 0.0;
        y = 0.0;
    }

    //returns true if both x and y are zero
    public boolean isZero() {
        return (x * x + y * y) < Utils.MinDouble;
    }

    /**
     *   returns the length of a 2D vector
     */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    //returns the squared length of the vector (thereby avoiding the sqrt)
    public double lengthSq() {
        return (x * x + y * y);
    }

    /**
     *   normalizes a 2D Vector
     */
    public void normalize() {
        double vector_length = this.length();

        if (vector_length > Utils.EpsilonDouble) {
            this.x /= vector_length;
            this.y /= vector_length;
        }
    }

    /**
     * calculates the dot product
     * @param v2
     * @return  dot product
     */
    public double dot(Vector v2) {
        return x * v2.x + y * v2.y;
    }

    /**
    /* returns positive if v2 is clockwise of this vector,
    /* negative if anticlockwise (assuming the Y axis is pointing down,
    /* X axis to right like a Window app)
     */
    public int sign(Vector v2) {
        return (y * v2.x > x * v2.y) ? anticlockwise : clockwise;
    }

    /**
     * returns the vector that is perpendicular to this one.
     */
    public Vector perp() {
        return new Vector(-y, x);
    }

    /**
     * adjusts x and y so that the length of the vector does not exceed max
     * truncates a vector so that its length does not exceed max
     * @param max 
     */
    public void truncate(double max) {
        if (this.length() > max) {
            this.normalize();
            this.mul(max);
        }
    }

    /**
     * calculates the euclidean dist between two vectors
     * @param v2
     * @return the dist between this vector and th one passed as a parameter
     */
    public double dist(Vector v2) {
        double ySeparation = v2.y - y;
        double xSeparation = v2.x - x;
        return Math.sqrt(ySeparation * ySeparation + xSeparation * xSeparation);
    }

    /** 
     * squared version of dist.
     * calculates the euclidean dist squared between two vectors 
     * @param v2
     */
    public double distSq(Vector v2) {
        double ySeparation = v2.y - y;
        double xSeparation = v2.x - x;
        return ySeparation * ySeparation + xSeparation * xSeparation;
    }

    /**
     *  given a normalized vector this method reflects the vector it
     *  is operating upon. (like the path of a ball bouncing off a wall)
     * @param norm 
     */
    public void reflect(Vector norm) {
        this.add(norm.reverse().mul(2.0 * dot(norm)));
    }

    /**
     * @return the vector that is the reverse of this vector 
     */
    public Vector reverse() {
        return new Vector(-this.x, -this.y);
    }

    //we need some overloaded operators
    public Vector add(Vector rhs) {
        x += rhs.x;
        y += rhs.y;
        return this;
    }

    public Vector sub(Vector rhs) {
        x -= rhs.x;
        y -= rhs.y;
        return this;
    }

    public Vector mul(double rhs) {
        x *= rhs;
        y *= rhs;
        return this;
    }

    public Vector div(double rhs) {
        x /= rhs;
        y /= rhs;
        return this;
    }

    public boolean isEqual(Vector rhs) {
        return (Utils.isEqual(x, rhs.x) && Utils.isEqual(y, rhs.y));
    }
    
    // operator !=
    public boolean notEqual(Vector rhs) {
        return (x != rhs.x) || (y != rhs.y);
    }


    static public Vector mul(Vector lhs, double rhs) {
        Vector result = new Vector(lhs);
        result.mul(rhs);
        return result;
    }

    static public Vector mul(double lhs, Vector rhs) {
        Vector result = new Vector(rhs);
        result.mul(lhs);
        return result;
    }

    static public Vector sub(Vector lhs, Vector rhs) {
        Vector result = new Vector(lhs);
        result.x -= rhs.x;
        result.y -= rhs.y;
        return result;
    }

    static public Vector add(Vector lhs, Vector rhs) {
        Vector result = new Vector(lhs);
        result.x += rhs.x;
        result.y += rhs.y;
        return result;
    }

    static public Vector div(Vector lhs, double val) {
        Vector result = new Vector(lhs);
        result.x /= val;
        result.y /= val;
        return result;
    }

    @Override
    public String toString() {
        return " " + StreamUtilityFunction.ttos(this.x,2) + " " + StreamUtilityFunction.ttos(this.y,2);
    }

    public Vector read(InputStream in) {
        Scanner sc = new Scanner(in);
        this.x = sc.nextDouble();
        this.y = sc.nextDouble();
        return this;
    }

    public static Vector normalize(Vector v) {
        Vector vec = new Vector(v);
        double vectorLength = vec.length();
        if (vectorLength > Utils.EpsilonDouble) {
            vec.x /= vectorLength;
            vec.y /= vectorLength;
        }
        return vec;
    }

    public static double dist(Vector v1, Vector v2) {
        double ySeparation = v2.y - v1.y;
        double xSeparation = v2.x - v1.x;
        return Math.sqrt(ySeparation * ySeparation + xSeparation * xSeparation);
    }

    public static double distSq(Vector v1, Vector v2) {
        double ySeparation = v2.y - v1.y;
        double xSeparation = v2.x - v1.x;
        return ySeparation * ySeparation + xSeparation * xSeparation;
    }

    public static double length(Vector v) {
        return Math.sqrt(v.x * v.x + v.y * v.y);
    }

    public static double lengthSq(Vector v) {
        return (v.x * v.x + v.y * v.y);
    }

    public static Vector pointsToVector(Windows.POINTS p) {
        return new Vector(p.x, p.y);
    }

    public static Vector pointToVector(Windows.POINT p) {
        return new Vector((double) p.x, (double) p.y);
    }

    public static Windows.POINTS VectorToPOINTS(Vector v) {
        Windows.POINTS p = new Windows.POINTS();
        p.x = (short) v.x;
        p.y = (short) v.y;
        return p;
    }

    public static Windows.POINT VectorToPOINT(Vector v) {
        Windows.POINT p = new Windows.POINT();
        p.x = (long) v.x;
        p.y = (long) v.y;
        return p;
    }

    ///////////////////////////////////////////////////////////////////////////////
    //treats a window as a toroid
    public static void wrapAround(Vector pos, int MaxX, int MaxY) {
        if (pos.x > MaxX) {
            pos.x = 0.0;
        }
        if (pos.x < 0) {
            pos.x = (double) MaxX;
        }
        if (pos.y < 0) {
            pos.y = (double) MaxY;
        }
        if (pos.y > MaxY) {
            pos.y = 0.0;
        }
    }

    /**
     * returns true if the point p is not inside the region defined by top_left and bot_rgt
     */
    public static boolean notInsideRegion(Vector p, Vector top_left, Vector bot_rgt) {
        return (p.x < top_left.x) || (p.x > bot_rgt.x) || (p.y < top_left.y) || (p.y > bot_rgt.y);
    }

    public static boolean insideRegion(Vector p, Vector top_left, Vector bot_rgt) {
        return !((p.x < top_left.x) || (p.x > bot_rgt.x) || (p.y < top_left.y) || (p.y > bot_rgt.y));
    }

    public static boolean insideRegion(Vector p, int left, int top, int right, int bottom) {
        return !((p.x < left) || (p.x > right) || (p.y < top) || (p.y > bottom));
    }

/**
 * @return true if the target position is in the field of view of the entity
 *         positioned at posFirst facing in facingFirst
 */
    public static boolean isSecondInFOVOfFirst(Vector posFirst, Vector facingFirst, Vector posSecond, double fov) {
        Vector toTarget = normalize(sub(posSecond, posFirst));
        return facingFirst.dot(toTarget) >= Math.cos(fov / 2.0);
    }
}