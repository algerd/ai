
package render.renderjs;

import common.D2.Vector;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import soccer.SoccerBall;

public class BallRenderJS implements Renderable {
    
    private GraphicsContext gc;
    private List<Vector> positionList = new ArrayList<>();
    private Vector position;
    double radius;
    
    public BallRenderJS(GraphicsContext gc, SoccerBall ball) {
        this.gc = gc;
        positionList = ball.getPositionList();
        radius = ball.getBoundingRadius() * ParamsRenderJS.SCALE_ANIMATION;
        position = positionList.get(0).muln(ParamsRenderJS.SCALE_ANIMATION);
    }
    
    @Override
    public void render(int tact) {   
        Vector pos = positionList.get(tact);
        if (pos != null) {
            position = pos.muln(ParamsRenderJS.SCALE_ANIMATION);
        }
        gc.save();          
        gc.setFill(Color.BLACK);
        gc.fillOval(position.x - radius, position.y - radius, radius * 2, radius * 2);               
        gc.restore(); 
    }
}
