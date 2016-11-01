
package soccer;

import common.D2.Vector;
import common.Messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;

/**
 * Base class to define a abstraction for all game entities.
 */
public abstract class BaseGameEntity {

    private int id;
    private static int nextId;     
    protected Vector position = new Vector();
    protected double boundingRadius;
    
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

    /**
     ************************ Getters & Setters. *****************************  
     */  
    public Vector getPosition() {
        return new Vector(position);
    }

    public void setPosition(Vector newPos) {
        position = new Vector(newPos);
    }

    public double getBoundingRadius() {
        return boundingRadius;
    }

    public int getId() {
        return id;
    }

}
