
package Tests;

import Steering.GameWorld;
import Steering.Params;
import Steering.Vehicle;
import Steering.behaviors.BehaviorType;
import Steering.behaviors.Evade;
import java.util.List;

public class CohesionTest {
      
    private GameWorld gameWorld;
    
    public CohesionTest (int cx, int cy) {
        gameWorld = new GameWorld(cx, cy);
        // Тестируем 
        List<Vehicle> vehicleList =  gameWorld.getVehicleList();
       
        // инициировать поведения всех агентов
        for (int i = 1; i < Params.NUM_AGENTS; ++i) {
            Vehicle vehicle = vehicleList.get(i);
            
            // включить поведение "избегать столкновение" и задать цели возможных столкновений
            // Evade включать до включения групповых поведений, чтобы они учли задаваемого в evade targetAgent
            Evade evade = (Evade)vehicle.getBehaviorManager().get(BehaviorType.EVADE);
            evade.setTargetAgent(vehicleList.get(0));
            evade.on();
            
            vehicle.getBehaviorManager().on(BehaviorType.WANDER);
            vehicle.getBehaviorManager().on(BehaviorType.COHESION);
            vehicle.getBehaviorManager().on(BehaviorType.ALIGNMENT);
            vehicle.getBehaviorManager().on(BehaviorType.SEPARATION);                 
        }
        
        // инициировать поведение главного агента
        vehicleList.get(0).getBehaviorManager().on(BehaviorType.WANDER);
    }
    
    public GameWorld getGameWorld() {
        return gameWorld;
    }   
}
