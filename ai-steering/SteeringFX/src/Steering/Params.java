
package Steering;

public class Params {
    
    public final static int NUM_AGENTS = 300;
    public final static int NUM_OBSTACLES = 7;
    public final static int MIN_OBSTACLE_RADIUS = 10;
    public final static int MAX_OBSTACLE_RADIUS = 30;
    //number of horizontal cells used for spatial partitioning
    public final static int NUM_CELLS_X = 7;
    //number of vertical cells used for spatial partitioning
    public final static int NUM_CELLS_Y = 7;
    //how many samples the smoother will use to average a value
    public final static int NUM_SAMPLES_FOR_SMOOTHING = 10;
    //this is used to multiply the steering force AND all the multipliers found in SteeringBehavior
    public final static double STEERING_FORCE_TWEAKER = 200.0;
    public final static double MAX_STEERING_FORCE = 2.0 * STEERING_FORCE_TWEAKER;
    public final static double MAX_SPEED = 150.0;
    public final static double VEHICLE_MASS = 1.0;
    public final static double VEHICLE_SCALE = 3.0;
    public final static double MAX_TURN_RATE_PER_SECOND = Math.PI;
    //use these values to tweak the amount that each steering force contributes to the total steering force
    public final static double SEPARATION_WEIGHT = 1.0 * STEERING_FORCE_TWEAKER;
    public final static double ALIGNMENT_WEIGHT = 1.0 * STEERING_FORCE_TWEAKER;  
    public final static double COHESION_WEIGHT = 2.0 * STEERING_FORCE_TWEAKER;
    public final static double OBSTACLE_AVOIDANCE_WEIGHT = 10.0 * STEERING_FORCE_TWEAKER;
    public final static double WALL_AVOIDANCE_WEIGHT = 10.0 * STEERING_FORCE_TWEAKER;        
    public final static double WANDER_WEIGHT = 1.0 * STEERING_FORCE_TWEAKER;
    public final static double SEEK_WEIGHT = 1.0 * STEERING_FORCE_TWEAKER;
    public final static double FLEE_WEIGHT = 1.0 * STEERING_FORCE_TWEAKER;
    public final static double ARRIVE_WEIGHT = 1.0 * STEERING_FORCE_TWEAKER;
    public final static double PURSUIT_WEIGHT = 1.0 * STEERING_FORCE_TWEAKER;
    public final static double OFFSET_PURSUIT_WEIGHT = 1.0 * STEERING_FORCE_TWEAKER;
    public final static double INTERPOSE_WEIGHT = 1.0 * STEERING_FORCE_TWEAKER;
    public final static double HIDE_WEIGHT = 1.0 * STEERING_FORCE_TWEAKER;
    public final static double EVADE_WEIGHT = 0.01 * STEERING_FORCE_TWEAKER;
    public final static double FOLLOW_PATH_WEIGHT = 0.05 * STEERING_FORCE_TWEAKER;
    //how close a neighbour must be before an agent perceives it (considers it to be within its neighborhood)
    public final static double VIEW_DISTANCE = 50.0;
    //used in obstacle avoidance
    public final static double MIN_DETECTION_BOX_LENGTH = 40.0;
    //used in wall avoidance
    public final static double WALL_DETECTION_FEELER_LENGTH = 40.0;
    //these are the probabilities that a steering behavior will be used when the prioritized dither calculate method is used
    public final static double PR_WALL_AVOIDANCE = 0.5;
    public final static double PR_OBSTACLE_AVOIDANCE = 0.5;
    public final static double PR_SEPARATION = 0.2;        
    public final static double PR_ALIGNMENT = 0.3;        
    public final static double PR_COHESION = 0.6;        
    public final static double PR_WANDER = 0.8;        
    public final static double PR_SEEK = 0.8;        
    public final static double PR_FLEE = 0.6;
    public final static double PR_EVADE = 1.0;
    public final static double PR_HIDE = 0.8;        
    public final static double PR_ARRIVE = 0.5;        
            
}
