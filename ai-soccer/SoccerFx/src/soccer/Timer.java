
package soccer;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Timer extends AnimationTimer {
    
    private GraphicsContext gc;
    private SoccerPitch soccerPitch;
         
    public Timer(Canvas canvas, SoccerPitch soccerPitch) {
        super();
        this.gc = canvas.getGraphicsContext2D();
        this.soccerPitch = soccerPitch;           
    }
    
    @Override
    public void handle(long currentNanoTime) {         
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        soccerPitch.render(gc);
        soccerPitch.update();          
    }

    @Override
    public void start() {
        super.start();        
    }
      
}
