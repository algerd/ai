/**
 * 
 *  Desc:   All the environment data and methods for the Steering
 *          Behavior projects. This class is the root of the project's
 *          update and render calls (excluding main of course)
 */
package Chapter3Steering;

import java.util.ArrayList;
import common.D2.Vector2D;
import java.util.List;
import common.misc.CellSpacePartition;
import common.misc.Utils;

public class GameWorld {
    //a container of all the moving entities
    private List<Vehicle> vehicles = new ArrayList<Vehicle>(ParamLoader.prm.NumAgents);

    private CellSpacePartition<Vehicle> cellSpace;
    //local copy of client window dimensions
    private int xClient, yClient;
    //the position of the crosshair
    private Vector2D crosshair;

    final static int SampleRate = 10;
    
    public GameWorld(int cx, int cy) {
        xClient = cx;
        yClient = cy;
        crosshair = new Vector2D(getXClient() / 2.0, getYClient() / 2.0);
        double border = 30;
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

            vehicle.getSteering().flockingOn();
            vehicles.add(vehicle);
            cellSpace.addEntity(vehicle);
        } 
        
        vehicles.get(ParamLoader.prm.NumAgents - 1).getSteering().flockingOff();
        vehicles.get(ParamLoader.prm.NumAgents - 1).setScale(new Vector2D(10, 10));
        vehicles.get(ParamLoader.prm.NumAgents - 1).getSteering().wanderOn();
        vehicles.get(ParamLoader.prm.NumAgents - 1).setMaxSpeed(70);
        for (int i = 0; i < ParamLoader.prm.NumAgents - 1; ++i) {
            vehicles.get(i).getSteering().evadeOn(vehicles.get(ParamLoader.prm.NumAgents - 1));
        }
    }
    
    //update the vehicles
    public void update(double timeElapsed) {       
        for (int a = 0; a < vehicles.size(); ++a) {
            vehicles.get(a).update(timeElapsed);
        }
    }
    
    //render the agents
    public void render() {                 
        for (int a = 0; a < vehicles.size(); ++a) {
            vehicles.get(a).render();        
        }
    }
 
    public CellSpacePartition<Vehicle> getCellSpace() {
        return cellSpace;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public Vector2D getCrosshair() {
        return crosshair;
    }

    public int getXClient() {
        return xClient;
    }

    public int getYClient() {
        return yClient;
    }
  
}
