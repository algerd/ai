
package common.misc;

public class FrameCounter {

    public static final FrameCounter tickCounter = new FrameCounter();
    private long counter = 0;
    private int framesElapsed = 0;

    private FrameCounter() {}

    public FrameCounter getInstance() {
        return tickCounter;
    }

    public void update() {
        ++counter;
        ++framesElapsed;
    }

    public long getCurrentFrame() {
        return counter;
    }

    public void reset() {
        counter = 0;
    }

    public void start() {
        framesElapsed = 0;
    }

    public int framesElapsedSinceStartCalled() {
        return framesElapsed;
    }
}