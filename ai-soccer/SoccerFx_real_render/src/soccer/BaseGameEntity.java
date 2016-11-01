
package soccer;

import common.Messaging.Telegram;

/**
 * Base class to define a abstraction for all game entities.
 */
public abstract class BaseGameEntity {

    private int id;
    private static int nextId;     
    
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
