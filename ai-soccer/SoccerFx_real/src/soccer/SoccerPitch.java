
package soccer;

import soccer.TeamStates.PrepareForKickOff;
import common.D2.Vector;
import common.Game.Region;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Arrays;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import render.FieldRender;
import render.ParamsRender;

/**
 *  A SoccerPitch is the main game object. It owns instances of
 *  two soccer teams, two goals, the playing area, the ball
 *  etc. This is the root class for all the game updates and renders etc
 */
public class SoccerPitch {

    public static int tick;
    public static final int NUN_REGIONS_HORIZONTAL = 6;
    public static final int NUM_REGIONS_VERTICAL = 3;
    private SoccerBall ball;
    private SoccerTeam redTeam;
    private SoccerTeam blueTeam;
    private Goal leftGoal;
    private Goal rightGoal;
    //the playing field is broken up into regions that the team can make use of to implement strategies.
    private List<Region> regionList;
    //true if a goal keeper has possession
    private boolean goalKeeperHasBall;
    //true if the game is in play. Set to false whenever the players are getting ready for kickoff
    private boolean gameOn = true;
       
    public SoccerPitch() {
        /* !!! в конструкторе соблюдать порядок инициализации объектов(ворота, мяч, команды) */
        tick = 0;             
        createRegions();
             
        leftGoal = new Goal(Goal.GoalSide.LEFT);
        rightGoal = new Goal(Goal.GoalSide.RIGHT);  
                     
        ball = new SoccerBall(Wall.createWalls(leftGoal, rightGoal)); 
        
        redTeam = new SoccerTeam(leftGoal, rightGoal, this, SoccerTeam.red);        
        blueTeam = new SoccerTeam(rightGoal, leftGoal, this, SoccerTeam.blue);                
        redTeam.setOpponent(blueTeam);
        blueTeam.setOpponent(redTeam);            
    }

    /**
     * this instantiates the regions the players utilize to  position themselves.
     */
    private void createRegions() {
        regionList = Arrays.asList((Region[])Array.newInstance(Region.class, NUN_REGIONS_HORIZONTAL * NUM_REGIONS_VERTICAL)); 
        double width = Field.LENGTH_FIELD / NUN_REGIONS_HORIZONTAL;
        double height = Field.WIDTH_FIELD / NUM_REGIONS_VERTICAL;                    
        int idx = regionList.size() - 1;
        for (int col = 0; col < NUN_REGIONS_HORIZONTAL; ++col) {
            for (int row = 0; row < NUM_REGIONS_VERTICAL; ++row) {
                regionList.set(idx, new Region(col * width, row * height, (col + 1) * width, (row + 1) * height, idx));
                --idx;
            }
        }
    }
    
    /**
     *  this demo works on a fixed frame rate (60 by default) so we don't need
     *  to pass a time_elapsed as a parameter to the game entities
     */
    public void update() {
        ball.update();
        redTeam.update();
        blueTeam.update();

        //if a goal has been detected reset the pitch ready for kickoff
        if (rightGoal.scored(ball) || leftGoal.scored(ball)) {
            gameOn = false;                                                    
            ball.placeAtPosition(new Vector(Field.LENGTH_FIELD_CENTER, Field.WIDTH_FIELD_CENTER));
            redTeam.getStateMachine().changeState(PrepareForKickOff.getInstance());
            blueTeam.getStateMachine().changeState(PrepareForKickOff.getInstance());
        }
        ++tick;
    }
    
    public boolean render(GraphicsContext gc) {
        gc.setLineWidth(1.0);
        
        //draw the grass     
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0, FieldRender.LENGTH_FIELD, FieldRender.WIDTH_FIELD);
           
        //render regions
        if (ParamsRender.RENDER_REGIONS) {
            for (Region region : regionList) {
                region.render(true, gc);
            }
        }
        
        //render the goals
        gc.setStroke(Color.RED);
        gc.strokeRect(
            0, 
            (FieldRender.WIDTH_FIELD - FieldRender.WIDTH_GATE) / 2, 
            FieldRender.HEIGHT_GATE, 
            FieldRender.WIDTH_GATE);
        gc.setStroke(Color.BLUE);
        gc.strokeRect(
            FieldRender.LENGTH_FIELD - FieldRender.HEIGHT_GATE,
            (FieldRender.WIDTH_FIELD - FieldRender.WIDTH_GATE) / 2,
            FieldRender.HEIGHT_GATE,
            FieldRender.WIDTH_GATE);
        
        //render the pitch markings
        gc.setStroke(Color.WHITE);    
        gc.strokeOval(FieldRender.LENGTH_FIELD_CENTER - FieldRender.RAD_CENTER, FieldRender.WIDTH_FIELD_CENTER - FieldRender.RAD_CENTER, FieldRender.RAD_CENTER * 2, FieldRender.RAD_CENTER * 2);
        gc.strokeLine(FieldRender.LENGTH_FIELD_CENTER, 0, FieldRender.LENGTH_FIELD_CENTER, FieldRender.WIDTH_FIELD);        
        gc.setFill(Color.WHITE);
        gc.fillOval(FieldRender.LENGTH_FIELD_CENTER - 2.0,  FieldRender.WIDTH_FIELD_CENTER - 2.0, 4.0, 4.0);        

        //the ball
        ball.render(gc);

        //Render the teams
        redTeam.render(gc);
        blueTeam.render(gc);

        //render the walls 
        for (Wall wall : ball.getWallList()) {    
            wall.render(gc);
        }
        
        //show the score
        gc.setStroke(Color.RED);
        gc.strokeText("Red: " + Integer.toString(rightGoal.getNumGoalsScored()), FieldRender.LENGTH_FIELD_CENTER - 50, FieldRender.WIDTH_FIELD - 5);
        gc.setStroke(Color.BLUE);
        gc.strokeText("Blue: " + Integer.toString(leftGoal.getNumGoalsScored()), FieldRender.LENGTH_FIELD_CENTER + 10, FieldRender.WIDTH_FIELD - 5);
        
        return true;
    }
      
    /**
     *********************** Getters & Setters *************************************.
     */
    
    public Region getRegionFromIndex(int idx) {
        return regionList.get(idx);
    }

    public boolean getGoalKeeperHasBall() {
        return goalKeeperHasBall;
    }

    public void setGoalKeeperHasBall(boolean b) {
        goalKeeperHasBall = b;
    }

    public SoccerBall getBall() {
        return ball;
    }

    public boolean isGameOn() {
        return gameOn;
    }

    public void gameOn() {
        gameOn = true;
    }

    public void gameOff() {
        gameOn = false;
    }

    public SoccerTeam getRedTeam() {
        return redTeam;
    }

    public SoccerTeam getBlueTeam() {
        return blueTeam;
    }
       
}
