
package soccer;

import common.D2.Vector;
import common.Messaging.Telegram;
import javafx.scene.canvas.GraphicsContext;

/**
 * Base class to define a common interface for all game entities.
 */
public abstract class BaseGameEntity {

    private int id;
    private static int nextId;
    public static int defaultEntityType = -1;     
    private int entityType;   
    protected Vector position = new Vector();
    protected Vector scale = new Vector();
    protected double boundingRadius;
    private boolean tag;  
    
    abstract public void render(GraphicsContext gc);
    
    protected BaseGameEntity() {
        scale = new Vector(1.0, 1.0);
        entityType = defaultEntityType;
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
    public boolean isTagged() {
        return tag;
    }

    public void tagOn() {
        tag = true;
    }

    public void tagOff() {
        tag = false;
    }
    
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

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int newType) {
        entityType = newType;
    }
    
}
