
package render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import soccer.Field;

public class FieldRender implements Renderable {
    
    private GraphicsContext gc;
    
    public final static double WIDTH_FIELD = Field.WIDTH_FIELD * ParamsRender.SCALE_ANIMATION;          // ширина поля
    public final static double WIDTH_FIELD_CENTER = WIDTH_FIELD / 2;
    public final static double LENGTH_FIELD = Field.LENGTH_FIELD * ParamsRender.SCALE_ANIMATION;        // длина поля
    public final static double LENGTH_FIELD_CENTER = LENGTH_FIELD / 2;
    public final static double LENGTH_OUT = Field.LENGTH_OUT * ParamsRender.SCALE_ANIMATION;            // внешняя кромка поля по длине
    public final static double WIDTH_LEFT_OUT = Field.WIDTH_LEFT_OUT * ParamsRender.SCALE_ANIMATION;    // внешняя кромка поля по ширине (левая)
    public final static double WIDTH_RIGHT_OUT = Field.WIDTH_RIGHT_OUT * ParamsRender.SCALE_ANIMATION;  // внешняя кромка поля по ширине (правая)                       
    public final static double WIDTH_GATE = Field.WIDTH_GATE * ParamsRender.SCALE_ANIMATION;            // ширина ворот
    public final static double HEIGHT_GATE = Field.HEIGHT_GATE * ParamsRender.SCALE_ANIMATION;          // высота ворот
    public final static double RAD_CENTER = Field.RAD_CENTER * ParamsRender.SCALE_ANIMATION;            // радиус центрального круга
       
    public FieldRender(GraphicsContext gc) {
        this.gc = gc;
    }
 
    
    @Override
    public void render(int tact) {
        gc.save();      
        gc.setLineWidth(1.0);
        
        //draw the grass     
        gc.setFill(Color.GREEN);
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
