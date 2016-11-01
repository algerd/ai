
package utils;

public class Vector3D {
    public double x;
    public double y;
    public double z;
    
    public Vector3D() {
        x = 0.0;
        y = 0.0;
        z = 0.0;
    }

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(Vector3D v) {
        super();
        this.set(v);
    }
    
    public Vector3D set(Vector3D v) {
        x = v.x;
        y = v.y;
        z = v.z;
        return this;
    }
    
    public Vector3D set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
    
    public Vector3D get() {
        return new Vector3D(x, y, z);
    }
    
    public void zero() {
        x = 0.0;
        y = 0.0;
        z = 0.0;
    }
    
    public boolean isZero() {
        return (x * x + y * y + z * z) < Double.MIN_VALUE;
    }
    
    public Vector getVectorXY() {
        return new Vector(x, y);
    }
    public Vector getVectorXZ() {
        return new Vector(x, z);
    }
    public Vector getVectorYZ() {
        return new Vector(y, z);
    }
    
    /**
     *  calculates length of vector.
     */
    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    public double lengthSq() {
        return x * x + y * y + z * z;
    }
    
    /**
     * calculates the distance between two vectors.
     */
    public double dist(Vector3D v) {
        return new Vector3D(v.x - x, v.y - y, v.z - z).length();
    }
    public double distSq(Vector3D v) {
        return new Vector3D(v.x - x, v.y - y, v.z - z).lengthSq();
    }
    
    /**
     * calculates the dot product.
     */
    public double dot(Vector3D v) {
        return x * v.x + y * v.y + z * v.z;
    }
    
    /**
     * ххх() - методы векторных вычислений, возвращающие этот!!! вектор.
     */   
    public Vector3D add(Vector3D v) {
        x += v.x;
        y += v.y;
        z += v.z;
        return this;
    }
    
    public Vector3D sub(Vector3D v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        return this;
    }  
    
    public Vector3D mul(double var) {
        x *= var;
        y *= var;
        z *= var;
        return this;
    }
    
    public Vector3D div(double var) {
        x /= var;
        y /= var;
        z /= var;
        return this;
    } 
    
    public Vector3D normalize() {
        double length = length();
        if (length > Double.MIN_NORMAL) {
            x /= length;
            y /= length;
            z /= length;
        }
        return this;
    }
    
    /**
     * хххn() - методы векторных вычислений, возвращающие новый!!! вектор.
     */
    public Vector3D addn(Vector3D v) {
        return new Vector3D(this).add(v);
    }
    
    public Vector3D subn(Vector3D v) {
        return new Vector3D(this).sub(v);
    }
    
    public Vector3D muln(double var) {
        return new Vector3D(this).mul(var);
    }
    
    public Vector3D divn(double var) {
        return new Vector3D(this).div(var);
    }
    
    public Vector3D normalizen() {
        return new Vector3D(this).normalize();
    }
    
    @Override
    public String toString() {
        return 
            "x = " + Math.round(this.x*10000)/10000.0 + 
            " y = " + Math.round(this.y*10000)/10000.0 +
            " z = " + Math.round(this.z*10000)/10000.0;
    }
    
    
}
