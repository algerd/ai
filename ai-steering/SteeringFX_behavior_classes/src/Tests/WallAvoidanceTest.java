
package Tests;

import Steering.GameWorld;
import Steering.Vehicle;
import Steering.Wall;
import Steering.WallFactory;
import Steering.behaviors.BehaviorType;
import java.util.List;

public class WallAvoidanceTest {
      
    private GameWorld gameWorld;
    
    public WallAvoidanceTest (int cx, int cy) {
        gameWorld = new GameWorld(cx, cy);
        // Тестируем 
        List<Vehicle> vehicleList =  gameWorld.getVehicleList();
        List<Wall> wallList = gameWorld.getWallList();
        
        // создать препятствия
        wallList.addAll(new WallFactory().create(cx, cy));
        
        // инициировать поведения
        for (Vehicle vehicle : vehicleList) {
            vehicle.getBehaviorManager().on(BehaviorType.WANDER);
            vehicle.getBehaviorManager().on(BehaviorType.WALL_AVOIDANCE);
        }
    }
    
    public GameWorld getGameWorld() {
        return gameWorld;
    }   
}
