
package soccer;

import soccer.TeamStates.PrepareForKickOff;
import common.D2.Vector;
import common.Game.Region;
import common.D2.Wall;
import java.lang.reflect.Array;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
    //local copy of client window dimensions
    private int xClient, yClient;
    

    public SoccerPitch(int cx, int cy) {
        tick = 0;
        xClient = cx;
        yClient = cy;
        regionList = Arrays.asList((Region[]) Array.newInstance(Region.class, NUN_REGIONS_HORIZONTAL * NUM_REGIONS_VERTICAL));
        //define the playing area
        playingArea = new Region(20, 20, cx - 20, cy - 20);

        //create the regions  
        createRegions(getPlayingArea().width() / NUN_REGIONS_HORIZONTAL, getPlayingArea().height() / NUM_REGIONS_VERTICAL);

        //create the goals
        redGoal = new Goal(
                new Vector(playingArea.left(), (cy - ParamLoader.getInstance().GoalWidth) / 2),
                new Vector(playingArea.left(), cy - (cy - ParamLoader.getInstance().GoalWidth) / 2),
                new Vector(1, 0)
        );
        blueGoal = new Goal(
                new Vector(playingArea.right(), (cy - ParamLoader.getInstance().GoalWidth) / 2),
                new Vector(playingArea.right(), cy - (cy - ParamLoader.getInstance().GoalWidth) / 2),
                new Vector(-1, 0)
        );
        //create the soccer ball
        ball = new SoccerBall(
                new Vector(xClient / 2.0, yClient / 2.0),
                ParamLoader.getInstance().BallSize,
                ParamLoader.getInstance().BallMass,
                wallList
        );
        //create the teams 
        redTeam = new SoccerTeam(redGoal, blueGoal, this, SoccerTeam.red);
        blueTeam = new SoccerTeam(blueGoal, redGoal, this, SoccerTeam.blue);

        //make sure each team knows who their opponents are
        redTeam.setOpponent(blueTeam);
        blueTeam.setOpponent(redTeam);

        //create the walls
        Vector topLeft = new Vector(playingArea.left(), playingArea.top());
        Vector topRight = new Vector(playingArea.right(), playingArea.top());
        Vector bottomRight = new Vector(playingArea.right(), playingArea.bottom());
        Vector bottomLeft = new Vector(playingArea.left(), playingArea.bottom());

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
        //index into the vector
        int idx = regionList.size() - 1;

        for (int col = 0; col < NUN_REGIONS_HORIZONTAL; ++col) {
            for (int row = 0; row < NUM_REGIONS_VERTICAL; ++row) {
                regionList.set(
                    idx, 
                    new Region(
                        getPlayingArea().left() + col * width,
                        getPlayingArea().top() + row * height,
                        getPlayingArea().left() + (col + 1) * width,
                        getPlayingArea().top() + (row + 1) * height,
                        idx
                    )
                );
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
            //reset the ball                                                      
            ball.placeAtPosition(new Vector(xClient / 2.0, yClient / 2.0));
            //get the teams ready for kickoff
            redTeam.getStateMachine().changeState(PrepareForKickOff.getInstance());
            blueTeam.getStateMachine().changeState(PrepareForKickOff.getInstance());
        }
        ++tick;
    }
    
    public boolean render(GraphicsContext gc) {
        gc.setLineWidth(1.0);
        
        //draw the grass     
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0, xClient, yClient);
           
        //render regions
        if (ParamLoader.getInstance().bRegions) {
            for (Region region : regionList) {
                region.render(true, gc);
            }
        }
        
        //render the goals
        gc.setStroke(Color.RED);
        gc.strokeRect(
            playingArea.left(), 
            (yClient - ParamLoader.getInstance().GoalWidth) / 2, 
            40, 
            ParamLoader.getInstance().GoalWidth);
        gc.setStroke(Color.BLUE);
        gc.strokeRect(
            playingArea.right() - 40,
            (yClient - ParamLoader.getInstance().GoalWidth) / 2,
            40,
            ParamLoader.getInstance().GoalWidth);
        
        //render the pitch markings
        Vector center = playingArea.center();
        double radius = playingArea.width() * 0.125;
        gc.setStroke(Color.WHITE);    
        gc.strokeOval(center.x - radius, center.y - radius, radius * 2, radius * 2);
        gc.strokeLine(center.x, playingArea.top(), playingArea.center().x, playingArea.bottom());        
        gc.setFill(Color.WHITE);
        gc.fillOval(center.x - radius, center.y - radius, 4.0, 4.0);        

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
        gc.strokeText("Red: " + Integer.toString(blueGoal.getNumGoalsScored()), xClient / 2 - 50, yClient - 5);
        gc.setStroke(Color.BLUE);
        gc.strokeText("Blue: " + Integer.toString(redGoal.getNumGoalsScored()), xClient / 2 + 10, yClient - 5);
        
        return true;
    }
      
    /**
     *********************** Getters & Setters *************************************.
     */
    
    public Region getRegionFromIndex(int idx) {
        return regionList.get(idx);
    }

    public int getXClient() {
        return xClient;
    }

    public int getYClient() {
        return yClient;
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
