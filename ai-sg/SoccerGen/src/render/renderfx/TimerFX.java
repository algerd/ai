
package render.renderfx;

import javafx.animation.AnimationTimer;

public class TimerFX extends AnimationTimer {
    
    private RenderFX render;
         
    public TimerFX(RenderFX render) {
        super();
        this.render = render; 
    }
    
    @Override
    public void handle(long currentNanoTime) {         
        render.update();
    }

    @Override
    public void start() {
        super.start();        
    }
      
}
