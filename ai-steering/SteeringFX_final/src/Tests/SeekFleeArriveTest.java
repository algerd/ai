
package Tests;

import Steering.GameWorld;
import Steering.Vehicle;
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
            //vehicle.getSteering().getBehavFlag().seekOn();
            vehicle.getSteering().getBehavFlag().arriveOn();
            vehicle.getSteering().getBehaviors().setTargetMove(targetMove);
        }
       
    }
    
    public GameWorld getGameWorld() {
        return gameWorld;
    }   
}
