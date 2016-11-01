/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpleSoccer;

import static SimpleSoccer.Main.buffer;
import static SimpleSoccer.Main.checkAllMenuItemsAppropriately;
import static SimpleSoccer.Main.createPanel;
import static SimpleSoccer.Main.soccerPitch;
import static SimpleSoccer.Main.hdcBackBuffer;
import static SimpleSoccer.Main.soccerPitchLock;
import static SimpleSoccer.Main.timer;
import static SimpleSoccer.Constants.WINDOW_HEIGHT;
import static SimpleSoccer.Constants.WINDOW_WIDTH;
import static SimpleSoccer.Resource.IDR_MENU1;
import java.awt.image.BufferedImage;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JApplet;

/**
 *
 * @author Petr
 */
public class Applet extends JApplet {

    private int cxClient;
    private int cyClient;
    Thread thread;
    final private Lock threadLock = new ReentrantLock();

    /**
     * Initialization method that will be called after the applet is loaded into
     * the browser.
     */
    public void init() {
        buffer = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
        hdcBackBuffer = buffer.createGraphics();
        //these hold the dimensions of the client window area
        cxClient = buffer.getWidth();
        cyClient = buffer.getHeight();
        //seed random number generator
        common.misc.Utils.setSeed(0);
        Script1.MyMenuBar menu = Script1.createMenu(IDR_MENU1);
        this.setJMenuBar(menu);
        soccerPitch = new SoccerPitch(cxClient, cyClient);

        checkAllMenuItemsAppropriately(menu);

        createPanel();

        this.add(Main.panel);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                runThread();
            }
        };
        thread = new Thread(r);
        thread.start();
        threadLock.lock();
    }

    @Override
    public void start() {
        timer.timeElapsed();
        threadLock.unlock();
    }

    @Override
    public void stop() {
        threadLock.lock();
    }

    private void runThread() {

        timer.start();

        while (true) {
            //update
            if (timer.readyForNextFrame()) {
                soccerPitchLock.lock();
                soccerPitch.update();
                soccerPitchLock.unlock();
                //render
                //panel.revalidate();
                Main.panel.repaint();

                try {
                    //System.out.println(timer.TimeElapsed());
                    Thread.sleep(2);
                } catch (InterruptedException ex) {
                }
            }
        }//end while
    }
}
