
package SimpleSoccer;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Point;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import common.Time.PrecisionTimer;
import common.misc.Cgdi;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.JDialog;

public class Main {

    final public static int WINDOW_WIDTH  = 700;
    final public static int WINDOW_HEIGHT = 400;    
    static String applicationName = "Simple Soccer";
    static SoccerPitch soccerPitch;
    // bacause of game restart (soccerPitch could be null for a while)
    static Lock soccerPitchLock = new ReentrantLock();
    //create a timer
    static PrecisionTimer timer = new PrecisionTimer(ParamLoader.instance.FrameRate);
    static BufferedImage buffer;
    static Graphics2D hdcBackBuffer;
    //these hold the dimensions of the client window area
    static int cxClient;
    static int cyClient;
    static JPanel panel;
    
    public static void main(String[] args) {
        final JDialog window = new JDialog((JFrame) null, applicationName);
        window.setIconImage(loadIcon("/SimpleSoccer/icon1.png"));
        buffer = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
        hdcBackBuffer = buffer.createGraphics();
        //these hold the dimensions of the client window area
        cxClient = buffer.getWidth();
        cyClient = buffer.getHeight();
        //seed random number generator
        common.misc.Utils.setSeed(0);

        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        
        window.setResizable(false);
        int y = center.y - window.getHeight() / 2;
        window.setLocation(center.x - window.getWidth() / 2, y >= 0 ? y : 0);
        

        soccerPitch = new SoccerPitch(cxClient, cyClient);     
        createPanel();

        window.add(panel);
        window.pack();

        //make the window visible
        window.setVisible(true);

        //timer.SmoothUpdatesOn();       
        timer.start();

        while (true) {
            if (timer.readyForNextFrame()) {
                soccerPitch.update();           
                panel.repaint();
                try {
                    Thread.sleep(2);
                } 
                catch (InterruptedException ex) {}
            }
        }
    }

    static void createPanel() {
        panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Cgdi.gdi.StartDrawing(hdcBackBuffer);
                //fill our backbuffer with white
                Cgdi.gdi.fillRect(Color.WHITE, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
                soccerPitch.render();
                Cgdi.gdi.StopDrawing(hdcBackBuffer);
                g.drawImage(buffer, 0, 0, null);
            }
        };
        panel.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        panel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
    }
    
    static Image loadIcon(String file) {
        URL icoURL = Main.class.getResource(file);
        Image img = Toolkit.getDefaultToolkit().createImage(icoURL);
        return img;
    }
   
}
