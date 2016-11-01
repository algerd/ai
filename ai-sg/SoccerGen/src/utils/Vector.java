
package utils;

public class Vector {

    public double x;
    public double y;

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
    public double dist(Vector v) {
        double ySeparation = v.y - y;
        double xSeparation = v.x - x;
        return Math.sqrt(ySeparation * ySeparation + xSeparation * xSeparation);
    }

    /** 
     * squared version of dist. calculates the euclidean dist squared between two vectors 
     */
    public double distSq(Vector v) {
        double ySeparation = v.y - y;
        double xSeparation = v.x - x;
        return ySeparation * ySeparation + xSeparation * xSeparation;
    }
    
    public Vector set(Vector v) {
        this.x = v.x;
        this.y = v.y;
        return this;
    }
    
    public Vector set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }
    
    public Vector get() {
        return new Vector(this.x, this.y);
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
    public double dot(Vector v) {
        return x * v.x + y * v.y;
    }

    /**
    /* returns positive if v2 is clockwise of this vector,
    /* negative if anticlockwise (assuming the Y axis is pointing down,
    /* X axis to right like a Window app)
     */
    public int sign(Vector v) {
        return (y * v.x > x * v.y) ? -1 : 1;
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
    
    @Override
    public String toString() {
        return "x = " + Math.round(this.x*10000)/10000.0 + " y = " + Math.round(this.y*10000)/10000.0;
    }

}
