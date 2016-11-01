
package Tests;

import Steering.GameWorld;
import Steering.Obstacle;
import Steering.ObstacleFactory;
import Steering.Vehicle;
import Steering.behaviors.Behavior;
import Steering.behaviors.BehaviorType;
import Steering.behaviors.Hide;
import java.util.List;

public class HideTest {
      
    private GameWorld gameWorld;
    
    public HideTest (int cx, int cy) {
        gameWorld = new GameWorld(cx, cy);
        // Тестируем 
        List<Vehicle> vehicleList =  gameWorld.getVehicleList();
        List<Obstacle> obstacleList = gameWorld.getObstacleList();
        
        // создать препятствия
        obstacleList.addAll(new ObstacleFactory().create(cx, cy));
        
        // инициировать поведения
        for (Vehicle vehicle : vehicleList) {
            vehicle.getBehaviorManager().on(BehaviorType.OBSTACLE_AVOIDANCE);
            Behavior hide = vehicle.getBehaviorManager().get(BehaviorType.HIDE);
            ((Hide)hide).setHunter(vehicleList.get(0));
            hide.on();
        }
        vehicleList.get(0).getBehaviorManager().on(BehaviorType.WANDER);
        vehicleList.get(0).getBehaviorManager().off(BehaviorType.HIDE);
        
    }
    
    public GameWorld getGameWorld() {
        return gameWorld;
    }   
}
