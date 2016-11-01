
package Chapter3Steering;

import java.awt.Graphics2D;
import common.Time.PrecisionTimer;
import java.awt.Color;
import common.misc.Cgdi;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JDialog;

public class Main {

    final public static int WINDOW_WIDTH  = 600;
    final public static int WINDOW_HEIGHT = 500;    
    static GameWorld gameWorld;

    public static void main(String[] args) {
        
        final JDialog window = new JDialog((JFrame) null, "Steering Behaviors - Another Big Shoal");
        final BufferedImage buffer = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
        final Graphics2D hdcBackBuffer = buffer.createGraphics();
        //these hold the dimensions of the client window area
        final int cxClient = buffer.getWidth();
        final int cyClient = buffer.getHeight();
        //seed random number generator
        common.misc.Utils.setSeed(0);

        window.setResizable(false);

        gameWorld = new GameWorld(cxClient, cyClient);

        final JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Cgdi.gdi.StartDrawing(hdcBackBuffer);
                Cgdi.gdi.fillRect(Color.WHITE, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
                gameWorld.render();
                g.drawImage(buffer, 0, 0, null);
            }
        };
        panel.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        panel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        window.add(panel);
        window.pack();

        //make the window visible
        window.setVisible(true);

        //create a timer
        final PrecisionTimer timer = new PrecisionTimer();

        //start the timer
        timer.start();

        while (true) {
            //update
            gameWorld.update(timer.timeElapsed());
            panel.repaint();
        }
    }
        
}
