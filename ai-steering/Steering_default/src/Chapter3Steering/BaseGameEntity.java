/**
 * Base class for a game object
 */
package Chapter3Steering;

import java.io.InputStream;
import common.D2.Vector2D;
import common.Messaging.Telegram;
import common.misc.Utils;

public abstract class BaseGameEntity {

    public static int default_entity_type = -1;
    //each entity has a unique ID
    private int id = 0;
    //every entity has a type associated with it (health, troll, ammo etc)
    int entityType;
    //this is a generic flag. 
    boolean tag;
    //this is the next valid ID. Each time a BaseGameEntity is instantiated this value is updated
    static private int nextId = 0;
    //its location in the environment
    protected Vector2D pos;
    protected Vector2D scale;
    //the length of this object's bounding radius
    protected double boundingRadius;

    protected BaseGameEntity() {
        id = nextValidID();
        pos = new Vector2D();
        scale = new Vector2D(1.0, 1.0);
        entityType = default_entity_type;
    }

    protected BaseGameEntity(int entity_type) {
        id = nextValidID();
        pos = new Vector2D();
        scale = new Vector2D(1.0, 1.0);
        entityType = entity_type;
    }

    protected BaseGameEntity(int entity_type, Vector2D pos, double r) {
        this.pos = pos;
        boundingRadius = r;
        id = nextValidID();
        scale = new Vector2D(1.0, 1.0);
        entityType = entity_type;
    }

    /**
     * this can be used to create an entity with a 'forced' ID. It can be used
     * when a previously created entity has been removed and deleted from the
     * game for some reason. For example, The Raven map editor uses this ctor 
     * in its undo/redo operations. 
     * USE WITH CAUTION!
     */
    protected BaseGameEntity(int entity_type, int ForcedID) {
        id = ForcedID;
        boundingRadius = 0.0;
        pos = new Vector2D();
        scale = new Vector2D(1.0, 1.0);
        entityType = entity_type;
        tag = false;
    }
       
    //used by the constructor to give each entity a unique ID
    private int nextValidID() {
        return nextId++;
    }

    //Use with recreating GameWord
    public static void resetValidID() {
        nextId = 0;
    }
   
    public void update(double time_elapsed) {}

    public void render() {}

    public boolean handleMessage(Telegram msg) {
        return false;
    }

    //entities should be able to read/write their data to a stream virtual void Write(std::ostream&  os)const{}
    @Override
    public String toString() {
        return String.format("%f %f", this.pos.x, this.pos.y);
    }

    //virtual void Read (std::ifstream& is){}
    public void read(InputStream in) {}

    public Vector2D getPos() {
        return new Vector2D(pos);
    }

    void setPos(Vector2D new_pos) {
        pos = new Vector2D(new_pos);
    }

    double getBoundingRadius() {
        return boundingRadius;
    }

    void setBoundingRadius(double r) {
        boundingRadius = r;
    }

    public int getId() {
        return id;
    }

    boolean isTagged() {
        return tag;
    }

    void setTagTrue() {
        tag = true;
    }

    void setTagFalse() {
        tag = false;
    }

    Vector2D getScale() {
        return new Vector2D(scale);
    }

    void setScale(Vector2D val) {
        boundingRadius *= Utils.MaxOf(val.x, val.y) / Utils.MaxOf(scale.x, scale.y);
        scale = new Vector2D(val);
    }

    void setScale(double val) {
        boundingRadius *= (val / Utils.MaxOf(scale.x, scale.y));
        scale = new Vector2D(val, val);
    }

    int getEntityType() {
        return entityType;
    }

    void setEntityType(int newType) {
        entityType = newType;
    }
}
