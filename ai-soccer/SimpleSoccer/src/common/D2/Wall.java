
package common.D2;

import common.misc.Cgdi;

public class Wall {

    protected Vector vA = new Vector();
    protected Vector vB = new Vector();
    protected Vector vN = new Vector();

    public Wall(Vector A, Vector B) {
        vA = A;
        vB = B;
        calculateNormal();
    }

    public Wall(Vector A, Vector B, Vector N) {
        vA = A;
        vB = B;
        vN = N;
    }
  
    protected void calculateNormal() {
        Vector temp = Vector.normalize(Vector.sub(vB, vA));
        vN.x = -temp.y;
        vN.y = temp.x;
    }

    public void render() {
        render(false);
    }

    public void render(boolean RenderNormals) {
        Cgdi.gdi.Line(vA, vB);
        //render the normals if rqd
        if (RenderNormals) {
            int MidX = (int) ((vA.x + vB.x) / 2);
            int MidY = (int) ((vA.y + vB.y) / 2);
            Cgdi.gdi.Line(MidX, MidY, (int) (MidX + (vN.x * 5)), (int) (MidY + (vN.y * 5)));
        }
    }

    public Vector from() {
        return vA;
    }

    public void setFrom(Vector v) {
        vA = v;
        calculateNormal();
    }

    public Vector to() {
        return vB;
    }

    public void setTo(Vector v) {
        vB = v;
        calculateNormal();
    }

    public Vector normal() {
        return vN;
    }

    public void setNormal(Vector n) {
        vN = n;
    }

    public Vector center() {
        return Vector.div(Vector.add(vA, vB), 2.0);
    }

}