
package common.D2;

import common.misc.Cgdi;

public class InvertedAABBox2D {

    private Vector2D topLeft;
    private Vector2D bottomRight;
    private Vector2D center;

    public InvertedAABBox2D(Vector2D tl, Vector2D br) {
        topLeft = tl;
        bottomRight = br;
        center = Vector2D.add(tl, br).div(2.0);
    }

    //returns true if the bbox described by other intersects with this one
    public boolean isOverlappedWith(InvertedAABBox2D other) {
        return !(
            (other.top() > this.bottom())
            || (other.bottom() < this.top())
            || (other.left() > this.right())
            || (other.right() < this.left())
        );
    }

    public Vector2D topLeft() {
        return topLeft;
    }

    public Vector2D bottomRight() {
        return bottomRight;
    }

    public double top() {
        return topLeft.y;
    }

    public double left() {
        return topLeft.x;
    }

    public double bottom() {
        return bottomRight.y;
    }

    public double right() {
        return bottomRight.x;
    }

    public Vector2D center() {
        return center;
    }

    public void render(Cgdi cgdi) {       
        cgdi.drawLine(left(), top(), right(), top());
        cgdi.drawLine(left(), bottom(), right(), bottom());
        cgdi.drawLine(left(), top(), left(), bottom());
        cgdi.drawLine(right(), top(), right(), bottom());
    }
}
