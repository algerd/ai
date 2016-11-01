
package Steering;

public class Params {
    
    public final static int NUM_AGENTS = 100;
    public final static int NUM_OBSTACLES = 7;
    public final static int MIN_OBSTACLE_RADIUS = 10;
    public final static int MAX_OBSTACLE_RADIUS = 30;
    //number of horizontal cells used for spatial partitioning
    public final static int NUM_CELLS_X = 7;
    //number of vertical cells used for spatial partitioning
    public final static int NUM_CELLS_Y = 7;
    //how many samples the smoother will use to average a value
    public final static int NUM_SAMPLES_FOR_SMOOTHING = 5;
    //this is used to multiply the steering force AND all the multipliers found in SteeringBehavior
    public final static double STEERING_FORCE_TWEAKER = 200.0;
    public final static double MAX_STEERING_FORCE = 2.0 * STEERING_FORCE_TWEAKER;
    public final static double MAX_SPEED = 150.0;
    public final static double VEHICLE_MASS = 1.0;
    public final static double VEHICLE_SCALE = 3.0;
    public final static double MAX_TURN_RATE_PER_SECOND = Math.PI;   
    //how close a neighbour must be before an agent perceives it (considers it to be within its neighborhood)
    public final static double VIEW_DISTANCE = 50.0;
    //used in obstacle avoidance
    public final static double MIN_DETECTION_BOX_LENGTH = 40.0;
    //used in wall avoidance
    public final static double WALL_DETECTION_FEELER_LENGTH = 40.0;
    
    // for SteeringBehavior::followPath()
    public final static double WAYPOINT_SEEK_DIST = 30;
    
    // Deceleration
    public final static double DECELERATION_SLOW = 0.3;
    public final static double DECELERATION_NORMAL = 0.6;
    public final static double DECELERATION_FAST = 0.9;
       
    // for SteeringBehavior::wander()   
    public final static double WANDER_RAD = 1.2;   //the radius of the constraining circle for the wander behavior
    public final static double WANDER_DIST = 2.0;  //distance the wander circle is projected in front of the agent  
    public final static double WANDER_JITTER_PER_SEC = 80.0;   //the maximum amount of displacement along the circle each frame
      
    // for SteeringBehavior::calculate...() : calculatePrioritized(), calculateWeightedSum(), calculateDithered()
    // множители при расчёте вектора силы по добавочному вектору скорости f = plusV * k
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
    
    // for SteeringBehavior::calculateDithered()
    public final static double PR_WALL_AVOIDANCE = 0.5;
    public final static double PR_OBSTACLE_AVOIDANCE = 0.5;
    public final static double PR_SEPARATION = 0.2;        
    public final static double PR_ALIGNMENT = 0.3;        
    public final static double PR_COHESION = 0.6;        
    public final static double PR_WANDER = 0.8;        
    public final static double PR_SEEK = 0.8;        
    public final static double PR_FLEE = 0.6;
    public final static double PR_EVADE = 1.0;
    public final static double PR_PURSUIT = 1.0;
    public final static double PR_HIDE = 0.8;        
    public final static double PR_ARRIVE = 0.5; 
    public final static double PR_INTERPOSE = 1.0;
    public final static double PR_FOLLOW_PATH = 1.0;
    public final static double PR_OFFSET_PURSUIT = 1.0; 
    
    // приоритеты поведений
    public final static int PRIORITY_WALL_AVOIDANCE = 1;
    public final static int PRIORITY_OBSTACLE_AVOIDANCE = 2;
    public final static int PRIORITY_HIDE = 3;
    public final static int PRIORITY_PURSUIT = 4;
    public final static int PRIORITY_EVADE = 5;  
    public final static int PRIORITY_SEPARATION = 6;
    public final static int PRIORITY_ALIGNMENT = 7;
    public final static int PRIORITY_COHESION = 8; 
    public final static int PRIORITY_FLOCKING = 9;
    public final static int PRIORITY_SEEK = 10;
    public final static int PRIORITY_FLEE = 11;
    public final static int PRIORITY_ARRIVE = 12;
    public final static int PRIORITY_WANDER = 13;
    public final static int PRIORITY_INTERPOSE = 14;
    public final static int PRIORITY_FOLLOW_PATH = 15;
    public final static int PRIORITY_OFFSET_PURSUIT = 16; 
                         
}
