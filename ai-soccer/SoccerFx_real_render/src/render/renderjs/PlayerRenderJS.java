
package render.renderjs;

import common.D2.Transformation;
import common.D2.Vector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import soccer.PlayerBase;
import static render.renderjs.ParamsRenderJS.SCALE_ANIMATION;

public class PlayerRenderJS implements Renderable {
    
    private final List<Vector> PLAYER_VERTEXES = new ArrayList<>(Arrays.asList(
        new Vector(-0.45, 1.15).mul(SCALE_ANIMATION),
        new Vector(0.45, 1.45).mul(SCALE_ANIMATION),
        new Vector(0.45, -1.45).mul(SCALE_ANIMATION),
        new Vector(-0.45, -1.15).mul(SCALE_ANIMATION)));
    
    private GraphicsContext gc;
    private List<Vector> positionList = new ArrayList<>();
    private List<Vector> headingList = new ArrayList<>();  
    private Color color;
    private Vector position;
    private Vector heading;
    private List<Vector> vertexList = new ArrayList<>();
         
    public PlayerRenderJS(GraphicsContext gc, PlayerBase player, Color color) {
        positionList = player.getPositionList();
        headingList = player.getHeadingList();
        position = positionList.get(0).muln(SCALE_ANIMATION);
        heading = headingList.get(0);
        this.gc = gc;
        this.color = color;
        vertexList.addAll(PLAYER_VERTEXES);
    }
    
    @Override
    public void render(int tact) {
        Vector pos = positionList.get(tact);
        Vector head = headingList.get(tact);
        if (pos != null) {
            position = pos.muln(SCALE_ANIMATION);
        }
        if (head != null) {           
            heading = head;
        }      
               
        gc.save();
        gc.setLineWidth(1.0);
        gc.setStroke(color);
        
        if (pos != null || head != null) {  
            vertexList = Transformation.worldTransform(PLAYER_VERTEXES, position, heading);
        }
        int length = vertexList.size();      
        double[] xPoints = new double[length];
        double[] yPoints = new double[length]; 
        for (int i = 0 ; i < length; i++) {
            xPoints[i] = vertexList.get(i).x;
            yPoints[i] = vertexList.get(i).y;
        }
        gc.strokePolygon(xPoints, yPoints, length);  
        
        //draw the head
        gc.setFill(color);
        double radius = 6;
        gc.fillOval(position.x - radius, position.y - radius, radius * 2, radius * 2);
        
        gc.restore();       
    }
    
}
