
package Steering.behaviors;

import Steering.BaseGameEntity;
import Steering.EntityFunctionTemplates;
import Steering.Params;
import Steering.Vehicle;
import common.D2.Vector2D;
import common.misc.CellSpacePartition;
import java.util.List;

public class Cohesion extends Behavior {
    
    private Vehicle vehicle;
    private boolean cellSpaceOn;
    private boolean tagAllNeighbors = true;
    private boolean tagCellNeighbors = true; 
    private Seek seek;
    private final double WEIGHT = Params.COHESION_WEIGHT;
    private final double PROBABILITY = Params.PR_COHESION;
    private final double VIEW_DISTANCE = Params.VIEW_DISTANCE;
    
    public Cohesion(Vehicle vehicle) {
        this.vehicle = vehicle;
        seek = new Seek(vehicle);
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
        //first find the center of mass of all the agents
        Vector2D centerOfMass = new Vector2D();
             
        if (cellSpaceOn) {
            if (tagCellNeighbors) {
                // calculate neighbors in cell-space
                vehicle.getWorld().getCellSpace().calculateNeighbors(vehicle.getPos(), VIEW_DISTANCE);              
            }
            CellSpacePartition cellSpase = vehicle.getWorld().getCellSpace();
            List<BaseGameEntity> neighborList = cellSpase.getNeighborList();

            //iterate through the neighbors and sum up all the position vectors 
            for (BaseGameEntity entity : neighborList) {
                //make sure *this* agent isn't included in the calculations and that the agent being examined is close enough
                if (entity != vehicle) {
                    centerOfMass.add(entity.getPos());
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
            //iterate through the neighbors and sum up all the position vectors
            for (Vehicle veh : vehicleList) {
                //make sure *this* agent isn't included in the calculations and that
                //the agent being examined is close enough ***also make sure it doesn't include the evade target ***
                if ((veh != vehicle) && veh.isTagged() && (veh != targetAgent)) {
                    centerOfMass.add(veh.getPos());
                    ++neighborCount;
                }
            }               
        } 
        if (neighborCount > 0) {
            //the center of mass is the average of the sum of positions
            centerOfMass.div(neighborCount);
            //now seek towards that position
            seek.setTargetPos(centerOfMass);           
            force = seek.getForce();
        }
        //the magnitude of cohesion is usually much larger than separation or allignment so it usually helps to normalize it.
        force = Vector2D.vec2DNormalize(force);
        return force;          
    }
    
    public void setTagAllNeighbors(boolean tagAllNeighbors) {
        this.tagAllNeighbors = tagAllNeighbors;
    }

    public void setTagCellNeighbors(boolean tagCellNeighbors) {
        this.tagCellNeighbors = tagCellNeighbors;
    }
}
