
package Steering.behaviors;

import Steering.BaseGameEntity;
import Steering.EntityFunctionTemplates;
import Steering.Params;
import Steering.Vehicle;
import common.D2.Vector2D;
import common.misc.CellSpacePartition;
import java.util.List;

public class Separation extends Behavior {
    
    private Vehicle vehicle;
    private boolean cellSpaceOn;
    private boolean tagAllNeighbors = true;
    private boolean tagCellNeighbors = true;        
    private final double WEIGHT = Params.SEPARATION_WEIGHT;
    private final double PROBABILITY = Params.PR_SEPARATION;
    private final double VIEW_DISTANCE = Params.VIEW_DISTANCE;
    
    public Separation(Vehicle vehicle) {
        this.vehicle = vehicle;
        cellSpaceOn = vehicle.getWorld().isCellSpaceOn();
    }
      
    @Override
    public Vector2D getWeightForce() {
        return Vector2D.mul(getForce(), WEIGHT);
    }
    
    @Override
    public Vector2D getDitheredForce() {
        return Vector2D.mul(getForce(), WEIGHT/PROBABILITY);
    }
    
    @Override
    public Vector2D getForce() {
        Vector2D force = new Vector2D();
        List<Vehicle> vehicleList = vehicle.getWorld().getVehicleList();
        
        if (cellSpaceOn) {
            if (tagCellNeighbors) {
                // calculate neighbors in cell-space
                vehicle.getWorld().getCellSpace().calculateNeighbors(vehicle.getPos(), VIEW_DISTANCE);              
            }
            CellSpacePartition cellSpase = vehicle.getWorld().getCellSpace();
            List<BaseGameEntity> neighborList = cellSpase.getNeighborList();   

            //iterate through the neighbors and sum up all the position vectors
            for (BaseGameEntity entity : neighborList) {
                //make sure this agent isn't included in the calculations and that the agent being examined is close enough
                if (entity != vehicle) {
                    Vector2D toAgent = Vector2D.sub(vehicle.getPos(), entity.getPos());
                    //scale the force inversely proportional to the agents distance from its neighbor.
                    force.add(Vector2D.div(Vector2D.vec2DNormalize(toAgent), toAgent.length()));
                }
            }           
        }
        else {
            if (tagAllNeighbors) {
                // tag all vehicles within range
                EntityFunctionTemplates.tagNeighbors(vehicle, vehicle.getWorld().getVehicleList(), VIEW_DISTANCE);
            }
            Evade evade = (Evade)vehicle.getBehaviorManager().get(BehaviorType.EVADE);
            Vehicle targetAgent = evade.getTargetAgent();
            for (Vehicle veh : vehicleList) {
                //make sure this agent isn't included in the calculations and that
                //the agent being examined is close enough. ***also make sure it doesn't include the evade target ***              
                if ((veh != vehicle) && veh.isTagged() && (veh != targetAgent)) {
                    Vector2D toAgent = Vector2D.sub(vehicle.getPos(), veh.getPos());
                    //scale the force inversely proportional to the agents distance from its neighbor.
                    force.add(Vector2D.div(Vector2D.vec2DNormalize(toAgent), toAgent.length()));
                }
            }
        }
        return force;          
    }
    
    public void setTagAllNeighbors(boolean tagAllNeighbors) {
        this.tagAllNeighbors = tagAllNeighbors;
    }

    public void setTagCellNeighbors(boolean tagCellNeighbors) {
        this.tagCellNeighbors = tagCellNeighbors;
    }
    
}
