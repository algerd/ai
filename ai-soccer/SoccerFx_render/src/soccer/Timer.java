
package soccer;

import render.Render;
import javafx.animation.AnimationTimer;

public class Timer extends AnimationTimer {
    
    private Render render;
         
    public Timer(Render render) {
        super();
        this.render = render; 
    }
    
    @Override
    public void handle(long currentNanoTime) {         
        if (render.tact > Generator.countTact - 2) {
            stop();
        }
        render.update();
    }

    @Override
    public void start() {
        super.start();        
    }
      
}
