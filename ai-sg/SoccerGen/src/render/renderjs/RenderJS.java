
package render.renderjs;

import generator.Field;
import generator.Match;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import static render.renderjs.ParamsRenderJS.*;

public class RenderJS {
    private GraphicsContext gc;
    public int tact;
    
    private final static double WIDTH_FIELD = Field.WIDTH_FIELD * SCALE_ANIMATION;  // ширина поля
    private final static double LENGTH_FIELD = Field.LENGTH_FIELD * SCALE_ANIMATION;// длина поля   
    private final double WIDTH_FIELD_CENTER = WIDTH_FIELD / 2;
    private final double LENGTH_FIELD_CENTER = LENGTH_FIELD / 2;
    private final double LENGTH_OUT = Field.LENGTH_OUT * SCALE_ANIMATION;            // внешняя кромка поля по длине
    private final double WIDTH_LEFT_OUT = Field.WIDTH_LEFT_OUT * SCALE_ANIMATION;    // внешняя кромка поля по ширине (левая)
    private final double WIDTH_RIGHT_OUT = Field.WIDTH_RIGHT_OUT * SCALE_ANIMATION;  // внешняя кромка поля по ширине (правая)                       
    private final double WIDTH_GATE = Field.WIDTH_GATE * SCALE_ANIMATION;            // ширина ворот
    private final double HEIGHT_GATE = Field.HEIGHT_GATE * SCALE_ANIMATION;          // высота ворот
    private final double RAD_CENTER = Field.RAD_CENTER * SCALE_ANIMATION;            // радиус центрального круга

           
    public RenderJS(Canvas canvas, Match match) {       
        canvas.setWidth(LENGTH_FIELD);
        canvas.setHeight(WIDTH_FIELD);
        gc = canvas.getGraphicsContext2D();  
    }

    /**
     * This method is called every tact of animation timer.
     */
    public void update() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        renderField();
        
        ++tact;     
    }
    
    public void renderField() {
        gc.save();      
        gc.setLineWidth(1.0);
        
        //draw the grass     
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0, LENGTH_FIELD, WIDTH_FIELD);
                  
        //render the goals
        gc.setStroke(Color.RED);
        gc.strokeRect(
            0, 
            (WIDTH_FIELD - WIDTH_GATE) / 2, 
            HEIGHT_GATE, 
            WIDTH_GATE);
        gc.setStroke(Color.BLUE);
        gc.strokeRect(
            LENGTH_FIELD - HEIGHT_GATE,
            (WIDTH_FIELD - WIDTH_GATE) / 2,
            HEIGHT_GATE,
            WIDTH_GATE);
       
        //render the pitch markings
        gc.setStroke(Color.WHITE);    
        gc.strokeOval(LENGTH_FIELD_CENTER - RAD_CENTER, WIDTH_FIELD_CENTER - RAD_CENTER, RAD_CENTER * 2, RAD_CENTER * 2);
        gc.strokeLine(LENGTH_FIELD_CENTER, 0, LENGTH_FIELD_CENTER, WIDTH_FIELD);        
        gc.setFill(Color.WHITE);
        gc.fillOval(LENGTH_FIELD_CENTER - 2, WIDTH_FIELD_CENTER - 2, 4, 4);        
       
        // render walls
        gc.strokeRect(0, 0, LENGTH_FIELD, WIDTH_FIELD);
        gc.restore(); 
    }
      
}
