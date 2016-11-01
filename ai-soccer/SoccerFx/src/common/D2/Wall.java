
package common.D2;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
        Vector temp = vB.subn(vA).normalize();
        vN.x = -temp.y;
        vN.y = temp.x;
    }

    public void render(GraphicsContext gc) {
        render(false, gc);
    }

    public void render(boolean RenderNormals, GraphicsContext gc) {
        gc.save();
        gc.setStroke(Color.WHITE);
        gc.strokeLine(vA.x, vA.y, vB.x, vB.y);
        
        //render the normals if rqd
        if (RenderNormals) {
            int MidX = (int) ((vA.x + vB.x) / 2);
            int MidY = (int) ((vA.y + vB.y) / 2);
            gc.strokeLine(MidX, MidY, MidX + (vN.x * 5), MidY + (vN.y * 5));
        }
        gc.restore();
    }

    public Vector from() {
        return vA;
    }

    public Vector to() {
        return vB;
    }

    public Vector normal() {
        return vN;
    }

    public Vector center() {
        return vA.addn(vB).div(2.0);
    }

}
