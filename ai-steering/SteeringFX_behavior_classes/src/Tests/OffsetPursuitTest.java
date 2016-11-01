
package Tests;

import Steering.GameWorld;
import Steering.Params;
import Steering.Vehicle;
import Steering.behaviors.BehaviorType;
import Steering.behaviors.OffsetPursuit;
import common.D2.Vector2D;
import java.util.List;

public class OffsetPursuitTest {
      
    private GameWorld gameWorld;
    
    public OffsetPursuitTest (int cx, int cy) {
        gameWorld = new GameWorld(cx, cy);
        // Тестируем 
        List<Vehicle> vehicleList =  gameWorld.getVehicleList();
        Vector2D offset = new Vector2D (50, 50);
        
        // инициировать поведения всех агентов - следовать за главным агентом на расстоянии offset
        for (int i = 1; i < Params.NUM_AGENTS; ++i) {
            Vehicle vehicle = vehicleList.get(i);
            
            OffsetPursuit offsetPursuit = (OffsetPursuit)vehicle.getBehaviorManager().get(BehaviorType.OFFSET_PURSUIT);
            offsetPursuit.setOffset(offset);
            offsetPursuit.setTargetAgent(vehicleList.get(0));
            offsetPursuit.on();           
        }
        
        // инициировать поведение главного агента
        vehicleList.get(0).getBehaviorManager().on(BehaviorType.WANDER);
    
    }
    
    public GameWorld getGameWorld() {
        return gameWorld;
    }   
}
