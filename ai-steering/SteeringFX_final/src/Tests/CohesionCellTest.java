
package Tests;

import Steering.GameWorld;
import Steering.Params;
import Steering.Vehicle;
import common.misc.CellSpacePartition;
import java.util.List;

public class CohesionCellTest {
      
    private GameWorld gameWorld;
    
    public CohesionCellTest (int cx, int cy) {
        gameWorld = new GameWorld(cx, cy);
        // Тестируем 
        List<Vehicle> vehicleList =  gameWorld.getVehicleList();
        CellSpacePartition<Vehicle> cellSpace = gameWorld.getCellSpace();
        gameWorld.cellSpaceOn();
        
        for (int i = 1; i < Params.NUM_AGENTS; ++i) {
            Vehicle vehicle = vehicleList.get(i); 
            // add vehicles to cell space and mark them
            cellSpace.addEntity(vehicle);
            vehicle.getSteering().spacePartitioningOn();
            
            // инициировать поведения агентов
            vehicle.getSteering().getBehavFlag().wanderOn();
            vehicle.getSteering().getBehavFlag().cohesionOn();
            vehicle.getSteering().getBehavFlag().alignmentOn();
            
            // включить поведение "избегать столкновение" и задать цели возможных столкновений 
            vehicle.getSteering().getBehavFlag().evadeOn();
            vehicle.getSteering().getBehaviors().setTargetAgent1(vehicleList.get(0));
        }
        // инициировать поведение главного агента
        vehicleList.get(0).getSteering().getBehavFlag().wanderOn();
        
    }
    
    public GameWorld getGameWorld() {
        return gameWorld;
    }   
}
