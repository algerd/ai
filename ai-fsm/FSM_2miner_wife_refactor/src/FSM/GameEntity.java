
package FSM;

public abstract class GameEntity { 
      
    private EntityEnum entity;
    
    public GameEntity(EntityEnum ent) {
        this.entity = ent;
    }
      
    public EntityEnum getEntity() {
        return this.entity;
    }
   
}
