/**
 * Desc:   Creates a resizable console window for recording and displaying
 *         debug info.
 *
 *         use the debug_con macro to send text and types to the console
 *         window via the print method. Flush the
 *         buffer using "" or the flush macro.  eg. 
 *
 *        debug_con.print("Hello World!").print("");
 */
package common.Debug;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.util.LinkedList;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.swing.JFrame;
import java.io.PrintStream;
import java.util.ListIterator;
import javax.swing.JOptionPane;
import SimpleSoccer.Define;
import common.misc.JConsole;

public class DbgConsole implements DebugConsole {

    private static JConsole hwnd;
    //the string buffer. All input to debug stream is stored here
    private static LinkedList<String> buffer = new LinkedList<String>();
    //maximum number of lines shown in console before the buffer is flushed to a file
    public final int MaxBufferSize = 500;
    //initial dimensions of the console window
    public final static int DEBUG_WINDOW_WIDTH = 400;
    public final static int DEBUG_WINDOW_HEIGHT = 400;
    public static DebugConsole debugConsole;
    //if true the next input will be pushed into the buffer. If false, it will be appended.
    private static boolean flushed = true;
    //set to true if the window is destroyed
    private static boolean destroyed = false;
    //if false the console will just disregard any input
    private static boolean active = true;
    //default logging file
    private static PrintStream logOut;
    private static DbgConsole instance;
    private static boolean created = false;

    static {
        //undefine DEBUG to send all debug messages to hyperspace (a sink - see below)
        //define(DEBUG);
        if (Define.def(Define.DEBUG)) {
            debugConsole = DbgConsole.getInstance();
        } else {
            debugConsole = CSink.getInstance();
        }
    }
    
    private DbgConsole() {}

    /**    
     * Retrieve a pointer to an instance of this class.
     */
    public static DbgConsole getInstance() {
        if (!created) {
            create();
            created = true;
        }
        return instance;
    }
    
    /**
     * this little class just acts as a sink for any input. Used in place
     * of the DebugConsole class when the console is not required
     */
    static class CSink implements DebugConsole {
        
        private static final CSink instance = new CSink();
        
        private CSink() {}

        public static CSink getInstance() {
            return instance;
        }

        @Override
        public CSink print(final Object T) {
            return this;
        }
    }
    
    /**
     * this registers the window class and creates the window(called by the ctor)
     */
    private static boolean create() {
        instance = new DbgConsole();
        hwnd = null;
        flushed = true;
        try {
            //open log file
            logOut = new PrintStream(new FileOutputStream("DebugLog.txt"));
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Create the info window
        hwnd = new JConsole("Debug Console");
        hwnd.setPreferredSize(new Dimension(DEBUG_WINDOW_WIDTH, DEBUG_WINDOW_HEIGHT));
        
        hwnd.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    hwnd.setVisible(false);
                }
            }
        });
        hwnd.setBGColor(Color.DARK_GRAY);
        hwnd.setColor(Color.WHITE);
        // Show the window
        hwnd.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        hwnd.setVisible(true);
        updateWindow(hwnd);
        return true;
    }
    
    //use these in your code to toggle output to the console on/off
    public static void debugOn() {
        DbgConsole.On();
    }

    public static void debugOff() {
        DbgConsole.Off();
    }

    private static void updateWindow(JFrame frame) {
        frame.repaint();
    }

    public void clearBuffer() {
        buffer.clear();
        flush();
    }

    public static void flush() {
        if (!destroyed) {
            ListIterator<String> it = buffer.listIterator();
            while (it.hasNext()) {
                hwnd.addString(it.next());
            }
            buffer.clear();
            flushed = true;
        }
    }
    
    /**
     * writes the contents of the buffer to the file "debugLog.txt", clears
     * the buffer and resets the appropriate scroll info
     */
    public void writeAndResetBuffer() {
        flushed = true;
        //write out the contents of the buffer to a file
        ListIterator<String> it = buffer.listIterator();
        while (it.hasNext()) {
            logOut.println(it.next());
        }
        buffer.clear();
        //m_hwnd.clearText();
    }

    //use to activate deactivate
    public static void Off() {
        active = false;
    }

    public static void On() {
        active = true;
    }

    public boolean Destroyed() {
        return destroyed;
    }

    @Override
    public DbgConsole print(final Object t) {
        if (!active || destroyed) {
            return this;
        }
        //reset buffer and scroll info if it overflows. Write the excess to file
        if (buffer.size() > MaxBufferSize) {
            writeAndResetBuffer();
        }
        if (t.toString().equals("")) {
            buffer.add("\n");
            flush();
            return this;
        }        
       buffer.add(t.toString());        
        return this;
    }
    
}
