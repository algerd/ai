package Steering;

import common.D2.Vector2D;
import common.misc.Cgdi;

public class Obstacle extends BaseGameEntity {

    public Obstacle(double x, double y, double boundingRadius) {
        super(0, new Vector2D(x, y), boundingRadius);
    }

    public Obstacle(Vector2D pos, double boundingRadius) {
        super(0, pos, boundingRadius);
    }
    
    public void render(Cgdi cgdi) {
        double x = getPos().x;
        double y = getPos().y;
        double radius = getBoundingRadius();
        cgdi.drawCircle(x, y, radius);
    }

}
