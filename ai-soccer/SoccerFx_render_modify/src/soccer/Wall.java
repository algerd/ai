
package soccer;

import common.D2.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import render.ParamsRender;

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
        Vector vArender = vA.muln(ParamsRender.SCALE_ANIMATION);
        Vector vBrender = vB.muln(ParamsRender.SCALE_ANIMATION);
        Vector vNrender = vN.muln(ParamsRender.SCALE_ANIMATION);
        
        gc.save();
        gc.setStroke(Color.WHITE);
        gc.strokeLine(vArender.x, vArender.y, vBrender.x, vBrender.y);
        
        //render the normals if rqd
        if (RenderNormals) {
            int MidX = (int) ((vArender.x + vBrender.x) / 2);
            int MidY = (int) ((vArender.y + vBrender.y) / 2);
            gc.strokeLine(MidX, MidY, MidX + (vNrender.x * 5), MidY + (vNrender.y * 5));
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
