/**
 * 
 *  Desc:   All the environment data and methods for the Steering
 *          Behavior projects. This class is the root of the project's
 *          update and render calls (excluding main of course)
 */
package Steering;

import java.util.ArrayList;
import common.D2.Vector2D;
import java.util.List;
import common.misc.Cgdi;
import common.misc.Utils;

public class GameWorld {
    //a container of all the moving entities
    private List<Vehicle> vehicles = new ArrayList<Vehicle>(Params.NUM_AGENTS);
    //local copy of client window dimensions
    private int xClient, yClient;
    //the position of the crosshair
    private Vector2D targetMove;
    
    public GameWorld(int cx, int cy) {
        xClient = cx;
        yClient = cy;
        targetMove = new Vector2D(getXClient() / 2.0, getYClient() / 2.0);
                
        BaseGameEntity.resetValidID();
        for (int a = 0; a < Params.NUM_AGENTS; ++a) {
            //determine a random starting position
            Vector2D SpawnPos = new Vector2D(
                cx / 2.0 + Utils.RandomClamped() * cx / 2.0,
                cy / 2.0 + Utils.RandomClamped() * cy / 2.0);

            Vehicle vehicle = new Vehicle(
                this,
                SpawnPos,                       //initial position
                Utils.RandFloat() * Utils.TwoPi,//start rotation
                new Vector2D(0, 0),             //velocity
                Params.VEHICLE_MASS,            //mass
                Params.MAX_STEERING_FORCE,      //max force
                Params.MAX_SPEED,               //max velocity
                Params.MAX_TURN_RATE_PER_SECOND,//max turn rate
                Params.VEHICLE_SCALE);          //scale

            vehicle.getSteering().getBehavFlag().flockingOn();
            vehicles.add(vehicle);
        } 
        
        // properties of big vehicle
        vehicles.get(Params.NUM_AGENTS - 1).getSteering().getBehavFlag().flockingOff();
        vehicles.get(Params.NUM_AGENTS - 1).setScale(new Vector2D(10, 10));
        vehicles.get(Params.NUM_AGENTS - 1).getSteering().getBehavFlag().wanderOn();
        vehicles.get(Params.NUM_AGENTS - 1).setMaxSpeed(70);
        for (int i = 0; i < Params.NUM_AGENTS - 1; ++i) {
            // включить поведение "избегать столкновение" и задать цели возможных столкновений  
            vehicles.get(i).getSteering().getBehavFlag().evadeOn();
            vehicles.get(i).getSteering().setTergetAgent(vehicles.get(Params.NUM_AGENTS - 1));          
        }
    }
    
    //update the vehicles
    public void update(double timeElapsed) {       
        for (int a = 0; a < vehicles.size(); ++a) {
            vehicles.get(a).update(timeElapsed);
        }
    }
    
    /**
     * render game agents.
     */
    public void render(Cgdi cgdi) {                 
        renderVehicles(cgdi);
    }
    
    private void renderVehicles(Cgdi cgdi) {                 
        for (int a = 0; a < vehicles.size(); ++a) {
            cgdi.drawPolygon(vehicles.get(a).getTransformVehicle());
        }
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public Vector2D getTargetMove() {
        return targetMove;
    }

    public int getXClient() {
        return xClient;
    }

    public int getYClient() {
        return yClient;
    }
  
}
