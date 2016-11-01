
package Steering.behaviors;

import Steering.Params;
import Steering.Path;
import Steering.Vehicle;
import common.D2.Vector2D;


public class FollowPath extends Behavior {
    
    private Vehicle vehicle;
    private Path path;
    private Seek seek;
    private Arrive arrive;
    private double deceleration = Params.DECELERATION_NORMAL;
    private final double WEIGHT = Params.FOLLOW_PATH_WEIGHT;
    private final double PROBABILITY = Params.PR_FOLLOW_PATH;
    
    public FollowPath(Vehicle vehicle) {
        this.vehicle = vehicle;
        seek = new Seek(vehicle);
        arrive = new Arrive(vehicle);
    }
    
    @Override
    public Vector2D getWeightForce() {
        return Vector2D.mul(getForce(), WEIGHT);
    }
    
    @Override
    public Vector2D getDitheredForce() {
        return Vector2D.mul(getForce(), WEIGHT/PROBABILITY);
    }
    
    /**
     *  Given a series of Vector2Ds, this method produces a force that will
     *  move the agent along the waypoints in order. The agent uses the
     * 'Seek' behavior to move to the next waypoint - unless it is the last
     *  waypoint, in which case it 'Arrives'
     */
    @Override
    public Vector2D getForce() {
        Vector2D force = null;
        try {
            if (path == null) {
                throw new NullPointerException("Path is null.");
            }    
            double waypointSeekDistSq = Params.WAYPOINT_SEEK_DIST * Params.WAYPOINT_SEEK_DIST;
            //Path path = vehicle.getSteering().getPath();
            //move to next target if close enough to current target (working in distance squared space)
            if (Vector2D.vec2DDistanceSq(path.getCurrentWaypoint(), vehicle.getPos()) < waypointSeekDistSq) {
                path.setNextWaypoint();
            }
            if (path.finished()) {
                arrive.setTargetPos(path.getCurrentWaypoint());
                force = arrive.getForce();
            }
            else {
                seek.setTargetPos(path.getCurrentWaypoint());
                force = seek.getForce();
            }     
        }
        catch(NullPointerException ex) {
            ex.printStackTrace();
        }
        return force;  
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }    

    public void setDeceleration(double deceleration) {
        this.deceleration = deceleration;
    }
       
}
