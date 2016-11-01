
package common.Time;

public class PrecisionTimer {

    private Long currentTime;
    private Long lastTime;
    private double timeElapsed;
    private double timeScale;

    public PrecisionTimer() {
        timeScale = 1.0/1000000000L;
    }

    /**
     *  this starts the timer call this immediately prior to game loop. Starts the timer (obviously!)
     */
    public void start() {
        timeElapsed = 0.0;     
        lastTime = System.nanoTime();
    }
   
    /**
     *  returns time elapsed since last call to this function.
     */
    public double timeElapsed() {
        currentTime = System.nanoTime();
        timeElapsed = (currentTime - lastTime) * timeScale;
        lastTime = currentTime;
        return timeElapsed;
    }
 
}
