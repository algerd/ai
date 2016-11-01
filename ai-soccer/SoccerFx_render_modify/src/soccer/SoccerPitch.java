
package soccer;

import soccer.TeamStates.PrepareForKickOff;
import common.D2.Vector;
import common.Game.Region;
import java.lang.reflect.Array;
import java.util.List;
import java.util.ArrayList;
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
    private Goal redGoal;
    private Goal blueGoal;
    //container for the boundary walls
    private List<Wall> wallList = new ArrayList<>();
    //defines the dimensions of the playing area
    private Region playingArea;
    //the playing field is broken up into regions that the team can make use of to implement strategies.
    private List<Region> regionList;
    //true if a goal keeper has possession
    private boolean goalKeeperHasBall;
    //true if the game is in play. Set to false whenever the players are getting ready for kickoff
    private boolean gameOn = true;
    
    public SoccerPitch() {
        tick = 0;
        regionList = Arrays.asList((Region[])Array.newInstance(Region.class, NUN_REGIONS_HORIZONTAL * NUM_REGIONS_VERTICAL));
        
        //define the playing area
        playingArea = new Region(0, 0, Field.LENGTH_FIELD, Field.WIDTH_FIELD);
        createRegions(Field.LENGTH_FIELD / NUN_REGIONS_HORIZONTAL, Field.WIDTH_FIELD / NUM_REGIONS_VERTICAL);

        //create the goals
        redGoal = new Goal(
                new Vector(0, (Field.WIDTH_FIELD - Field.WIDTH_GATE) / 2),
                new Vector(0, Field.WIDTH_FIELD - (Field.WIDTH_FIELD - Field.WIDTH_GATE) / 2),
                new Vector(1, 0)
        );
        blueGoal = new Goal(
                new Vector(Field.LENGTH_FIELD, (Field.WIDTH_FIELD - Field.WIDTH_GATE) / 2),
                new Vector(Field.LENGTH_FIELD, Field.WIDTH_FIELD - (Field.WIDTH_FIELD - Field.WIDTH_GATE) / 2),
                new Vector(-1, 0)
        );
        //create the soccer ball
        ball = new SoccerBall(
                new Vector(Field.LENGTH_FIELD_CENTER, Field.WIDTH_FIELD_CENTER),
                Params.BALL_SIZE,
                Params.BALL_MASS,
                wallList
        );
        //create the teams 
        redTeam = new SoccerTeam(redGoal, blueGoal, this, SoccerTeam.red);
        blueTeam = new SoccerTeam(blueGoal, redGoal, this, SoccerTeam.blue);

        //make sure each team knows who their opponents are
        redTeam.setOpponent(blueTeam);
        blueTeam.setOpponent(redTeam);

        //create the walls
        Vector topLeft = new Vector(0, 0);
        Vector topRight = new Vector(Field.LENGTH_FIELD, 0);
        Vector bottomRight = new Vector(Field.LENGTH_FIELD, Field.WIDTH_FIELD);
        Vector bottomLeft = new Vector(0, Field.WIDTH_FIELD);

        wallList.add(new Wall(bottomLeft, redGoal.getRightPost()));
        wallList.add(new Wall(redGoal.getLeftPost(), topLeft));
        wallList.add(new Wall(topLeft, topRight));
        wallList.add(new Wall(topRight, blueGoal.getLeftPost()));
        wallList.add(new Wall(blueGoal.getRightPost(), bottomRight));
        wallList.add(new Wall(bottomRight, bottomLeft));
    }

    /**
     * this instantiates the regions the players utilize to  position themselves.
     */
    private void createRegions(double width, double height) {
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
        if (blueGoal.scored(ball) || redGoal.scored(ball)) {
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
        for (Wall wall : wallList) {    
            wall.render(gc);
        }
        
        //show the score
        gc.setStroke(Color.RED);
        gc.strokeText("Red: " + Integer.toString(blueGoal.getNumGoalsScored()), FieldRender.LENGTH_FIELD_CENTER - 50, FieldRender.WIDTH_FIELD - 5);
        gc.setStroke(Color.BLUE);
        gc.strokeText("Blue: " + Integer.toString(redGoal.getNumGoalsScored()), FieldRender.LENGTH_FIELD_CENTER + 10, FieldRender.WIDTH_FIELD - 5);
        
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

    public Region getPlayingArea() {
        return playingArea;
    }

    public List<Wall> getWallList() {
        return wallList;
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
