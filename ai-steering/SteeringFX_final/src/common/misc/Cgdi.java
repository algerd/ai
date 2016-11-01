
package common.misc;

import common.D2.Vector2D;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cgdi {
   
    private GraphicsContext gc;   
    
    public Cgdi(GraphicsContext gc) {
        this.gc = gc;      
    }
   
    public void clear() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }
    
    public void drawPolygon(List<Vector2D> points) {
        int length = points.size();
        if (length > 0) {
            double[] xPoints = new double[length];
            double[] yPoints = new double[length]; 
            for (int i = 0 ; i < length; i++) {
                xPoints[i] = points.get(i).x;
                yPoints[i] = points.get(i).y;
            }
            gc.setLineWidth(1.0);
            gc.setStroke(Color.BLACK);
            gc.strokePolygon(xPoints, yPoints, length);
        }    
    }
    
    public void drawCircle(double x, double y, double radius) {
        gc.setLineWidth(1.0);
        gc.setStroke(Color.RED);
        gc.strokeOval(x, y, radius * 2, radius * 2);
    }
    
    public void drawLine(Vector2D from, Vector2D to) {
        gc.setLineWidth(1.0);
        gc.setStroke(Color.BLACK);       
        gc.strokeLine(from.x, from.y, to.x, to.y);
    }
    
    public void drawLine(double fromX, double fromY, double toX, double toY) {
        gc.setLineWidth(1.0);
        gc.setStroke(Color.BLACK);       
        gc.strokeLine(fromX, fromY, toX, toY);
    }
    
}
