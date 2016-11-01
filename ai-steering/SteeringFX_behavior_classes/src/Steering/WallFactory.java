
package Steering;

import common.D2.Vector2D;
import java.util.ArrayList;
import java.util.List;

public class WallFactory {
    
    public List<Wall> create(int xClient, int yClient) {
        List<Wall> wallList = new ArrayList<>();
              
        double borderSize = 20.0;
        double cornerSize = 0.2;
        double vDist = yClient - 2 * borderSize;
        double hDist = xClient - 2 * borderSize;

        Vector2D wall[] = {
            new Vector2D(hDist * cornerSize + borderSize, borderSize),
            new Vector2D(xClient - borderSize - hDist * cornerSize, borderSize),
            new Vector2D(xClient - borderSize, borderSize + vDist * cornerSize),
            new Vector2D(xClient - borderSize, yClient - borderSize - vDist * cornerSize),
            new Vector2D(xClient - borderSize - hDist * cornerSize, yClient - borderSize),
            new Vector2D(hDist * cornerSize + borderSize, yClient - borderSize),
            new Vector2D(borderSize, yClient - borderSize - vDist * cornerSize),
            new Vector2D(borderSize, borderSize + vDist * cornerSize)
        };

        int numWallVerts = wall.length;
        for (int w = 0; w < numWallVerts - 1; ++w) {
            wallList.add(new Wall(wall[w], wall[w + 1]));
        }
        wallList.add(new Wall(wall[numWallVerts - 1], wall[0]));
        
        return wallList;
    }
    
}
