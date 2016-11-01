
package common.D2;

public class InvertedBox {

    private Vector topLeft;
    private Vector bottomRight;
    private Vector center;

    public InvertedBox(Vector tl, Vector br) {
        topLeft = tl;
        bottomRight = br;
        center = tl.addn(br).div(2.0);
    }

    //returns true if the bbox described by other intersects with this one
    public boolean isOverlappedWith(InvertedBox other) {
        return !((other.top() > this.bottom())
                || (other.bottom() < this.top())
                || (other.left() > this.right())
                || (other.right() < this.left()));
    }

    public Vector topLeft() {
        return topLeft;
    }

    public Vector bottomRight() {
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

    public Vector center() {
        return center;
    }

}