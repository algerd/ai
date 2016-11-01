
package SimpleSoccer;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import javax.swing.JMenuBar;
import java.awt.event.KeyEvent;
import common.misc.CppToJava;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Point;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Cursor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import SimpleSoccer.Script1.MyMenuBar;
import common.Time.PrecisionTimer;
import common.misc.Cgdi;
import common.misc.WindowUtils;
import common.Windows;

public class Main {

    static String applicationName = "Simple Soccer";
    //static String	g_szWindowClassName = "MyWindowClass";
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

    //used when a user clicks on a menu item to ensure the option is 'checked' correctly
    static void checkAllMenuItemsAppropriately(MyMenuBar hwnd) {
        WindowUtils.CheckMenuItemAppropriately(hwnd, Resource.IDM_SHOW_REGIONS, ParamLoader.instance.bRegions);
        WindowUtils.CheckMenuItemAppropriately(hwnd, Resource.IDM_SHOW_STATES, ParamLoader.instance.bStates);
        WindowUtils.CheckMenuItemAppropriately(hwnd, Resource.IDM_SHOW_IDS, ParamLoader.instance.bIDs);
        WindowUtils.CheckMenuItemAppropriately(hwnd, Resource.IDM_AIDS_SUPPORTSPOTS, ParamLoader.instance.bSupportSpots);
        WindowUtils.CheckMenuItemAppropriately(hwnd, Resource.ID_AIDS_SHOWTARGETS, ParamLoader.instance.bViewTargets);
        WindowUtils.CheckMenuItemAppropriately(hwnd, Resource.IDM_AIDS_HIGHLITE, ParamLoader.instance.bHighlightIfThreatened);
    }

    static void handleMenuItems(int wParam, MyMenuBar hwnd) {
        switch (wParam) {
            case Resource.ID_AIDS_NOAIDS:
                ParamLoader.instance.bStates = false;
                ParamLoader.instance.bRegions = false;
                ParamLoader.instance.bIDs = false;
                ParamLoader.instance.bSupportSpots = false;
                ParamLoader.instance.bViewTargets = false;
                ParamLoader.instance.bHighlightIfThreatened = false;
                checkAllMenuItemsAppropriately(hwnd);
                break;

            case Resource.IDM_SHOW_REGIONS:
                ParamLoader.instance.bRegions = !ParamLoader.instance.bRegions;
                checkAllMenuItemsAppropriately(hwnd);
                break;

            case Resource.IDM_SHOW_STATES:
                ParamLoader.instance.bStates = !ParamLoader.instance.bStates;
                checkAllMenuItemsAppropriately(hwnd);
                break;

            case Resource.IDM_SHOW_IDS:
                ParamLoader.instance.bIDs = !ParamLoader.instance.bIDs;
                checkAllMenuItemsAppropriately(hwnd);
                break;

            case Resource.IDM_AIDS_SUPPORTSPOTS:
                ParamLoader.instance.bSupportSpots = !ParamLoader.instance.bSupportSpots;
                checkAllMenuItemsAppropriately(hwnd);
                break;

            case Resource.ID_AIDS_SHOWTARGETS:
                ParamLoader.instance.bViewTargets = !ParamLoader.instance.bViewTargets;
                checkAllMenuItemsAppropriately(hwnd);
                break;

            case Resource.IDM_AIDS_HIGHLITE:
                ParamLoader.instance.bHighlightIfThreatened = !ParamLoader.instance.bHighlightIfThreatened;
                checkAllMenuItemsAppropriately(hwnd);
                break;
        }
    }
   
    static void createPanel() {
        panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Cgdi.gdi.StartDrawing(hdcBackBuffer);
                //fill our backbuffer with white
                Cgdi.gdi.fillRect(Color.WHITE, 0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
                soccerPitchLock.lock();
                soccerPitch.render();
                soccerPitchLock.unlock();
                Cgdi.gdi.StopDrawing(hdcBackBuffer);
                g.drawImage(buffer, 0, 0, null);
            }
        };
        panel.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        panel.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
    }

    public static void main(String[] args) {
        final WindowUtils.Window window = new WindowUtils.Window(applicationName);
        window.setIconImage(Windows.loadIcon("/SimpleSoccer/icon1.png"));
        window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        buffer = new BufferedImage(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
        hdcBackBuffer = buffer.createGraphics();
        //these hold the dimensions of the client window area
        cxClient = buffer.getWidth();
        cyClient = buffer.getHeight();
        //seed random number generator
        common.misc.Utils.setSeed(0);

        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        //Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        window.setResizable(false);

        int y = center.y - window.getHeight() / 2;
        window.setLocation(center.x - window.getWidth() / 2, y >= 0 ? y : 0);
        Script1.MyMenuBar menu = Script1.createMenu(Resource.IDR_MENU1);
        window.setJMenuBar(menu);

        soccerPitch = new SoccerPitch(cxClient, cyClient);

        checkAllMenuItemsAppropriately(menu);

        createPanel();

        window.add(panel);
        window.pack();

        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                CppToJava.keyCache.released(e);
                switch (e.getKeyChar()) {
                    case KeyEvent.VK_ESCAPE:
                        System.exit(0);                  
                        break;
                    case 'r':
                    case 'R':
                        soccerPitchLock.lock();
                        soccerPitch = null;
                        soccerPitch = new SoccerPitch(cxClient, cyClient);
                        JMenuBar bar = Script1.createMenu(Resource.IDR_MENU1);
                        window.setJMenuBar(bar);
                        bar.revalidate();
                        soccerPitchLock.unlock();                  
                        break;

                    case 'p':
                    case 'P':
                        soccerPitch.togglePause();                  
                        break;
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                CppToJava.keyCache.pressed(e);
            }
        });

        window.addComponentListener(new ComponentAdapter() {
            @Override //has the user resized the client area?
            public void componentResized(ComponentEvent e) {
                //if so we need to update our variables so that any drawing
                //we do using getXClient and getYClient is scaled accordingly
                cxClient = e.getComponent().getBounds().width;
                cyClient = e.getComponent().getBounds().height;
                //now to resize the backbuffer accordingly. 
                buffer = new BufferedImage(cxClient, cyClient, BufferedImage.TYPE_INT_RGB);
                hdcBackBuffer = buffer.createGraphics();
            }
        });

        //make the window visible
        window.setVisible(true);

        //timer.SmoothUpdatesOn();
        
        //start the timer
        timer.start();

        while (true) {
            //update
            if (timer.readyForNextFrame()) {
                soccerPitchLock.lock();
                soccerPitch.update();
                soccerPitchLock.unlock();
                //render
                //panel.revalidate();
                panel.repaint();
                try {
                    //System.out.println(timer.TimeElapsed());
                    Thread.sleep(2);
                } catch (InterruptedException ex) {
                }
            }
        }
    }
}
