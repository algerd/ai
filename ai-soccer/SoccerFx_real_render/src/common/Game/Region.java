
package common.Game;

import common.D2.Vector;

/**
 * Defines a rectangular region. A region has an identifying number, and four corners.
 */  
public class Region {
    
    public enum RegionType {
        HALFSIZE, NORMAL
    };   
    
    public static final RegionType halfsize = RegionType.HALFSIZE;
    public static final RegionType normal = RegionType.NORMAL;   
    protected double top;
    protected double left;
    protected double right;
    protected double bottom;
    protected double width;
    protected double height;
    protected Vector center;
    protected int id;

    public Region() {
        this(0, 0, 0, 0, -1);
    }

    public Region(double left, double top, double right, double bottom) {
        this(left, top, right, bottom, -1);
    }

    public Region(double left, double top, double right, double bottom, int id) {
        this.top = top;
        this.right = right;
        this.left = left;
        this.bottom = bottom;
        this.id = id;
        //calculate center of region
        this.center = new Vector((left + right) * 0.5, (top + bottom) * 0.5);
        this.width = Math.abs(right - left);
        this.height = Math.abs(bottom - top);
    }

    /**
     * returns true if the given position lays inside the region. The
     * region modifier can be used to contract the region bounderies
     */
    public boolean inside(Vector pos) {
        return inside(pos, RegionType.NORMAL);
    }

    public boolean inside(Vector pos, RegionType r) {
        if (r == RegionType.NORMAL) {
            return ((pos.x > left) && (pos.x < right) && (pos.y > top) && (pos.y < bottom));
        } 
        else {
            final double marginX = width * 0.25;
            final double marginY = height * 0.25;
            return ((pos.x > (left + marginX)) && (pos.x < (right - marginX))
                    && (pos.y > (top + marginY)) && (pos.y < (bottom - marginY)));
        }
    }

    /** 
     * return a vector representing a random location within the region.
     */
    public Vector getRandomPosition() {
        return new Vector(RandomUtil.randInRange(left, right), RandomUtil.randInRange(top, bottom));
    }

    public double top() {
        return top;
    }

    public double bottom() {
        return bottom;
    }

    public double left() {
        return left;
    }

    public double right() {
        return right;
    }

    public double width() {
        return Math.abs(right - left);
    }

    public double height() {
        return Math.abs(top - bottom);
    }

    public double length() {
        return Math.max(width(), height());
    }

    public double breadth() {
        return Math.min(width(), height());
    }

    public Vector center() {
        return new Vector(center);
    }

    public int getId() {
        return id;
    }
}
