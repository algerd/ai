
package common.Game;

import soccer.BaseGameEntity;
import common.D2.Vector;
import common.D2.Geometry;
import java.util.List;
import java.util.ListIterator;

public class EntityFunctionTemplates {
    /**
     *  tests to see if an entity is overlapping any of a number of entities stored in a std container.
     */
    public static <T extends BaseGameEntity, conT extends List<? extends BaseGameEntity>>
            boolean overlapped(final T ob, final conT conOb) 
    {
        return overlapped(ob, conOb, 40.0);
    }

    public static <T extends BaseGameEntity, conT extends List<? extends BaseGameEntity>>
            boolean overlapped(final T ob, final conT conOb, double MinDistBetweenObstacles) 
    {               
        ListIterator<? extends BaseGameEntity> it = conOb.listIterator();
        while (it.hasNext()) {
            BaseGameEntity curOb = it.next();
            if (Geometry.twoCirclesOverlapped(ob.getPosition(),
                    ob.getBoundingRadius() + MinDistBetweenObstacles,
                    curOb.getPosition(),
                    curOb.getBoundingRadius())) {
                return true;
            }
        }
        return false;
    }

    /**
     * tags any entities contained in a std container that are within the
     * radius of the single entity parameter
     */
    public static <T extends BaseGameEntity, conT extends List<? extends T>> 
            void tagNeighbors(T entity, conT others, final double radius) 
    {               
        ListIterator<? extends T> it = others.listIterator();
        //iterate through all entities checking for range
        while (it.hasNext()) {
            T curOb = it.next();
            //first clear any current tag
            curOb.tagOff();
            //work in dist squared to avoid sqrts
            Vector to = curOb.getPosition().subn(entity.getPosition());

            //the bounding radius of the other is taken into account by adding it to the range
            double range = radius + curOb.getBoundingRadius();
            //if entity within range, tag for further consideration
            if ((curOb != entity) && (to.lengthSq() < range * range)) {
                curOb.tagOn();
            }
        }
    }

    /**
     * Given a pointer to an entity and a std container of pointers to nearby
     * entities, this function checks to see if there is an overlap between
     * entities. If there is, then the entities are moved away from each other
     */
    public static <T extends BaseGameEntity, conT extends List<? extends BaseGameEntity>> 
            void enforceNonPenetrationConstraint(T entity, final conT others) 
    {
        ListIterator<? extends BaseGameEntity> it = others.listIterator();
        //iterate through all entities checking for any overlap of bounding radii
        while (it.hasNext()) {
            BaseGameEntity curOb = it.next();
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
            
}