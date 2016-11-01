
package Tests;

import Steering.GameWorld;
import Steering.Params;
import Steering.Vehicle;
import Steering.behaviors.Arrive;
import Steering.behaviors.BehaviorType;
import common.D2.Vector2D;
import java.util.List;

public class SeekFleeArriveTest {
      
    private GameWorld gameWorld;
    
    public SeekFleeArriveTest (int cx, int cy) {
        gameWorld = new GameWorld(cx, cy);
        // Тестируем 
        List<Vehicle> vehicleList =  gameWorld.getVehicleList();
        
        // Задать целевую точку движения агентов (для seek(), flee(), arrive())
        Vector2D targetMove = new Vector2D(cx / 2.0, cy / 2.0); 
        for (Vehicle vehicle : vehicleList) {
               
            //Behavior behavior = vehicle.getBehaviorManager().getBehavior(BehaviorEnum.SEEK);
            //((Seek)behavior).setTargetPos(targetMove);
            
            //Behavior behavior = vehicle.getBehaviorManager().getBehavior(BehaviorEnum.FLEE);
            //((Flee)behavior).setTargetPos(targetMove);
            
            Arrive behavior = (Arrive)vehicle.getBehaviorManager().get(BehaviorType.ARRIVE);
            behavior.setDeceleration(Params.DECELERATION_SLOW);
            behavior.setTargetPos(targetMove);
            
            behavior.on();
        }    
    }
    
    public GameWorld getGameWorld() {
        return gameWorld;
    }   
}
