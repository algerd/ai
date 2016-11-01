
package FSM;

import java.util.HashMap;
import java.util.Map;

public class EntityManager {
    
    private Map<EntityEnum, BaseGameEntity> entityMap = new HashMap<>();
    private static EntityManager instance = new EntityManager();
    
    private EntityManager() {}

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not allowed");
    }

    public static EntityManager getInstance() {
        return instance;
    }

    public void registerEntity(BaseGameEntity entity) {
        this.entityMap.put(entity.getEntity(), entity);
    }

    public BaseGameEntity getEntity(EntityEnum entity) {
        return this.entityMap.get(entity);
    }

    public void removeEntity(EntityEnum entity) {
        this.entityMap.remove(entity);
    }
}
