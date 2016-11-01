
package Steering.behaviors;

import Steering.Vehicle;
import common.D2.Vector2D;

public class Flocking extends Behavior {
    
    private Wander wander;
    private Alignment alignment;
    private Cohesion cohesion;
    private Separation separation;
    
    public Flocking(Vehicle vehicle) {
        wander = new Wander(vehicle);
        alignment = new Alignment(vehicle);
        cohesion = new Cohesion(vehicle);
        separation = new Separation(vehicle);
        // пометить соседей в диапазоне только 1 раз(при расчёте силы в Alignment), 
        // остальные поведения будут испольщовать помеченных сосоедей в Alignment
        alignment.setTagAllNeighbors(true);
        alignment.setTagCellNeighbors(true);
        cohesion.setTagAllNeighbors(false);
        cohesion.setTagCellNeighbors(false);
        separation.setTagAllNeighbors(false);
        separation.setTagCellNeighbors(false);       
    }
    
    @Override
    public Vector2D getWeightForce() {
        Vector2D force = new Vector2D();
        force.
            add(wander.getWeightForce()).
            add(alignment.getWeightForce()).
            add(cohesion.getWeightForce()).
            add(separation.getWeightForce());
        return force;                       
    }
    
    @Override
    public Vector2D getDitheredForce() {
        Vector2D force = new Vector2D();
        force.
            add(wander.getDitheredForce()).
            add(alignment.getDitheredForce()).
            add(cohesion.getDitheredForce()).
            add(separation.getDitheredForce());
        return force;
    }
       
    @Override
    public Vector2D getForce() {
        return new Vector2D();
    }
          
}
