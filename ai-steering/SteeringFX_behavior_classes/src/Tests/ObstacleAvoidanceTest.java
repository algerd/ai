
package Tests;

import Steering.GameWorld;
import Steering.Obstacle;
import Steering.ObstacleFactory;
import Steering.Vehicle;
import Steering.behaviors.BehaviorType;
import java.util.List;

public class ObstacleAvoidanceTest {
      
    private GameWorld gameWorld;
    
    public ObstacleAvoidanceTest (int cx, int cy) {
        gameWorld = new GameWorld(cx, cy);
        // Тестируем 
        List<Vehicle> vehicleList =  gameWorld.getVehicleList();
        List<Obstacle> obstacleList = gameWorld.getObstacleList();
        
        // создать препятствия
        obstacleList.addAll(new ObstacleFactory().create(cx, cy));
        
        // инициировать поведения
        for (Vehicle vehicle : vehicleList) {
            vehicle.getBehaviorManager().on(BehaviorType.WANDER);
            vehicle.getBehaviorManager().on(BehaviorType.OBSTACLE_AVOIDANCE);
        }
    }
    
    public GameWorld getGameWorld() {
        return gameWorld;
    }   
}
