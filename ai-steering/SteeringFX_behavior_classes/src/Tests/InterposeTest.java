
package Tests;

import Steering.GameWorld;
import Steering.Params;
import Steering.Vehicle;
import Steering.behaviors.Behavior;
import Steering.behaviors.BehaviorType;
import Steering.behaviors.Evade;
import Steering.behaviors.Interpose;
import java.util.List;

public class InterposeTest {
      
    private GameWorld gameWorld;
    
    public InterposeTest (int cx, int cy) {
        gameWorld = new GameWorld(cx, cy);
        // Тестируем 
        List<Vehicle> vehicleList =  gameWorld.getVehicleList();
      
        // инициировать поведения двух агентов
        for (int i = 1; i < 3; ++i) {
            Vehicle vehicle = vehicleList.get(i);            
            vehicle.getBehaviorManager().on(BehaviorType.WANDER);           
        }
        
        // инициировать поведение главного агента - двигаться между двумя заданными агентами
        Interpose interpose = (Interpose)vehicleList.get(0).getBehaviorManager().get(BehaviorType.INTERPOSE);
        interpose.setTargetAgent1(vehicleList.get(1));
        interpose.setTargetAgent2(vehicleList.get(2));
        interpose.on();
    }
    
    public GameWorld getGameWorld() {
        return gameWorld;
    }   
}
