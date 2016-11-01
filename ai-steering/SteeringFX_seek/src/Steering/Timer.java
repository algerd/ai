
package Steering;

import common.misc.Cgdi;
import javafx.animation.AnimationTimer;

public class Timer extends AnimationTimer {

    private Long lastNanoTime;
    private double timeElapsed;
    private double timeScale = 1.0/1000000000L;
    private Cgdi cgdi;
    private GameWorld gameWorld;

    public Timer(Cgdi cgdi, GameWorld gameWorld) {
        super();
        this.cgdi = cgdi;
        this.gameWorld = gameWorld;
    }
    
    @Override
    public void handle(long currentNanoTime) {
        cgdi.clear();
        gameWorld.render(cgdi);
        gameWorld.update(timeElapsed(currentNanoTime));
    }

    @Override
    public void start() {
        super.start();
        timeElapsed = 0.0;     
        lastNanoTime = System.nanoTime();
    }
   
    /**
     *  returns time elapsed since last call to this function.
     */
    private double timeElapsed(long currentNanoTime) {
        timeElapsed = (currentNanoTime - lastNanoTime) * timeScale;
        lastNanoTime = currentNanoTime;
        return timeElapsed;
    }
   
}
