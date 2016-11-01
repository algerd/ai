
package render.renderfx;

import generator.Field;
import generator.Match;
import generator.Player;
import generator.Team;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import static render.renderfx.ParamsRenderFX.*;
import utils.Transformation;
import utils.Vector;
import utils.Vector3D;

public class RenderFX {
    private Match match;
    private GraphicsContext gc;
    
    private final double WIDTH_FIELD = Field.WIDTH_FIELD * SCALE_ANIMATION;          // ширина поля
    private final double LENGTH_FIELD = Field.LENGTH_FIELD * SCALE_ANIMATION;        // длина поля 
    private final double WIDTH_FIELD_CENTER = WIDTH_FIELD / 2;
    private final double LENGTH_FIELD_CENTER = LENGTH_FIELD / 2;
    private final double LENGTH_OUT = Field.LENGTH_OUT * SCALE_ANIMATION;            // внешняя кромка поля по длине
    private final double WIDTH_LEFT_OUT = Field.WIDTH_LEFT_OUT * SCALE_ANIMATION;    // внешняя кромка поля по ширине (левая)
    private final double WIDTH_RIGHT_OUT = Field.WIDTH_RIGHT_OUT * SCALE_ANIMATION;  // внешняя кромка поля по ширине (правая)                       
    private final double WIDTH_GATE = Field.WIDTH_GATE * SCALE_ANIMATION;            // ширина ворот
    private final double HEIGHT_GATE = Field.HEIGHT_GATE * SCALE_ANIMATION;          // высота ворот
    private final double RAD_CENTER = Field.RAD_CENTER * SCALE_ANIMATION;            // радиус центрального круга
    
    private final double RADIUS_HEAD = 6;
    private final List<Vector> PLAYER_VERTEXES = new ArrayList<>(Arrays.asList(
        new Vector(-0.45, 1.15).mul(SCALE_ANIMATION),
        new Vector(0.45, 1.45).mul(SCALE_ANIMATION),
        new Vector(0.45, -1.45).mul(SCALE_ANIMATION),
        new Vector(-0.45, -1.15).mul(SCALE_ANIMATION)));
    
    public RenderFX(Canvas canvas, Match match) { 
        canvas.setWidth(LENGTH_FIELD);
        canvas.setHeight(WIDTH_FIELD);
        this.match = match;
        gc = canvas.getGraphicsContext2D();
    }

    /**
     * This method is called every tact of animation timer.
     */
    public void update() {
        render();
        match.update();
    }
    
    public void render() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        renderField();
        renderBall();
        renderTeam(match.getHomeTeam());
        renderTeam(match.getGuestTeam());
        
    }
    
     private void renderField() {
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
    
    private void renderBall() {       
        Vector3D posBallRender = match.getBall().getPos().muln(SCALE_ANIMATION);
        double radius = RADIUS_BALL_MIN + (RADIUS_BALL_MAX - RADIUS_BALL_MIN) * posBallRender.z / (HEIGHT_BALL_MAX * SCALE_ANIMATION);                           
        gc.setFill(COLOR_BALL);
        gc.fillOval(posBallRender.x - radius, posBallRender.y - radius, radius * 2, radius * 2);      
    }
     
    private void renderTeam(Team team) {
        for (Player player : team.getPlayerList()) {
            renderPlayer(player, team.getColor());
        } 
    }
    
    private void renderPlayer(Player player, Color color) {  
        
        Vector posRender = player.getPosition().muln(SCALE_ANIMATION);
        List<Vector> transVertexList = Transformation.worldTransform(PLAYER_VERTEXES, posRender, player.getDirection());               
        int length = transVertexList.size();      
        double[] xPoints = new double[length];
        double[] yPoints = new double[length]; 
        for (int i = 0 ; i < length; i++) {
            xPoints[i] = transVertexList.get(i).x;
            yPoints[i] = transVertexList.get(i).y;
        }
        
        gc.save();
        gc.setLineWidth(1.0);          
        gc.setStroke(color);        //set team color           
        gc.strokePolygon(xPoints, yPoints, length);  //render the player's body 
        gc.setFill(Color.BROWN);    //set head color
        gc.fillOval(posRender.x - RADIUS_HEAD, posRender.y - RADIUS_HEAD, RADIUS_HEAD * 2, RADIUS_HEAD * 2);
        
        //render the state
        if (RENDER_STATE) {
            gc.setStroke(Color.rgb(0, 170, 0, 0.5));
            gc.strokeText(player.getStateMachine().getNameOfCurrentState(), posRender.x, posRender.y - 17);           
        }
        //show IDs
        if (RENDER_ID) {
            gc.setStroke(Color.rgb(0, 170, 0));
            gc.strokeText(Integer.toString(player.getId()), posRender.x - 11, posRender.y - 17);
        }
        /*
        if (RENDER_TARGETS) {
            gc.setFill(Color.RED);
            radius = 3;
            Vector target = player.getSteering().getTarget().muln(SCALE_ANIMATION);
            gc.fillOval(target.x - radius, target.y - radius, radius * 2, radius * 2);
            gc.setStroke(Color.GREY);
            gc.strokeText(Integer.toString(player.getId()), target.x, target.y);
        } 
        */        
        gc.restore();
    }
                 
}
