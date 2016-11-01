/**
 * 
 *  Desc:   All the environment data and methods for the Steering
 *          Behavior projects. This class is the root of the project's
 *          update and render calls (excluding main of course)
 */
package Chapter3Steering;

import common.D2.InvertedAABBox2D;
import java.util.ArrayList;
import common.D2.Vector2D;
import java.util.List;
import common.misc.CellSpacePartition;
import common.misc.Utils;
import common.misc.Smoother;
import common.misc.Cgdi;
import common.misc.StreamUtilityFunction;

public class GameWorld {
    //a container of all the moving entities
    private List<Vehicle> vehicles = new ArrayList<Vehicle>(ParamLoader.prm.NumAgents);

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
    
    public CellSpacePartition<Vehicle> getCellSpace() {
        return cellSpace;
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

    synchronized public void render() {
        Cgdi.gdi.TransparentText();
              
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
