
package common.D2;

import common.misc.Cgdi;

public class InvertedBox {

    private Vector topLeft;
    private Vector bottomRight;
    private Vector center;

    public InvertedBox(Vector tl, Vector br) {
        topLeft = tl;
        bottomRight = br;
        center = Vector.add(tl, br).div(2.0);
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

    public void render() {
        Render(false);
    }

    public void Render(boolean RenderCenter) {
        Cgdi.gdi.Line((int) left(), (int) top(), (int) right(), (int) top());
        Cgdi.gdi.Line((int) left(), (int) bottom(), (int) right(), (int) bottom());
        Cgdi.gdi.Line((int) left(), (int) top(), (int) left(), (int) bottom());
        Cgdi.gdi.Line((int) right(), (int) top(), (int) right(), (int) bottom());
        if (RenderCenter) {
            Cgdi.gdi.Circle(center, 5);
        }
    }
}