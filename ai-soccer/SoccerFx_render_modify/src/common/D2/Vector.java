
package common.D2;

import common.Game.Functions;

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
   
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public double lengthSq() {
        return x * x + y * y;
    }
    
    /**
     * calculates the euclidean dist between two vectors
     * @return the dist between this vector and th one passed as a parameter
     */
    public double dist(Vector v2) {
        double ySeparation = v2.y - y;
        double xSeparation = v2.x - x;
        return Math.sqrt(ySeparation * ySeparation + xSeparation * xSeparation);
    }

    /** 
     * squared version of dist. calculates the euclidean dist squared between two vectors 
     */
    public double distSq(Vector v2) {
        double ySeparation = v2.y - y;
        double xSeparation = v2.x - x;
        return ySeparation * ySeparation + xSeparation * xSeparation;
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
        return (x * x + y * y) < Double.MIN_VALUE;
    }


    /**
     * calculates the dot product.
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
     */
    public void truncate(double max) {
        if (this.length() > max) {
            this.normalize();
            this.mul(max);
        }
    }
  
    /**
     *  given a normalized vector this method reflects the vector it
     *  is operating upon. (like the path of a ball bouncing off a wall)
     */
    public void reflect(Vector norm) {
        this.add(norm.reverse().mul(2.0 * dot(norm)));
    }

    public Vector reverse() {
        return new Vector(-this.x, -this.y);
    }

    public boolean isEqual(Vector rhs) {
        return (Functions.isEqual(x, rhs.x) && Functions.isEqual(y, rhs.y));
    }
    
    public boolean notEqual(Vector rhs) {
        return (x != rhs.x) || (y != rhs.y);
    }
    
     /**
     * ххх() - Объектные методы векторных вычислений, возвращающие этот!!! вектор.
     */
    public Vector normalize() {
        double length = this.length();
        if (length > Double.MIN_NORMAL) {
            this.x /= length;
            this.y /= length;
        }
        return this;
    }
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
    
    /**
     * хххn() - Объектные методы векторных вычислений, возвращающие новый!!! вектор.
     */
    public Vector addn(Vector rhs) {
        Vector result = new Vector(this);
        result.x += rhs.x;
        result.y += rhs.y;
        return result;
    }
    public Vector subn(Vector rhs) {
        Vector result = new Vector(this);
        result.x -= rhs.x;
        result.y -= rhs.y;
        return result;
    }
    public Vector muln(double rhs) {
        Vector result = new Vector(this);
        result.mul(rhs);
        return result;
    }
    public Vector divn(double val) {
        Vector result = new Vector(this);
        result.x /= val;
        result.y /= val;
        return result;
    }
    public Vector normalizen() {
        Vector result = new Vector(this);
        double length = result.length();
        if (length > Double.MIN_NORMAL) {
            result.x /= length;
            result.y /= length;
        }
        return result;
    }
    //////////////////////////////////////////////////////////////////////
    
    @Override
    public String toString() {
        return " " + Double.toString(Math.round(this.x*100)/100) + " " + Double.toString(Math.round(this.x*100)/100);
    }

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
     * return true if the target position is in the field of view of the entity
     * positioned at posFirst facing in facingFirst.
     */
    public static boolean isSecondInFOVOfFirst(Vector posFirst, Vector facingFirst, Vector posSecond, double fov) {
        Vector toTarget = posSecond.subn(posFirst).normalize();
        return facingFirst.dot(toTarget) >= Math.cos(fov / 2.0);
    }
}
