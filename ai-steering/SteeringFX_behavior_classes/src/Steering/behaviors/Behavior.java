
package Steering.behaviors;

import common.D2.Vector2D;

public abstract class Behavior {
    
    private boolean on;
    
    public boolean isOn() {
        return on;
    }
    
    public void on() {
        this.on = true;
    }
    
    public void off() {
       this.on = false;
    }
    
    public abstract Vector2D getWeightForce();
    public abstract Vector2D getDitheredForce();
    public abstract Vector2D getForce();
    
}
