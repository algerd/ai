
package Steering;

import java.util.LinkedList;
import common.D2.Vector2D;
import java.util.List;
import java.util.ListIterator;
import common.D2.Geometry;
import common.misc.Utils;

public class EntityFunctionTemplates {

    /**
     * tests to see if an entity is overlapping any of a number of entities stored in a std container
     */
    public static <T extends BaseGameEntity, conT extends List<? extends T>> boolean overlapped(T ob, conT conOb) {
        return overlapped(ob, conOb, 40.0);
    }

    public static <T extends BaseGameEntity, conT extends List<? extends T>> boolean overlapped(T ob, conT conOb, double MinDistBetweenObstacles) {
        ListIterator<? extends T> it = conOb.listIterator();
        while (it.hasNext()) {
            T tmp = it.next();
            if (Geometry.twoCirclesOverlapped(
                    ob.getPos(),
                    ob.getBoundingRadius() + MinDistBetweenObstacles,
                    tmp.getPos(),
                    tmp.getBoundingRadius())) {
                return true;
            }
        }
        return false;
    }

    /**
     *  tags any entities contained in a std container that are within the radius of the single entity parameter
     */
    public static <T extends BaseGameEntity, conT extends List<? extends T>> void tagNeighbors
        (final T entity, conT ContainerOfEntities, double radius) 
    {
        //iterate through all entities checking for range
        ListIterator<? extends T> it = ContainerOfEntities.listIterator();
        while (it.hasNext()) {
            T curEntity = it.next();         
            curEntity.setTagFalse();    //first clear any current tag
            Vector2D to = Vector2D.sub(curEntity.getPos(), entity.getPos());

            //the bounding radius of the other is taken into account by adding it to the range
            double range = radius + curEntity.getBoundingRadius();

            //if entity within range, tag for further consideration. (working in distance-squared space to avoid sqrts)
            if ((curEntity != entity) && (to.lengthSq() < range * range)) {
                curEntity.setTagTrue();
            }
        }
    }

    /**
     *  Given a pointer to an entity and a std container of pointers to nearby
     *  entities, this function checks to see if there is an overlap between
     *  entities. If there is, then the entities are moved away from each other
     */
    public static <T extends BaseGameEntity, conT extends List<T>> void enforceNonPenetrationConstraint
        (final T entity, final conT containerOfEntities) 
    {
        //iterate through all entities checking for any overlap of bounding radii
        ListIterator<T> it = containerOfEntities.listIterator();
        while (it.hasNext()) {
            T curEntity = it.next();
            //make sure we don't check against the individual
            if (curEntity == entity) {
                continue;
            }
            //calculate the distance between the positions of the entities
            Vector2D ToEntity = Vector2D.sub(entity.getPos(), curEntity.getPos());

            double DistFromEachOther = ToEntity.length();

            //if this distance is smaller than the sum of their radii then this
            //entity must be moved away in the direction parallel to the ToEntity vector   
            double AmountOfOverLap = curEntity.getBoundingRadius() + entity.getBoundingRadius() - DistFromEachOther;

            if (AmountOfOverLap >= 0) {
                //move the entity a distance away equivalent to the amount of overlap.
                entity.setPos(Vector2D.add(entity.getPos(), Vector2D.mul(Vector2D.div(ToEntity, DistFromEachOther), AmountOfOverLap)));
            }
        }
    }

    /**
     *  tests a line segment AB against a container of entities. First of all
     *  a test is made to confirm that the entity is within a specified range of 
     *  the one_to_ignore (positioned at A). If within range the intersection test is made.
     *  returns a list of all the entities that tested positive for intersection
     */
    public static <T extends BaseGameEntity, conT extends List<T>> List<T> getEntityLineSegmentIntersections
        (final conT entities, int the_one_to_ignore, Vector2D A, Vector2D B) 
    {
        return getEntityLineSegmentIntersections(entities, the_one_to_ignore, A, B, Utils.MaxDouble);
    }

    public static <T extends BaseGameEntity, conT extends List<T>> List<T> getEntityLineSegmentIntersections
        (final conT entities, int the_one_to_ignore, Vector2D A, Vector2D B, double range) 
    {
        ListIterator<T> it = entities.listIterator();
        List<T> hits = new LinkedList<T>();

        //iterate through all entities checking against the line segment AB
        while (it.hasNext()) {
            T curEntity = it.next();
            //if not within range or the entity being checked is the_one_to_ignore just continue with the next entity
            if ((curEntity.getId() == the_one_to_ignore)
                    || (Vector2D.vec2DDistanceSq(curEntity.getPos(), A) > range * range)) {
                continue;
            }
            //if the distance to AB is less than the entities bounding radius then there is an intersection so add it to hits
            if (Geometry.DistToLineSegment(A, B, curEntity.getPos()) < curEntity.getBoundingRadius()) {
                hits.add(curEntity);
            }
        }
        return hits;
    }

    /**
     *  tests a line segment AB against a container of entities. First of all
     *  a test is made to confirm that the entity is within a specified range of 
     *  the one_to_ignore (positioned at A). If within range the intersection test is made.
     *  returns the closest entity that tested positive for intersection or NULL if none found
     */
    public static <T extends BaseGameEntity, conT extends List<T>> T getClosestEntityLineSegmentIntersection
        (final conT entities, int the_one_to_ignore, Vector2D A, Vector2D B) 
    {
        return getClosestEntityLineSegmentIntersection(entities, the_one_to_ignore, A, B, Utils.MaxDouble);
    }

    public static <T extends BaseGameEntity, conT extends List<T>> T getClosestEntityLineSegmentIntersection
        (final conT entities, int the_one_to_ignore, Vector2D A, Vector2D B, double range) 
    {
        ListIterator<T> it = entities.listIterator();

        T closestEntity = null;
        double closestDist = Utils.MaxDouble;

        //iterate through all entities checking against the line segment AB
        while (it.hasNext()) {
            T curEntity = it.next();

            double distSq = Vector2D.vec2DDistanceSq(curEntity.getPos(), A);

            //if not within range or the entity being checked is the_one_to_ignore just continue with the next entity
            if ((curEntity.getId() == the_one_to_ignore) || (distSq > range * range)) {
                continue;
            }

            //if the distance to AB is less than the entities bounding radius then there is an intersection so add it to hits
            if (Geometry.DistToLineSegment(A, B, curEntity.getPos()) < curEntity.getBoundingRadius()) {
                if (distSq < closestDist) {
                    closestDist = distSq;
                    closestEntity = curEntity;
                }
            }
        }
        return closestEntity;
    }
}
