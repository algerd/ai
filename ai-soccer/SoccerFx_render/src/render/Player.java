
package render;

import common.D2.Transformation;
import common.D2.Vector;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import soccer.PlayerBase;

public class Player implements Renderable {
    
    private GraphicsContext gc;
    private List<Vector> positionList = new ArrayList<>();
    private List<Vector> headingList = new ArrayList<>();  
    private Color color;
    private Vector position;
    private Vector heading;
    private List<Vector> varVertexList = new ArrayList<>(4);
    private final List<Vector> vertexList = new ArrayList<>(4);
    
    public Player(GraphicsContext gc, PlayerBase player, Color color) {
        positionList = player.getPositionList();
        headingList = player.getHeadingList();
        position = positionList.get(0);
        heading = headingList.get(0);
        this.gc = gc;
        this.color = color;
        vertexList.add(new Vector(-3, 8));
        vertexList.add(new Vector(3, 10));
        vertexList.add(new Vector(3, -10));
        vertexList.add(new Vector(-3, -8));
        varVertexList.addAll(vertexList);
    }
    
    @Override
    public void render(int tact) {
        Vector pos = positionList.get(tact);
        Vector head = headingList.get(tact);
        if (pos != null) {
            position = pos;
        }
        if (head != null) {           
            heading = head;
        }      
               
        gc.save();
        gc.setLineWidth(1.0);
        gc.setStroke(color);
        
        if (pos != null || head != null) {  
            varVertexList = Transformation.worldTransform(vertexList, position, heading);
        }
        int length = varVertexList.size();      
        double[] xPoints = new double[length];
        double[] yPoints = new double[length]; 
        for (int i = 0 ; i < length; i++) {
            xPoints[i] = varVertexList.get(i).x;
            yPoints[i] = varVertexList.get(i).y;
        }
        gc.strokePolygon(xPoints, yPoints, length);  
        
        //draw the head
        gc.setFill(color);
        double radius = 6;
        gc.fillOval(position.x - radius, position.y - radius, radius * 2, radius * 2);
        
        gc.restore();       
    }
    
}
