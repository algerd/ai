
package Tests;

import Steering.GameWorld;
import Steering.Vehicle;
import Steering.Wall;
import Steering.WallFactory;
import Steering.behaviors.BehaviorType;
import java.util.List;

public class SmoothingTest {
      
    private GameWorld gameWorld;
    
    public SmoothingTest (int cx, int cy) {
        gameWorld = new GameWorld(cx, cy);
        // Тестируем 
        List<Vehicle> vehicleList =  gameWorld.getVehicleList();
        List<Wall> wallList = gameWorld.getWallList();
        
        // создать препятствия
        wallList.addAll(new WallFactory().create(cx, cy));
        
        // включить сглаживание движения агентов
        for (Vehicle vehicle : vehicleList) {
            vehicle.setSmoothingOn();
            vehicle.getBehaviorManager().on(BehaviorType.WALL_AVOIDANCE);
            vehicle.getBehaviorManager().on(BehaviorType.WANDER); 
        }
     
    }
    
    public GameWorld getGameWorld() {
        return gameWorld;
    }   
}
