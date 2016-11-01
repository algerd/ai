/**
 *  Desc:   Use this class to regulate code flow (for an update function say)
 *          Instantiate the class with the frequency you would like your code
 *          section to flow (like 10 times per second) and then only allow 
 *          the program flow to continue if Ready() returns true.
 */
package common.Time;

import common.misc.Utils;

public class Regulator {

    //the time period between updates 
    private double updatePeriod;
    //the next time the regulator allows code flow
    private long nextUpdateTime;
    //the number of milliseconds the update period can vary per required
    //update-step. This is here to make sure any multiple clients of this class
    //have their updates spread evenly
    private static final double UpdatePeriodVariator = 10.0;

    public Regulator(double NumUpdatesPerSecondRqd) {
        nextUpdateTime = (long)(System.currentTimeMillis() + Utils.randFloat() * 1000);

        if (NumUpdatesPerSecondRqd > 0) {
            updatePeriod = 1000.0 / NumUpdatesPerSecondRqd;
        } else if (Utils.isEqual(0.0, NumUpdatesPerSecondRqd)) {
            updatePeriod = 0.0;
        } else if (NumUpdatesPerSecondRqd < 0) {
            updatePeriod = -1;
        }
    }
    
    /**
     * @return true if the current time exceeds m_dwNextUpdateTime
     */
    public boolean isReady() {
        //if a regulator is instantiated with a zero freq then it goes into stealth mode (doesn't regulate)
        if (Utils.isEqual(0.0, updatePeriod)) {
            return true;
        }
        //if the regulator is instantiated with a negative freq then it will never allow the code to flow
        if (updatePeriod < 0) {
            return false;
        }
        long CurrentTime = System.currentTimeMillis();
        if (CurrentTime >= nextUpdateTime) {
            nextUpdateTime = (long) (CurrentTime + updatePeriod + Utils.randInRange(-UpdatePeriodVariator, UpdatePeriodVariator));
            return true;
        }
        return false;
    }
    
}
