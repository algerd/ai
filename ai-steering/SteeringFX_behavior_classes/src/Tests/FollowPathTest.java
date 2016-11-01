
package Tests;

import Steering.GameWorld;
import Steering.Vehicle;
import common.misc.Utils;
import Steering.Path;
import Steering.behaviors.Behavior;
import Steering.behaviors.BehaviorType;
import Steering.behaviors.FollowPath;
import java.util.List;

public class FollowPathTest {
      
    private GameWorld gameWorld;
    
    public FollowPathTest (int cx, int cy) {
        gameWorld = new GameWorld(cx, cy);
        List<Vehicle> vehicleList =  gameWorld.getVehicleList();
      
        double border = 60;
        Path path = new Path(Utils.RandInt(3, 7), border, border, cx - border, cy - border, true);
        
        for(Vehicle vehicle : vehicleList) {
            Behavior followPath = vehicle.getBehaviorManager().get(BehaviorType.FOLLOW_PATH);
            ((FollowPath)followPath).setPath(path.clone());
            followPath.on();
        }
        
    }
    
    public GameWorld getGameWorld() {
        return gameWorld;
    }   
}
