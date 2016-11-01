/**
 * Base class for a game object
 */
package Steering;

import common.D2.Vector2D;
import common.misc.Utils;

public abstract class BaseGameEntity {

    //each entity has a unique ID
    private int id = 0;
    //every entity has a type associated with it (health, troll, ammo etc), default entityType = -1 
    int entityType = -1;
    //this is a generic flag. 
    boolean tag;
    //this is the next valid ID. Each time a BaseGameEntity is instantiated this value is updated
    static private int nextId = 0;
    //its location in the environment
    protected Vector2D pos;
    protected Vector2D scale = new Vector2D(1.0, 1.0);
    //the length of this object's bounding radius
    protected double boundingRadius;
   
    protected BaseGameEntity() {
        id = nextValidID();
        pos = new Vector2D();
    }

    protected BaseGameEntity(int entity_type, Vector2D pos, double boundingRadius) {
        id = nextValidID();
        this.pos = pos;
        this.boundingRadius = boundingRadius;      
        entityType = entity_type;
    }
    
    //Use with recreating GameWord
    public static void resetValidID() {
        nextId = 0;
    }
    
    //used by the constructor to give each entity a unique ID
    private int nextValidID() {
        return nextId++;
    }
    
    public Vector2D getPos() {
        return new Vector2D(pos);
    }

    public void setPos(Vector2D new_pos) {
        pos = new Vector2D(new_pos);
    }

    public double getBoundingRadius() {
        return boundingRadius;
    }

    public void setBoundingRadius(double r) {
        boundingRadius = r;
    }

    public int getId() {
        return id;
    }

    public boolean isTagged() {
        return tag;
    }
    
    public void setTagTrue() {
        tag = true;
    }

    public void setTagFalse() {
        tag = false;
    }

    public Vector2D getScale() {
        return new Vector2D(scale);
    }

    public void setScale(Vector2D val) {
        boundingRadius *= Utils.MaxOf(val.x, val.y) / Utils.MaxOf(scale.x, scale.y);
        scale = new Vector2D(val);
    }

    public void setScale(double val) {
        boundingRadius *= (val / Utils.MaxOf(scale.x, scale.y));
        scale = new Vector2D(val, val);
    }

}