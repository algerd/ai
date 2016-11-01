
package FSM;

public abstract class BaseGameEntity { 
      
    private EntityEnum entity;
    
    public BaseGameEntity(EntityEnum ent) {
        this.entity = ent;
    }
      
    public EntityEnum getEntity() {
        return this.entity;
    }
    
    /**
     * All entities must implement an update().
     */
    abstract public void update();
}
