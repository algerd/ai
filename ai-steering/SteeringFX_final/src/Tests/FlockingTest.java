
package Tests;

import Steering.GameWorld;
import Steering.Params;
import Steering.Vehicle;
import java.util.List;

public class FlockingTest {
      
    private GameWorld gameWorld;
    
    public FlockingTest (int cx, int cy) {
        gameWorld = new GameWorld(cx, cy);
        // Тестируем 
        List<Vehicle> vehicleList =  gameWorld.getVehicleList();
       
        // инициировать поведения всех агентов
        for (int i = 1; i < Params.NUM_AGENTS; ++i) {
            Vehicle vehicle = vehicleList.get(i);
            vehicle.getSteering().getBehavFlag().flockingOn();
            
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
