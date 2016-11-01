
package Tests;

import Steering.GameWorld;
import Steering.Params;
import Steering.Vehicle;
import Steering.behaviors.Behavior;
import Steering.behaviors.BehaviorType;
import Steering.behaviors.Evade;
import java.util.List;

public class EvadeTest {
      
    private GameWorld gameWorld;
    
    public EvadeTest (int cx, int cy) {
        gameWorld = new GameWorld(cx, cy);
        // Тестируем 
        List<Vehicle> vehicleList =  gameWorld.getVehicleList();
      
        // инициировать поведения всех агентов - избегать главного агента
        for (int i = 1; i < Params.NUM_AGENTS; ++i) {
            Vehicle vehicle = vehicleList.get(i);
            
            vehicle.getBehaviorManager().on(BehaviorType.WANDER);
            
            Behavior evade = vehicle.getBehaviorManager().get(BehaviorType.EVADE);          
            ((Evade)evade).setTargetAgent(vehicleList.get(0));
            evade.on();     
        }
        
        // инициировать поведение главного агента
        vehicleList.get(0).getBehaviorManager().on(BehaviorType.WANDER); 
    }
    
    public GameWorld getGameWorld() {
        return gameWorld;
    }   
}
