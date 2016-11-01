
package soccer;

import common.Messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;

/**
 * Base class to define a abstraction for all game entities.
 */
public abstract class BaseGameEntity {

    private int id;
    private static int nextId;     
    
    abstract public void render(GraphicsContext gc);
    
    protected BaseGameEntity() {
        id = nextValidID();
    }
       
    //Use with recreating GameWord
    public static void resetValidID() {
        nextId = 0;
    }
    
    //used by the constructor to give each entity a unique ID
    private int nextValidID() {
        return nextId++;
    }
      
    public void update() {}
   
    public boolean handleMessage(Telegram msg) {
        return false;
    }
    
    public int getId() {
        return id;
    }

}
