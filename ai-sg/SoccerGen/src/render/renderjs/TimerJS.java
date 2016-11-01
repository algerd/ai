
package render.renderjs;

import generator.Generator;
import javafx.animation.AnimationTimer;

public class TimerJS extends AnimationTimer {
    
    private RenderJS render;
         
    public TimerJS(RenderJS render) {
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
