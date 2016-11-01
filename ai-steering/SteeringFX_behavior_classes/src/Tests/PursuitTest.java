
package Tests;

import Steering.GameWorld;
import Steering.Params;
import Steering.Vehicle;
import Steering.behaviors.Behavior;
import Steering.behaviors.BehaviorType;
import Steering.behaviors.Pursuit;
import java.util.List;

public class PursuitTest {
      
    private GameWorld gameWorld;
    
    public PursuitTest (int cx, int cy) {
        gameWorld = new GameWorld(cx, cy);
        // Тестируем 
        List<Vehicle> vehicleList =  gameWorld.getVehicleList();
      
        // инициировать поведения всех агентов - преследовать главного агента
        for (int i = 1; i < Params.NUM_AGENTS; ++i) {
            Vehicle vehicle = vehicleList.get(i);
            
            //vehicle.getBehaviorManager().on(BehaviorType.WANDER);
            
            Behavior pursuit = vehicle.getBehaviorManager().get(BehaviorType.PURSUIT);          
            ((Pursuit)pursuit).setTargetAgent(vehicleList.get(0));
            pursuit.on();     
        }
        
        // инициировать поведение главного агента
        vehicleList.get(0).getBehaviorManager().on(BehaviorType.WANDER);
    
    }
    
    public GameWorld getGameWorld() {
        return gameWorld;
    }   
}
