
package Steering.behaviors;

import Steering.EntityFunctionTemplates;
import Steering.MovingEntity;
import Steering.Params;
import Steering.Vehicle;
import common.D2.Vector2D;
import common.misc.CellSpacePartition;
import java.util.List;

public class Alignment extends Behavior {
    
    private Vehicle vehicle;
    private boolean cellSpaceOn;
    private boolean tagAllNeighbors = true;
    private boolean tagCellNeighbors = true;        
    private final double WEIGHT = Params.ALIGNMENT_WEIGHT;
    private final double PROBABILITY = Params.PR_ALIGNMENT;
    private final double VIEW_DISTANCE = Params.VIEW_DISTANCE;
    
    public Alignment(Vehicle vehicle) {
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
        
        //used to count the number of vehicles in the neighborhood
        double neighborCount = 0;
        
        if (cellSpaceOn) {
            if (tagCellNeighbors) {
                // calculate neighbors in cell-space
                vehicle.getWorld().getCellSpace().calculateNeighbors(vehicle.getPos(), VIEW_DISTANCE);              
            }
            
            CellSpacePartition cellSpase = vehicle.getWorld().getCellSpace();
            List<MovingEntity> neighborList = cellSpase.getNeighborList();   
                    
            //iterate through the neighbors and sum up all the position vectors
            for (MovingEntity entity : neighborList) {
                //make sure *this* agent isn't included in the calculations and that the agent being examined  is close enough
                if (entity != vehicle) {
                    force.add(entity.getHeading());
                    ++neighborCount;
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
            //iterate through all the tagged vehicles and sum their heading vectors
            for (Vehicle veh : vehicleList) {
                //make sure *this* agent isn't included in the calculations and that
                //the agent being examined  is close enough ***also make sure it doesn't include any evade target ***
                if ((veh != vehicle) && veh.isTagged() && (veh != targetAgent)) {
                    force.add(veh.getHeading());
                    ++neighborCount;
                }
            }                  
        }
        //if the neighborhood contained one or more vehicles, average their heading vectors.
        if (neighborCount > 0) {
            force.div(neighborCount);
            force.sub(vehicle.getHeading());
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
