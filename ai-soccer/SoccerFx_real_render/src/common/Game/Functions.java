
package common.Game;

import common.D2.Vector;
import java.util.List;
import java.util.ListIterator;
import soccer.MovingEntity;

public class Functions {
    /**
     * Given a pointer to an entity and a std container of pointers to nearby
     * entities, this function checks to see if there is an overlap between
     * entities. If there is, then the entities are moved away from each other
     */
    public static <T extends MovingEntity, conT extends List<? extends MovingEntity>> 
            void enforceNonPenetrationConstraint(T entity, final conT others) 
    {
        ListIterator<? extends MovingEntity> it = others.listIterator();
        //iterate through all entities checking for any overlap of bounding radii
        while (it.hasNext()) {
            MovingEntity curOb = it.next();
            //make sure we don't check against this entity
            if (curOb == entity) {
                continue;
            }
            //calculate the dist between the positions of the entities
            Vector toEntity = entity.getPosition().subn(curOb.getPosition());
            double distFromEachOther = toEntity.length();

            //if this dist is smaller than the sum of their radii then this
            //entity must be moved away in the direction parallel to the ToEntity vector   
            double amountOfOverLap = curOb.getBoundingRadius() + entity.getBoundingRadius() - distFromEachOther;

            if (amountOfOverLap >= 0) {
                //move the entity a dist away equivalent to the amount of overlap.
                entity.setPosition(toEntity.divn(distFromEachOther).mul(amountOfOverLap).add(entity.getPosition()));
            }
        }
    }
               
    static public boolean isEqual(double a, double b) {
        return Math.abs(a - b) < 1E-12;
    }
            
}
