
package render.renderfx;

import common.D2.Transformation;
import common.D2.Vector;
import common.Game.Region;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import soccer.PlayerBase;
import soccer.SoccerPitch;
import soccer.SoccerTeam;
import soccer.SupportSpotCalculator;
import static render.renderfx.ParamsRenderFX.*;
import soccer.Field;

public class RenderFX {
    private SoccerPitch soccerPitch;
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
     
    private final List<Vector> PLAYER_VERTEXES = new ArrayList<>(Arrays.asList(
        new Vector(-0.45, 1.15).mul(SCALE_ANIMATION),
        new Vector(0.45, 1.45).mul(SCALE_ANIMATION),
        new Vector(0.45, -1.45).mul(SCALE_ANIMATION),
        new Vector(-0.45, -1.15).mul(SCALE_ANIMATION)));
    
    public RenderFX(Canvas canvas, SoccerPitch soccerPitch) { 
        canvas.setWidth(LENGTH_FIELD);
        canvas.setHeight(WIDTH_FIELD);
        this.soccerPitch = soccerPitch;
        gc = canvas.getGraphicsContext2D();
    }

    /**
     * This method is called every tact of animation timer.
     */
    public void update() {
        render();
        soccerPitch.update();
    }
    
    public void render() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        renderField();
        if (RENDER_REGIONS) {
            for (Region region : soccerPitch.getRegionList()) {
                renderRegion(region, true);
            }
        }
        renderBall();
        renderTeam(soccerPitch.getRedTeam());
        renderTeam(soccerPitch.getBlueTeam());
                  
        //show the score
        gc.setStroke(Color.RED);
        gc.strokeText("Red: " + Integer.toString(soccerPitch.getRightGoal().getNumGoalsScored()), LENGTH_FIELD_CENTER - 50, WIDTH_FIELD - 5);
        gc.setStroke(Color.BLUE);
        gc.strokeText("Blue: " + Integer.toString(soccerPitch.getLeftGoal().getNumGoalsScored()), LENGTH_FIELD_CENTER + 10, WIDTH_FIELD - 5);
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
        Vector posBallRender = soccerPitch.getBall().getPosition().muln(SCALE_ANIMATION);
        double radiusRender = 3;
        gc.setFill(Color.BLACK);
        gc.fillOval(posBallRender.x - radiusRender, posBallRender.y - radiusRender, radiusRender * 2, radiusRender * 2);      
    }
    
    private void renderTeam(SoccerTeam team) {
        for (PlayerBase player : team.getPlayerList()) {
            renderPlayer(player);
        }       
        //show the controlling team and player at the top of the display
        if (RENDER_SHOW_CONTROLLING_TEAM) {
            gc.setStroke(Color.WHITE);
            String str;
            if ((team.getColor() == SoccerTeam.blue) && team.isControllingPlayer()) {
                str = "Blue in Control";
                gc.strokeText("Blue in Control", 20, 11);
            } 
            else if ((team.getColor() == SoccerTeam.red) && team.isControllingPlayer()) {
                str = "Red in Control";
                gc.strokeText("Red in Control", 20, 11);
            }         
            if (team.getControllingPlayer() != null) {
                gc.strokeText("Controlling Player: " + Integer.toString(team.getControllingPlayer().getId()), LENGTH_FIELD - 150, 11);
            }
        }       
        //render the sweet spots
        if (RENDER_SUPPORT_SPOTS && team.isControllingPlayer()) {
            renderSpot(team);
        }            
    }
    
    private void renderPlayer(PlayerBase player) {
        
        Vector posRender = player.getPosition().muln(SCALE_ANIMATION);      
        gc.save();
        gc.setLineWidth(1.0);
        
        //set appropriate team color
        if (player.getTeam().getColor() == SoccerTeam.blue) {
            gc.setStroke(Color.BLUE);
        } else {
            gc.setStroke(Color.RED);
        }

        //render the player's body
        List<Vector> transVertexList = Transformation.worldTransform(PLAYER_VERTEXES, posRender, player.getHeading());               
        int length = transVertexList.size();      
        double[] xPoints = new double[length];
        double[] yPoints = new double[length]; 
        for (int i = 0 ; i < length; i++) {
            xPoints[i] = transVertexList.get(i).x;
            yPoints[i] = transVertexList.get(i).y;
        }
        gc.strokePolygon(xPoints, yPoints, length);
                
        // render Highlight If Threatened
        if (RENDER_HIGHLIGHT_IF_THREATENED && (player.getTeam().getControllingPlayer() == player) && player.isThreatened()) {          
            gc.setFill(Color.YELLOW);
        } else {
            gc.setFill(Color.BROWN); 
        }
       
        //draw the head
        double radius = 6;
        gc.fillOval(posRender.x - radius, posRender.y - radius, radius * 2, radius * 2);
        
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

        if (RENDER_TARGETS) {
            gc.setFill(Color.RED);
            radius = 3;
            Vector target = player.getSteering().getTarget().muln(SCALE_ANIMATION);
            gc.fillOval(target.x - radius, target.y - radius, radius * 2, radius * 2);
            gc.setStroke(Color.GREY);
            gc.strokeText(Integer.toString(player.getId()), target.x, target.y);
        }     
        gc.restore();
    }
    
    private void renderSpot(SoccerTeam team) {  
        gc.save();
        double radius;
        List<SupportSpotCalculator.SupportSpot> spotList = team.getSupportSpotCalc().getSpotList();
        for (int spt = 0; spt < spotList.size(); ++spt) {  
            Vector spotRender = spotList.get(spt).pos.muln(SCALE_ANIMATION);
            radius = spotList.get(spt).score;
            gc.setStroke(Color.GREY);
            gc.strokeOval(spotRender.x - radius, spotRender.y - radius, radius * 2, radius * 2);
        }
        SupportSpotCalculator.SupportSpot bestSupportingSpot = team.getSupportSpotCalc().getBestSupportingSpot();
        if (bestSupportingSpot != null) {
            radius = bestSupportingSpot.score;
            Vector bestSpotRender = bestSupportingSpot.pos.muln(SCALE_ANIMATION);
            gc.setStroke(Color.GREEN);
            gc.strokeOval(bestSpotRender.x - radius, bestSpotRender.y - radius, radius * 2, radius * 2);
        }
        gc.restore();
    }
    
    private void renderRegion(Region region, boolean ShowID) {
        gc.save();
        gc.setStroke(Color.GREEN);
        gc.strokeRect(
            region.left() * SCALE_ANIMATION, 
            region.top() * SCALE_ANIMATION, 
            (region.right() - region.left()) * SCALE_ANIMATION, 
            (region.bottom() - region.top()) * SCALE_ANIMATION);       
        if (ShowID) {
            gc.setStroke(Color.GREEN);
            gc.strokeText(
                Integer.toString(region.getId()), 
                region.center().x * SCALE_ANIMATION, 
                region.center().y * SCALE_ANIMATION);    
        }
        gc.restore();
    }
        
}
