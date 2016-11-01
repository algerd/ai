
package Steering.behaviors;

import Steering.Params;
import Steering.Vehicle;
import java.util.function.Function;

public enum BehaviorType {
    
    SEEK(Seek::new, Params.PRIORITY_SEEK),
    FLEE(Flee::new, Params.PRIORITY_FLEE),
    ARRIVE(Arrive::new, Params.PRIORITY_ARRIVE),
    WANDER(Wander::new, Params.PRIORITY_WANDER),
    EVADE(Evade::new, Params.PRIORITY_EVADE),
    PURSUIT(Pursuit::new, Params.PRIORITY_PURSUIT),
    WALL_AVOIDANCE(WallAvoidance::new, Params.PRIORITY_WALL_AVOIDANCE),
    OBSTACLE_AVOIDANCE(ObstacleAvoidance::new, Params.PRIORITY_OBSTACLE_AVOIDANCE),
    INTERPOSE(Interpose::new, Params.PRIORITY_INTERPOSE),
    HIDE(Hide::new, Params.PRIORITY_HIDE),
    FOLLOW_PATH(FollowPath::new, Params.PRIORITY_FOLLOW_PATH),
    OFFSET_PURSUIT(OffsetPursuit::new, Params.PRIORITY_OFFSET_PURSUIT),
    SEPARATION(Separation::new, Params.PRIORITY_SEPARATION),
    ALIGNMENT(Alignment::new, Params.PRIORITY_ALIGNMENT),
    COHESION(Cohesion::new, Params.PRIORITY_COHESION), 
    FLOCKING(Flocking::new, Params.PRIORITY_FLOCKING);

    private final Function<Vehicle, Behavior> closure;
    private int priority;
    
    private BehaviorType(Function<Vehicle, Behavior> callback, int priority) {
        this.closure = callback;
        this.priority = priority;
    }
    
    /**
     * Get new instance of State abstract class.
     */
    public Behavior get(Vehicle vehicle) {
        return closure.apply(vehicle);
    }

    public int getPriority() {
        return priority;
    }
       
}
