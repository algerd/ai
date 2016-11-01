
package render;

import common.D2.Vector;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import soccer.ParamLoader;

public class Field implements Renderable {
    
    private GraphicsContext gc;
    private double widthCanvas; 
    private double heightCanvas;
    private double top;
    private double left;
    private double right;
    private double bottom;
    private Vector center;
    
    public Field(GraphicsContext gc) {
        this.gc = gc;
        widthCanvas = gc.getCanvas().getWidth();
        heightCanvas = gc.getCanvas().getHeight();
        top = 20;
        left = 20;
        right = widthCanvas - 20;
        bottom = heightCanvas - 20;
        center = new Vector((left + right) / 2, (top + bottom) / 2);
    }
 
    
    @Override
    public void render(int tact) {
        gc.save();      
        gc.setLineWidth(1.0);
        
        //draw the grass     
        gc.setFill(Color.GREEN);
        gc.fillRect(0, 0, widthCanvas, heightCanvas);
                  
        //render the goals
        gc.setStroke(Color.RED);
        gc.strokeRect(
            left, 
            (heightCanvas - ParamLoader.getInstance().GoalWidth) / 2, 
            40, 
            ParamLoader.getInstance().GoalWidth);
        gc.setStroke(Color.BLUE);
        gc.strokeRect(
            right - 40,
            (heightCanvas - ParamLoader.getInstance().GoalWidth) / 2,
            40,
            ParamLoader.getInstance().GoalWidth);
       
        //render the pitch markings
        double radius = Math.abs(right - left) * 0.125;
        gc.setStroke(Color.WHITE);    
        gc.strokeOval(center.x - radius, center.y - radius, radius * 2, radius * 2);
        gc.strokeLine(center.x, top, center.x, bottom);        
        gc.setFill(Color.WHITE);
        gc.fillOval(center.x - 2, center.y - 2, 4, 4); 
       
        // render walls
        gc.strokeRect(left, top, right - left, bottom - top);
        gc.restore(); 
    }
    
}
