
package common.Game;

import SimpleSoccer.BaseGameEntity;
import java.util.HashMap;

public class EntityManager {
    
    private class EntityMap extends HashMap<Integer, BaseGameEntity> {}

    public static final EntityManager EntityMgr = new EntityManager();
    //to facilitate quick lookup the entities are stored in a std::map, in which
    //pointers to entities are cross referenced by their identifying number
    private EntityMap entityMap = new EntityMap();    

    private EntityManager() {}

    public static EntityManager getInstance() {
        return EntityMgr;
    }

    /**
     * this method stores a pointer to the entity in the std::vector
     * m_Entities at the index position indicated by the entity's ID
     * (makes for faster access)
     */
    public void registerEntity(BaseGameEntity NewEntity) {
        entityMap.put(NewEntity.getId(), NewEntity);
    }

    /**
     * return a pointer to the entity with the ID given as a parameter.
     */
    public BaseGameEntity getEntityFromID(int id) {
        //find the entity
        BaseGameEntity ent = entityMap.get(id);
        return ent;
    }

    /**
     * this method removes the entity from the list.
     */
    public void removeEntity(BaseGameEntity pEntity) {
        entityMap.remove(pEntity.getId());
    }

    /**
     * clears all entities from the entity map.
     */
    public void reset() {
        entityMap.clear();
    }
}
