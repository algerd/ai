
package FSM.Utils;

public class Timer {

    final private static Timer instance = new Timer();
    private double startTime;

    private Timer() {
        startTime = System.currentTimeMillis() * 0.001;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not allowed");
    }

    public static Timer getInstance() {
        return instance;
    }

    /**
     * returns how much time has elapsed since the timer was started.
     */
    public double getCurrentTime() {
        return ((double) (Math.round((System.currentTimeMillis() * 0.001 - startTime) * 1000))) / 1000;
    }
}
