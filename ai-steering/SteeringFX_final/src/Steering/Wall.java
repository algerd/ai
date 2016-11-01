
package Steering;

import common.D2.Vector2D;
import common.misc.Cgdi;

public class Wall {

    protected Vector2D vA = new Vector2D();
    protected Vector2D vB = new Vector2D();
    protected Vector2D vN = new Vector2D();
    
    public Wall(Vector2D A, Vector2D B) {
        vA = A;
        vB = B;
        calculateNormal();
    }

    public Wall(Vector2D A, Vector2D B, Vector2D N) {
        vA = A;
        vB = B;
        vN = N;
    }

    private void calculateNormal() {
        Vector2D temp = Vector2D.vec2DNormalize(Vector2D.sub(vB, vA));
        vN.x = -temp.y;
        vN.y = temp.x;
    }

    public void render(Cgdi cgdi) {
        cgdi.drawLine(vA, vB);

        //render the normals if rqd
        int MidX = (int) ((vA.x + vB.x) / 2);
        int MidY = (int) ((vA.y + vB.y) / 2);
        cgdi.drawLine(MidX, MidY, (int) (MidX + (vN.x * 5)), (int) (MidY + (vN.y * 5)));       
    }

    public Vector2D from() {
        return vA;
    }

    public void setFrom(Vector2D v) {
        vA = v;
        calculateNormal();
    }

    public Vector2D to() {
        return vB;
    }

    public void setTo(Vector2D v) {
        vB = v;
        calculateNormal();
    }

    public Vector2D normal() {
        return vN;
    }

    public void setNormal(Vector2D n) {
        vN = n;
    }

    public Vector2D center() {
        return Vector2D.div(Vector2D.add(vA, vB), 2.0);
    }


}
