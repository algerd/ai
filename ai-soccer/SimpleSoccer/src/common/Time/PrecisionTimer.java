/**
 *
 *  Desc: Windows timer class.
 *
 *        nb. this only uses the high performance timer. There is no
 *        support for ancient computers. I know, I know, I should add
 *        support, but hey, I have shares in AMD and Intel... Go upgrade ;o)
 */
package common.Time;

public class PrecisionTimer {

    private Long currentTime,
            lastTime,
            lastTimeInTimeElapsed,
            nextTime,
            startTime,
            frameTime,
            perfCountFreq;
    private double timeElapsed,
            lastTimeElapsed,
            timeScale;
    private double normalFPS;
    private double slowFPS;
    private boolean started;
    //if true a call to TimeElapsed() will return 0 if the current
    //time elapsed is much smaller than the previous. Used to counter
    //the problems associated with the user using menus/resizing/moving a window etc
    private boolean smoothUpdates;

    /**
     * default constructor.
     */
    public PrecisionTimer() {
        slowFPS = 1.0;
        //how many ticks per sec do we get
        //QueryPerformanceFrequency((LARGE_INTEGER *) & m_PerfCountFreq);
        //using System.nanoSecond() it is obviously 1 000 000 000 nano second per second
        perfCountFreq = 1000000000L;
        timeScale = 1.0 / perfCountFreq;
    }

    /**
    /* use to specify FPS.
     */
    public PrecisionTimer(double fps) {
        normalFPS = fps;
        slowFPS = 1.0;       
        //how many ticks per sec do we get
        //QueryPerformanceFrequency((LARGE_INTEGER *) & m_PerfCountFreq);
        //using System.nanoSecond() it is obviously 1 000 000 000 nano second per second
        perfCountFreq = 1000000000L;
        timeScale = 1.0 / perfCountFreq;
        //calculate ticks per frame
        frameTime = (long)(perfCountFreq / normalFPS);
    }

    /**
     *  whatdayaknow, this starts the timer call this immediately prior to game loop. Starts the timer (obviously!)
     */
    public void start() {
        started = true;
        timeElapsed = 0.0;
        //get the time
        //QueryPerformanceCounter((LARGE_INTEGER *) & m_LastTime);
        lastTime = System.nanoTime();
        //keep a record of when the timer was started
        startTime = lastTimeInTimeElapsed = lastTime;
        //update time to render next frame
        nextTime = lastTime + frameTime;
        return;
    }

    //determines if enough time has passed to move onto next frame
    //public boolean    ReadyForNextFrame();
    //only use this after a call to the above.
    //double  GetTimeElapsed(){return m_TimeElapsed;}
    //public double  TimeElapsed();
    public double currentTime() {
        //QueryPerformanceCounter((LARGE_INTEGER *) & m_CurrentTime);
        currentTime = System.nanoTime();
        return (currentTime - startTime) * timeScale;
    }

    public boolean started() {
        return started;
    }

    public void smoothUpdatesOn() {
        smoothUpdates = true;
    }

    public void smoothUpdatesOff() {
        smoothUpdates = false;
    }

    /**
     *  returns true if it is time to move on to the next frame step. To be used if FPS is set.
     */
    public boolean readyForNextFrame() {     
        //QueryPerformanceCounter((LARGE_INTEGER *) & m_CurrentTime);
        currentTime = System.nanoTime();
        if (currentTime > nextTime) {
            timeElapsed = (currentTime - lastTime) * timeScale;
            lastTime = currentTime;
            //update time to render next frame
            nextTime = currentTime + frameTime;
            return true;
        }
        return false;
    }

    /**
     *  returns time elapsed since last call to this function.
     */
    public double timeElapsed() {
        lastTimeElapsed = timeElapsed;

        //QueryPerformanceCounter((LARGE_INTEGER *) & m_CurrentTime);
        currentTime = System.nanoTime();
        timeElapsed = (currentTime - lastTimeInTimeElapsed) * timeScale;
        lastTimeInTimeElapsed = currentTime;
        final double Smoothness = 5.0;

        if (smoothUpdates) {
            if (timeElapsed < (lastTimeElapsed * Smoothness)) {
                return timeElapsed;
            } else {
                return 0.0;
            }
        } else {
            return timeElapsed;
        }
    }
    
}