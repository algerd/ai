/**
 * 
 *  Desc:   All the environment data and methods for the Steering
 *          Behavior projects. This class is the root of the project's
 *          update and render calls (excluding main of course)
 */
package Steering;

import java.util.ArrayList;
import common.D2.Vector2D;
import common.misc.CellSpacePartition;
import java.util.List;
import common.misc.Cgdi;
import common.misc.Utils;

public class GameWorld {
   
    private List<Vehicle> vehicleList = new ArrayList<>();
    public List<Obstacle> obstacleList = new ArrayList<>();
    private List<Wall> wallList = new ArrayList<>();
    private CellSpacePartition<Vehicle> cellSpace;
    //local copy of client window dimensions
    private int xClient, yClient;
    // для тестирования
    private boolean cellSpaceOn;
    
    public GameWorld(int cx, int cy) {
        xClient = cx;
        yClient = cy;
        cellSpace = new CellSpacePartition<>(cx, cy, Params.NUM_CELLS_X, Params.NUM_CELLS_Y);
        
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
                Params.VEHICLE_MASS,
                Params.MAX_STEERING_FORCE,
                Params.MAX_SPEED,
                Params.MAX_TURN_RATE_PER_SECOND,
                Params.VEHICLE_SCALE
            );         
                 
            vehicleList.add(vehicle);
        } 
        vehicleList.get(0).setScale(new Vector2D(10, 10));
        vehicleList.get(0).setMaxSpeed(70);     
    }
     
    public void update(double timeElapsed) {
        for (Vehicle vehicle : vehicleList) {
            vehicle.update(timeElapsed);        
            if (cellSpaceOn) {
                // сместить позициию агента, если он накладывается на другого агента, на величину наложения
                // используя Spatial Partitioning (CellSpacePartition)!!!                
                cellSpace.calculateNeighbors(vehicle.getPos(), Params.VIEW_DISTANCE);
                EntityFunctionTemplates.enforceNonPenetrationConstraint(vehicle, cellSpace.getNeighborList());
            } 
            else {
                // сместить позициию агента, если он накладывается на другого агента, на величину наложения
                // очень затратная функция, её надо использовать в Spatial Partitioning (CellSpacePartition)!!!,чтобы снизить издержки производительности. 
                // В прямом виде использовать при числе проверяемых агентов < 20
                EntityFunctionTemplates.enforceNonPenetrationConstraint(vehicle, vehicleList);
            }    
        }
    }
       
    /**
     * ********************** Render game agents. ***********************
     */
    public void render(Cgdi cgdi) {                 
        renderVehicles(cgdi);
        renderObstacles(cgdi);
        renderWalls(cgdi);
        renderPath(cgdi);
        renderCells(cgdi);
    }
    
    private void renderVehicles(Cgdi cgdi) {                 
        for (Vehicle vehicle : vehicleList) {
            vehicle.render(cgdi);
        }
    }
    
    private void renderObstacles(Cgdi cgdi) {
        for (Obstacle obstacle : obstacleList) {
            obstacle.render(cgdi);
        }       
    }
    
    private void renderWalls(Cgdi cgdi) {
        for (Wall wall : wallList) {
            wall.render(cgdi);
        }
    }
    
    private void renderPath(Cgdi cgdi) {
        for (Vehicle vehicle : vehicleList) {
            Path path = vehicle.getSteering().getBehaviors().getPath();
            if (path != null) {
                path.render(cgdi);
            }
        }
    }
    
    private void renderCells(Cgdi cgdi) {
        cellSpace.renderCells(cgdi);   
    }
    
    /**
     ************************ Getters & setters. *************************
     */
    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }
    
    public List<Obstacle> getObstacleList() {
        return obstacleList;
    }
    
    public List<Wall> getWallList() {
        return wallList;
    }
    
    public CellSpacePartition<Vehicle> getCellSpace() {
        return cellSpace;
    }

    public int getXClient() {
        return xClient;
    }

    public int getYClient() {
        return yClient;
    }

    public void cellSpaceOn() {
        this.cellSpaceOn = true;
    }
     
}
