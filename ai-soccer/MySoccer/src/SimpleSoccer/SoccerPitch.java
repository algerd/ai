
package SimpleSoccer;

import SimpleSoccer.TeamStates.PrepareForKickOff;
import common.D2.Vector;
import common.Game.Region;
import common.D2.Wall;
import java.lang.reflect.Array;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import common.misc.Cgdi;
import static common.misc.StreamUtilityFunction.ttos;

/**
 *  A SoccerPitch is the main game object. It owns instances of
 *  two soccer teams, two goals, the playing area, the ball
 *  etc. This is the root class for all the game updates and renders etc
 */
public class SoccerPitch {

    static int tick;
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
    //set true to pause the motion
    private boolean paused;
    //local copy of client window dimensions
    private int xClient, yClient;
    

    public SoccerPitch(int cx, int cy) {
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
        if (paused) {
            return;
        }
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
    }
    
    public boolean render() {
        //draw the grass
        Cgdi.gdi.DarkGreenPen();
        Cgdi.gdi.DarkGreenBrush();
        Cgdi.gdi.Rect(0, 0, xClient, yClient);

        //render regions
        if (ParamLoader.getInstance().bRegions) {
            for (int r = 0; r < regionList.size(); ++r) {
                regionList.get(r).render(true);
            }
        }

        //render the goals
        Cgdi.gdi.HollowBrush();
        Cgdi.gdi.RedPen();
        Cgdi.gdi.Rect(playingArea.left(), (yClient - ParamLoader.getInstance().GoalWidth) / 2, playingArea.left() + 40,
                yClient - (yClient - ParamLoader.getInstance().GoalWidth) / 2);

        Cgdi.gdi.BluePen();
        Cgdi.gdi.Rect(playingArea.right(), (yClient - ParamLoader.getInstance().GoalWidth) / 2, playingArea.right() - 40,
                yClient - (yClient - ParamLoader.getInstance().GoalWidth) / 2);

        //render the pitch markings
        Cgdi.gdi.WhitePen();
        Cgdi.gdi.Circle(playingArea.center(), playingArea.width() * 0.125);
        Cgdi.gdi.Line(playingArea.center().x, playingArea.top(), playingArea.center().x, playingArea.bottom());
        Cgdi.gdi.WhiteBrush();
        Cgdi.gdi.Circle(playingArea.center(), 2.0);

        //the ball
        Cgdi.gdi.WhitePen();
        Cgdi.gdi.WhiteBrush();
        ball.render();

        //Render the teams
        redTeam.render();
        blueTeam.render();

        //render the walls
        Cgdi.gdi.WhitePen();
        for (int w = 0; w < wallList.size(); ++w) {
            wallList.get(w).render();
        }
        //show the score
        Cgdi.gdi.TextColor(Cgdi.red);
        Cgdi.gdi.TextAtPos((xClient / 2) - 50, yClient - 18, "Red: " + ttos(blueGoal.getNumGoalsScored()));
        Cgdi.gdi.TextColor(Cgdi.blue);
        Cgdi.gdi.TextAtPos((xClient / 2) + 10, yClient - 18, "Blue: " + ttos(redGoal.getNumGoalsScored()));

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
}
