/**
 *  Desc:   class to divide a 2D space into a grid of cells each of which
 *          may contain a number of entities. Once created and initialized 
 *          with entities, fast proximity querys can be made by calling the
 *          CalculateNeighbors method with a position and proximity radius.
 *
 *          If an entity is capable of moving, and therefore capable of moving
 *          between cells, the Update method should be called each update-cycle
 *          to sychronize the entity and the cell space it occupies
 */
package common.misc;

import Steering.BaseGameEntity;
import common.D2.InvertedAABBox2D;
import common.D2.Vector2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * defines a cell containing a list of pointers to entities
 */
class Cell<entity extends Object> {
    
    //all the entities inhabiting this cell
    public List<entity> members = new LinkedList<entity>();
    //the cell's bounding box (it's inverted because the Window's default
    //co-ordinate system has a y axis that increases as it descends)
    public InvertedAABBox2D BBox;

    public Cell(Vector2D topleft, Vector2D botright) {
        BBox = new InvertedAABBox2D(topleft, botright);
    }
}

public class CellSpacePartition<entity extends BaseGameEntity> {
    //the required amount of cells in the space
    private List<Cell<entity>> cells = new ArrayList<Cell<entity>>();
    //this is used to store any valid neighbors when an agent searches its neighboring space
    private List<entity> neighbors;
    //this iterator will be used by the methods next and begin to traverse through the above vector of neighbors
    private ListIterator<entity> curNeighbor;
    //the width and height of the world space the entities inhabit
    private double spaceWidth;
    private double spaceHeight;
    //the number of cells the space is going to be divided up into
    private int numCellsX;
    private int numCellsY;
    private double cellSizeX;
    private double cellSizeY;

    public CellSpacePartition(double width, double height, int cellsX, int cellsY, int MaxEntities) {  
        spaceWidth = width;      //width of 2D space
        spaceHeight = height;    //height...
        numCellsX = cellsX;      //number of divisions horizontally
        numCellsY = cellsY;      //and vertically  
        neighbors = new ArrayList<entity>(MaxEntities);   //maximum number of entities to partition
        //calculate bounds of each cell
        cellSizeX = width / cellsX;
        cellSizeY = height / cellsY;

        //create the cells
        for (int y = 0; y < numCellsY; ++y) {
            for (int x = 0; x < numCellsX; ++x) {
                double left = x * cellSizeX;
                double right = left + cellSizeX;
                double top = y * cellSizeY;
                double bot = top + cellSizeY;
                cells.add(new Cell<entity>(new Vector2D(left, top), new Vector2D(right, bot)));
            }
        }
    }
    
    /**
     * Given a 2D vector representing a position within the game world, this
     * method calculates an index into its appropriate cell
     */
    private int positionToIndex(Vector2D pos) {
        int idx = (int) (numCellsX * pos.x / spaceWidth) + ((int) ((numCellsY) * pos.y / spaceHeight) * numCellsX);

        //if the entity's position is equal to vector2d(m_dSpaceWidth, m_dSpaceHeight)
        //then the index will overshoot. We need to check for this and adjust
        if (idx > (int) cells.size() - 1) {
            idx = (int) cells.size() - 1;
        }
        return idx;
    }

    /**
     * Used to add the entities to the data structure
     * adds entities to the class by allocating them to the appropriate cell
     */
    public void addEntity(entity ent) {
        assert (ent != null);
        int sz = cells.size();
        int idx = positionToIndex(ent.getPos());
        cells.get(idx).members.add(ent);
    }

    /**
     * update an entity's cell by calling this from your entity's Update method 
     * Checks to see if an entity has moved cells. If so the data structure
     * is updated accordingly
     */
    public void updateEntity(entity ent, Vector2D OldPos) {
        //if the index for the old pos and the new pos are not equal then the entity has moved to another cell.      
        int OldIdx = positionToIndex(OldPos);
        int NewIdx = positionToIndex(ent.getPos());
        if (NewIdx == OldIdx) {
            return;
        }
        //the entity has moved into another cell so delete from current cell and add to new one
        cells.get(OldIdx).members.remove(ent);
        cells.get(NewIdx).members.add(ent);
    }

    /**
     * This must be called to create the vector of neighbors.This method 
     *  examines each cell within range of the target, If the 
     *  cells contain entities then they are tested to see if they are situated
     *  within the target's neighborhood region. If they are they are added to
     *  neighbor list
     *
     *  this method stores a target's neighbors in
     *  the neighbor vector. After you have called this method use the begin, 
     *  next and end methods to iterate through the vector.
     */
    public void calculateNeighbors(Vector2D TargetPos, double QueryRadius) {
        neighbors.clear();
        //create the query box that is the bounding box of the target's query area
        InvertedAABBox2D QueryBox = new InvertedAABBox2D(
                Vector2D.sub(TargetPos, new Vector2D(QueryRadius, QueryRadius)),
                Vector2D.add(TargetPos, new Vector2D(QueryRadius, QueryRadius)));

        //iterate through each cell and test to see if its bounding box overlaps
        //with the query box. If it does and it also contains entities thenm make further proximity tests.
        ListIterator<Cell<entity>> c_it = cells.listIterator();
        while (c_it.hasNext()) {
            Cell<entity> curCell = c_it.next();
            //test to see if this cell contains members and if it overlaps the query box
            if (curCell.BBox.isOverlappedWith(QueryBox) && !curCell.members.isEmpty()) {              
                //add any entities found within query radius to the neighbor list
                ListIterator<entity> it = curCell.members.listIterator();
                while (it.hasNext()) {
                    entity ent = it.next();
                    if (Vector2D.vec2DDistanceSq(ent.getPos(), TargetPos) < QueryRadius * QueryRadius) {
                        neighbors.add(ent);
                    }
                }
            }
        }
    }

    /**
     * @return a reference to the entity at the front of the neighbor vector
     */
    public entity begin() {
        curNeighbor = neighbors.listIterator();
        return (!curNeighbor.hasNext()) ? null : curNeighbor.next();
    }

    /**
     * this returns the next entity in the neighbor vector
     */
    public entity next() {
        return (curNeighbor == null || !curNeighbor.hasNext()) ? null : curNeighbor.next();     
    }

    /**
     * returns true if the end of the vector is found (a zero value marks the end)
     */
    public boolean end() {
        return (curNeighbor == null || (!curNeighbor.hasNext()));
    }

    /**
     * clears the cells of all entities
     */
    public void emptyCells() {
        ListIterator<Cell<entity>> it = cells.listIterator();
        while (it.hasNext()) {
            it.next().members.clear();
        }
    }

    /**
     * call this to use the gdi to render the cell edges
     */
    public void renderCells() {
        ListIterator<Cell<entity>> curCell = cells.listIterator();
        while (curCell.hasNext()) {
            curCell.next().BBox.Render(false);
        }
    }
}
