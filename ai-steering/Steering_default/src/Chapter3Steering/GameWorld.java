/**
 * 
 *  Desc:   All the environment data and methods for the Steering
 *          Behavior projects. This class is the root of the project's
 *          update and render calls (excluding main of course)
 */
package Chapter3Steering;

import common.D2.InvertedAABBox2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import common.D2.Vector2D;
import java.util.List;
import java.util.ListIterator;
import common.D2.Wall2D;
import common.misc.CellSpacePartition;
import common.misc.Utils;
import common.misc.Smoother;
import common.Windows;
import common.D2.Geometry;
import common.misc.Cgdi;
import common.misc.WindowUtils;
import common.misc.StreamUtilityFunction;

public class GameWorld {
    //a container of all the moving entities
    private List<Vehicle> vehicles = new ArrayList<Vehicle>(ParamLoader.prm.NumAgents);
    //any obstacles
    private List<BaseGameEntity> obstacles = new ArrayList<BaseGameEntity>(ParamLoader.prm.NumObstacles);
    //container containing any walls in the environment
    private List<Wall2D> walls = new ArrayList<Wall2D>();
    private CellSpacePartition<Vehicle> cellSpace;
    //any path we may create for the vehicles to follow
    private Path path;
    //set true to pause the motion
    private boolean paused;
    //local copy of client window dimensions
    private int xClient, yClient;
    //the position of the crosshair
    private Vector2D crosshair;
    //keeps track of the average FPS
    private double avFrameTime;
    //flags to turn aids and obstacles etc on/off
    private boolean showWalls;
    private boolean showObstacles;
    private boolean showPath;
    private boolean showDetectionBox;
    private boolean showWanderCircle;
    private boolean showFeelers;
    private boolean showSteeringForce;
    private boolean showFPS;
    private boolean renderNeighbors;
    private boolean viewKeys;
    private boolean showCellSpaceInfo;
    final static int SampleRate = 10;
    private static Smoother<Double> FrameRateSmoother = new Smoother<Double>(SampleRate, 0.0);
    
    public GameWorld(int cx, int cy) {
        xClient = cx;
        yClient = cy;
        crosshair = new Vector2D(getXClient() / 2.0, getXClient() / 2.0);
        showFPS = true;
        double border = 30;
        path = new Path(5, border, border, cx - border, cy - border, true);
        cellSpace = new CellSpacePartition<Vehicle>(
            (double) cx, (double) cy, ParamLoader.prm.NumCellsX, ParamLoader.prm.NumCellsY, ParamLoader.prm.NumAgents);
        
        BaseGameEntity.resetValidID();
        for (int a = 0; a < ParamLoader.prm.NumAgents; ++a) {
            //determine a random starting position
            Vector2D SpawnPos = new Vector2D(
                cx / 2.0 + Utils.RandomClamped() * cx / 2.0,
                cy / 2.0 + Utils.RandomClamped() * cy / 2.0);

            Vehicle vehicle = new Vehicle(
                this,
                SpawnPos, //initial position
                Utils.RandFloat() * Utils.TwoPi, //start rotation
                new Vector2D(0, 0), //velocity
                ParamLoader.prm.VehicleMass, //mass
                ParamLoader.prm.MaxSteeringForce, //max force
                ParamLoader.prm.MaxSpeed, //max velocity
                ParamLoader.prm.MaxTurnRatePerSecond, //max turn rate
                ParamLoader.prm.VehicleScale);        //scale

            vehicle.getSteering().FlockingOn();
            vehicles.add(vehicle);
            cellSpace.addEntity(vehicle);
        } 
        
        vehicles.get(ParamLoader.prm.NumAgents - 1).getSteering().FlockingOff();
        vehicles.get(ParamLoader.prm.NumAgents - 1).setScale(new Vector2D(10, 10));
        vehicles.get(ParamLoader.prm.NumAgents - 1).getSteering().WanderOn();
        vehicles.get(ParamLoader.prm.NumAgents - 1).setMaxSpeed(70);
        for (int i = 0; i < ParamLoader.prm.NumAgents - 1; ++i) {
            vehicles.get(i).getSteering().EvadeOn(vehicles.get(ParamLoader.prm.NumAgents - 1));
        }
    }
    
    public void nonPenetrationContraint(Vehicle v) {
        EntityFunctionTemplates.EnforceNonPenetrationConstraint(v, vehicles);
    }

    public void tagVehiclesWithinViewRange(BaseGameEntity pVehicle, double range) {
        EntityFunctionTemplates.TagNeighbors(pVehicle, vehicles, range);
    }

    public void tagObstaclesWithinViewRange(BaseGameEntity pVehicle, double range) {
        EntityFunctionTemplates.TagNeighbors(pVehicle, obstacles, range);
    }

    public List<Wall2D> getWalls() {
        return walls;
    }

    public CellSpacePartition<Vehicle> getCellSpace() {
        return cellSpace;
    }

    public List<BaseGameEntity> getObstacles() {
        return obstacles;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void togglePause() {
        paused = !paused;
    }

    public boolean isPaused() {
        return paused;
    }

    public Vector2D getCrosshair() {
        return crosshair;
    }

    public void setCrosshair(Vector2D v) {
        crosshair = v;
    }

    public int getXClient() {
        return xClient;
    }

    public int getYClient() {
        return yClient;
    }

    public boolean isShowWalls() {
        return showWalls;
    }

    public boolean isShowObstacles() {
        return showObstacles;
    }

    public boolean isShowPath() {
        return showPath;
    }

    public boolean isShowDetectionBox() {
        return showDetectionBox;
    }

    public boolean isShowWanderCircle() {
        return showWanderCircle;
    }

    public boolean isShowFeelers() {
        return showFeelers;
    }

    public boolean isShowSteeringForce() {
        return showSteeringForce;
    }

    public boolean isShowFPS() {
        return showFPS;
    }

    public void toggleShowFPS() {
        showFPS = !showFPS;
    }

    public void toggleRenderNeighbors() {
        renderNeighbors = !renderNeighbors;
    }

    public boolean isRenderNeighbors() {
        return renderNeighbors;
    }

    public void toggleViewKeys() {
        viewKeys = !viewKeys;
    }

    public boolean isViewKeys() {
        return viewKeys;
    }

    /**
     * create a smoother to smooth the framerate
     */
    synchronized public void update(double timeElapsed) {
        if (paused) {
            return;
        }
        avFrameTime = FrameRateSmoother.Update(timeElapsed);
        //update the vehicles
        for (int a = 0; a < vehicles.size(); ++a) {
            vehicles.get(a).update(timeElapsed);
        }
    }

    /**
     * creates some walls that form an enclosure for the steering agents.
     * used to demonstrate several of the steering behaviors
     */
    private void createWalls() {
        //create the walls  
        double bordersize = 20.0;
        double CornerSize = 0.2;
        double vDist = yClient - 2 * bordersize;
        double hDist = xClient - 2 * bordersize;

        Vector2D wall[] = {new Vector2D(hDist * CornerSize + bordersize, bordersize),
            new Vector2D(xClient - bordersize - hDist * CornerSize, bordersize),
            new Vector2D(xClient - bordersize, bordersize + vDist * CornerSize),
            new Vector2D(xClient - bordersize, yClient - bordersize - vDist * CornerSize),
            new Vector2D(xClient - bordersize - hDist * CornerSize, yClient - bordersize),
            new Vector2D(hDist * CornerSize + bordersize, yClient - bordersize),
            new Vector2D(bordersize, yClient - bordersize - vDist * CornerSize),
            new Vector2D(bordersize, bordersize + vDist * CornerSize)};

        int NumWallVerts = wall.length;
        for (int w = 0; w < NumWallVerts - 1; ++w) {
            walls.add(new Wall2D(wall[w], wall[w + 1]));
        }
        walls.add(new Wall2D(wall[NumWallVerts - 1], wall[0]));
    }

    /**
     * Sets up the vector of obstacles with random positions and sizes. Makes
     *  sure the obstacles do not overlap
     */
    private void createObstacles() {
        //create a number of randomly sized tiddlywinks
        for (int o = 0; o < ParamLoader.prm.NumObstacles; ++o) {
            boolean bOverlapped = true;

            //keep creating tiddlywinks until we find one that doesn't overlap
            //any others.Sometimes this can get into an endless loop because the
            //obstacle has nowhere to fit. We test for this case and exit accordingly
            int numTrys = 0;
            int numAllowableTrys = 2000;
            while (bOverlapped) {
                numTrys++;
                if (numTrys > numAllowableTrys) {
                    return;
                }
                int radius = Utils.RandInt((int) ParamLoader.prm.MinObstacleRadius, (int) ParamLoader.prm.MaxObstacleRadius);
                final int border = 10;
                final int MinGapBetweenObstacles = 20;

                Obstacle ob = new Obstacle(
                        Utils.RandInt(radius + border, xClient - radius - border),
                        Utils.RandInt(radius + border, yClient - radius - 30 - border),
                        radius);

                if (!EntityFunctionTemplates.Overlapped((BaseGameEntity) ob, obstacles, MinGapBetweenObstacles)) {
                    //its not overlapped so we can add it
                    obstacles.add(ob);
                    bOverlapped = false;
                } else {
                    ob = null;
                }
            }
        }
    }

    /**
     * The user can set the position of the crosshair by right clicking the
     *  mouse. This method makes sure the click is not inside any enabled
     *  Obstacles and sets the position appropriately
     */
    synchronized public void setCrosshair(Windows.POINTS p) {
        Vector2D ProposedPosition = new Vector2D((double) p.x, (double) p.y);
        //make sure it's not inside an obstacle
        ListIterator<BaseGameEntity> it = obstacles.listIterator();
        while (it.hasNext()) {
            BaseGameEntity curOb = it.next();
            if (Geometry.PointInCircle(curOb.getPos(), curOb.getBoundingRadius(), ProposedPosition)) {
                return;
            }
        }
        crosshair.x = (double) p.x;
        crosshair.y = (double) p.y;
    }

    synchronized public void handleKeyPresses(KeyEvent wParam) {
        switch (wParam.getKeyChar()) {
            case 'u':
            case 'U':
                path = null;
                double border = 60;
                path = new Path(Utils.RandInt(3, 7), border, border, getXClient() - border, getYClient() - border, true);
                showPath = true;
                for (int i = 0; i < vehicles.size(); ++i) {
                    vehicles.get(i).getSteering().SetPath(path.GetPath());
                }
                break;
            case 'p':
            case 'P':
                togglePause();
                break;
            case 'o':
            case 'O':
                toggleRenderNeighbors();
                break;
            case 'i':
            case 'I':
                for (int i = 0; i < vehicles.size(); ++i) {
                    vehicles.get(i).toggleSmoothing();
                }
                break;
            case 'y':
            case 'Y':
                showObstacles = !showObstacles;
                if (!showObstacles) {
                    obstacles.clear();
                    for (int i = 0; i < vehicles.size(); ++i) {
                        vehicles.get(i).getSteering().ObstacleAvoidanceOff();
                    }
                } else {
                    createObstacles();

                    for (int i = 0; i < vehicles.size(); ++i) {
                        vehicles.get(i).getSteering().ObstacleAvoidanceOn();
                    }
                }
                break;
        }
    }

    synchronized public void handleMenuItems(int wParam, Script1.MyMenuBar hwnd) {
        switch (wParam) {
            case Resource.ID_OB_OBSTACLES:
                showObstacles = !showObstacles;
                if (!showObstacles) {
                    obstacles.clear();
                    for (int i = 0; i < vehicles.size(); ++i) {
                        vehicles.get(i).getSteering().ObstacleAvoidanceOff();
                    }
                    //uncheck the menu
                    WindowUtils.ChangeMenuState(hwnd, Resource.ID_OB_OBSTACLES, Windows.MFS_UNCHECKED);
                } else {
                    createObstacles();
                    for (int i = 0; i < vehicles.size(); ++i) {
                        vehicles.get(i).getSteering().ObstacleAvoidanceOn();
                    }
                    //check the menu
                    WindowUtils.ChangeMenuState(hwnd, Resource.ID_OB_OBSTACLES, Windows.MFS_CHECKED);
                }
                break;
                
            case Resource.ID_OB_WALLS:
                showWalls = !showWalls;
                if (showWalls) {
                    createWalls();
                    for (int i = 0; i < vehicles.size(); ++i) {
                        vehicles.get(i).getSteering().WallAvoidanceOn();
                    }
                    //check the menu
                    WindowUtils.ChangeMenuState(hwnd, Resource.ID_OB_WALLS, Windows.MFS_CHECKED);
                } else {
                    walls.clear();
                    for (int i = 0; i < vehicles.size(); ++i) {
                        vehicles.get(i).getSteering().WallAvoidanceOff();
                    }
                    //uncheck the menu
                    WindowUtils.ChangeMenuState(hwnd, Resource.ID_OB_WALLS, Windows.MFS_UNCHECKED);
                }
                break;
                
            case Resource.IDR_PARTITIONING:
                for (int i = 0; i < vehicles.size(); ++i) {
                    vehicles.get(i).getSteering().ToggleSpacePartitioningOnOff();
                }
                //if toggled on, empty the cell space and then re-add all the vehicles
                if (vehicles.get(0).getSteering().isSpacePartitioningOn()) {
                    cellSpace.emptyCells();
                    for (int i = 0; i < vehicles.size(); ++i) {
                        cellSpace.addEntity(vehicles.get(i));
                    }
                    WindowUtils.ChangeMenuState(hwnd, Resource.IDR_PARTITIONING, Windows.MFS_CHECKED);
                } else {
                    WindowUtils.ChangeMenuState(hwnd, Resource.IDR_PARTITIONING, Windows.MFS_UNCHECKED);
                    WindowUtils.ChangeMenuState(hwnd, Resource.IDM_PARTITION_VIEW_NEIGHBORS, Windows.MFS_UNCHECKED);
                    showCellSpaceInfo = false;
                }       
                break;
                
            case Resource.IDM_PARTITION_VIEW_NEIGHBORS:
                showCellSpaceInfo = !showCellSpaceInfo;
                if (showCellSpaceInfo) {
                    WindowUtils.ChangeMenuState(hwnd, Resource.IDM_PARTITION_VIEW_NEIGHBORS, Windows.MFS_CHECKED);
                    if (!vehicles.get(0).getSteering().isSpacePartitioningOn()) {
                        WindowUtils.SendChangeMenuMessage(hwnd, Resource.IDR_PARTITIONING);
                    }
                } else {
                    WindowUtils.ChangeMenuState(hwnd, Resource.IDM_PARTITION_VIEW_NEIGHBORS, Windows.MFS_UNCHECKED);
                }
                break;
                
            case Resource.IDR_WEIGHTED_SUM:
                WindowUtils.ChangeMenuState(hwnd, Resource.IDR_WEIGHTED_SUM, Windows.MFS_CHECKED);
                WindowUtils.ChangeMenuState(hwnd, Resource.IDR_PRIORITIZED, Windows.MFS_UNCHECKED);
                WindowUtils.ChangeMenuState(hwnd, Resource.IDR_DITHERED, Windows.MFS_UNCHECKED);

                for (int i = 0; i < vehicles.size(); ++i) {
                    vehicles.get(i).getSteering().SetSummingMethod(SteeringBehavior.SummingMethod.WEIGHTED_AVERAGE);
                }
                break;
                
            case Resource.IDR_PRIORITIZED:
                WindowUtils.ChangeMenuState(hwnd, Resource.IDR_WEIGHTED_SUM, Windows.MFS_UNCHECKED);
                WindowUtils.ChangeMenuState(hwnd, Resource.IDR_PRIORITIZED, Windows.MFS_CHECKED);
                WindowUtils.ChangeMenuState(hwnd, Resource.IDR_DITHERED, Windows.MFS_UNCHECKED);

                for (int i = 0; i < vehicles.size(); ++i) {
                    vehicles.get(i).getSteering().SetSummingMethod(SteeringBehavior.SummingMethod.PRIORITIZED);
                }
                break;
                
            case Resource.IDR_DITHERED:
                WindowUtils.ChangeMenuState(hwnd, Resource.IDR_WEIGHTED_SUM, Windows.MFS_UNCHECKED);
                WindowUtils.ChangeMenuState(hwnd, Resource.IDR_PRIORITIZED, Windows.MFS_UNCHECKED);
                WindowUtils.ChangeMenuState(hwnd, Resource.IDR_DITHERED, Windows.MFS_CHECKED);
                for (int i = 0; i < vehicles.size(); ++i) {
                    vehicles.get(i).getSteering().SetSummingMethod(SteeringBehavior.SummingMethod.DITHERED);
                }
                break;
                
            case Resource.ID_VIEW_KEYS:
                toggleViewKeys();
                WindowUtils.CheckMenuItemAppropriately(hwnd, Resource.ID_VIEW_KEYS, viewKeys);
                break;
                
            case Resource.ID_VIEW_FPS:
                toggleShowFPS();
                WindowUtils.CheckMenuItemAppropriately(hwnd, Resource.ID_VIEW_FPS, isShowFPS());
                break;
                
            case Resource.ID_MENU_SMOOTHING:
                for (int i = 0; i < vehicles.size(); ++i) {
                    vehicles.get(i).toggleSmoothing();
                }
                WindowUtils.CheckMenuItemAppropriately(hwnd, Resource.ID_MENU_SMOOTHING, vehicles.get(0).isSmoothingOn());
                break;
        }
    }

    synchronized public void render() {
        Cgdi.gdi.TransparentText();
        //render any walls
        Cgdi.gdi.BlackPen();
        for (int w = 0; w < walls.size(); ++w) {
            walls.get(w).Render(true);  //true flag shows normals
        }
        //render any obstacles
        Cgdi.gdi.BlackPen();
        for (int ob = 0; ob < obstacles.size(); ++ob) {
            Cgdi.gdi.Circle(obstacles.get(ob).getPos(), obstacles.get(ob).getBoundingRadius());
        }

        //render the agents
        for (int a = 0; a < vehicles.size(); ++a) {
            vehicles.get(a).render(a == 0);
            //render cell partitioning stuff
            if (showCellSpaceInfo && a == 0) {
                Cgdi.gdi.HollowBrush();
                InvertedAABBox2D box = new InvertedAABBox2D(
                        Vector2D.sub(vehicles.get(a).getPos(), new Vector2D(ParamLoader.prm.ViewDistance, ParamLoader.prm.ViewDistance)),
                        Vector2D.add(vehicles.get(a).getPos(), new Vector2D(ParamLoader.prm.ViewDistance, ParamLoader.prm.ViewDistance)));
                box.Render();
                Cgdi.gdi.RedPen();
                getCellSpace().calculateNeighbors(vehicles.get(a).getPos(), ParamLoader.prm.ViewDistance);
                for (BaseGameEntity pV = getCellSpace().begin(); !getCellSpace().end(); pV = getCellSpace().next()) {
                    Cgdi.gdi.Circle(pV.getPos(), pV.getBoundingRadius());
                }
                Cgdi.gdi.GreenPen();
                Cgdi.gdi.Circle(vehicles.get(a).getPos(), ParamLoader.prm.ViewDistance);
            }
        }

        boolean CROSSHAIR = false;
        if (CROSSHAIR) {
            //and finally the crosshair
            Cgdi.gdi.RedPen();
            Cgdi.gdi.Circle(crosshair, 4);
            Cgdi.gdi.Line(crosshair.x - 8, crosshair.y, crosshair.x + 8, crosshair.y);
            Cgdi.gdi.Line(crosshair.x, crosshair.y - 8, crosshair.x, crosshair.y + 8);
            Cgdi.gdi.TextAtPos(5, getYClient() - 20, "Click to move crosshair");
        }

        //gdi->TextAtPos(cxClient() -120, cyClient() - 20, "Press R to reset");
        Cgdi.gdi.TextColor(Cgdi.grey);
        if (isShowPath()) {
            Cgdi.gdi.TextAtPos((int)(getXClient() / 2.0f - 80), getYClient() - 20, "Press 'U' for random path");
            path.Render();
        }
        if (isShowFPS()) {
            Cgdi.gdi.TextColor(Cgdi.grey);
            Cgdi.gdi.TextAtPos(5, getYClient() - 20, StreamUtilityFunction.ttos(1.0 / avFrameTime));
        }
        if (showCellSpaceInfo) {
            cellSpace.renderCells();
        }

    }
}
