
package Steering;

import common.misc.Utils;
import java.util.ArrayList;
import java.util.List;

public class ObstacleFactory {
    
   public List<Obstacle> create(int xClient, int yClient) {
        List<Obstacle> obstacleList = new ArrayList<>(Params.NUM_OBSTACLES);       
       
        for (int i = 0; i < Params.NUM_OBSTACLES; ++i) {       
            //keep creating tiddlywinks until we find one that doesn't overlap any others.
            //Sometimes this can get into an endless loop because the obstacle has nowhere to fit.
            int numTrys = 0;
            int numAllowableTrys = 2000;
            while (true) {
                numTrys++;
                if (numTrys > numAllowableTrys) {
                    return obstacleList;
                }
                int radius = Utils.RandInt(Params.MIN_OBSTACLE_RADIUS, Params.MAX_OBSTACLE_RADIUS);
                final int border = 10;
                final int minGapBetweenObstacles = 20;

                Obstacle obstacle = new Obstacle(
                    Utils.RandInt(radius + border, xClient - radius - border),
                    Utils.RandInt(radius + border, yClient - radius - 30 - border),
                    radius);

                // проверка наложения нового препятствия на препятствия из obstacleList
                if (!EntityFunctionTemplates.overlapped((BaseGameEntity) obstacle, obstacleList, minGapBetweenObstacles)) {
                    obstacleList.add(obstacle);
                    break;
                } else {
                    obstacle = null;
                }          
            }
        }
        return obstacleList;     
    }
    
}
