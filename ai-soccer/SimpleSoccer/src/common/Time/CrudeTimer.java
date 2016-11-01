
package common.Time;

public class CrudeTimer {

    final public static CrudeTimer clock = new CrudeTimer();
    //set to the time (in seconds) when class is instantiated
    private double startTime;

    private CrudeTimer() {
        startTime = System.currentTimeMillis() * 0.001;
    }

    public CrudeTimer getInstance() {
        return clock;
    }

    //returns how much time has elapsed since the timer was started
    public double getCurrentTime() {
        //return System.currentTimeMillis() * 0.001 - m_dStartTime;
        // The truncation of the results was added to produce results similar to what is produced by the C++ code
        // Improved by A.Rick Anderson
        return ((double) (Math.round((System.currentTimeMillis() * 0.001 - startTime) * 1000))) / 1000;
    }
}
