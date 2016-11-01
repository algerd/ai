
package soccer;

public class Params {
        
    // длительность такта в сек.
    public static double TIME_TACT = 0.015;
    
    //these values tweak the various rules used to calculate the support spots
    public static int NUM_SUPPORT_SPOTS_X = 13;
    public static int NUM_SUPPORT_SPOTS_Y = 6;
    public static double SPOT_PASS_SAFE = 2.0;
    public static double SPOT_CAN_SCORE_FROM_POSITION = 1.0;
    public static double SPOT_DIST_FROM_CONTROLLING_PLAYER = 2.0;
      
    //the chance a player might take a random pot shot at the goal
    public static double CHANCE_PLAYER_ATTEMPTS_SHOT = 0.05;
    //this is the chance that a player will receive a pass using the arrive steering behavior, rather than Pursuit
    public static double CHANCE_OF_USING_ARRIVE_TYPE_RECEIVE_BEHAVIOR = 0.5;
    
    
    
    
    ////////////////// Заменить и убрать : посмотреть используется ли сила мяча при расчёте steeringForce
    public static double BALL_MASS = 0.45;          // кг - не нужна при задании начальных скоростей мяча
    // acceleration = velocityInit = (force / BALL_MASS) * TIME_TACT
    public static double TURN_FORCE_BALL = 0.8;     // v_start = 120 m/c : force = v/time_tact*ball_mass = 120/0.015 
    public static double DRIBBLE_FORCE_BALL = 1.0;// заменить на initial velocity
    public static double SHOOT_FORCE_BALL = 6.0;// заменить на initial velocity
    public static double PASS_FORCE_BALL = 3.0; // заменить на initial velocity
    public static double FRICTION = -0.015; // замениь на BALL_COEFF_DECELERATION
    // Замены:
    public static double BALL_SIZE = 0.22;          // radius 0.11 m
    public static double BALL_COEFF_DECELERATION = 0.015; // ? привязать к TIME_TACT
    public static double BALL_VELOCITY_SHOOT = 120;     // m/s initial velocity for shoot
    public static double BALL_VELOCITY_SHOOT_TACT = BALL_VELOCITY_SHOOT * TIME_TACT;
    public static double BALL_VELOCITY_DRIBBLE = 20;    // m/s initial velocity for dribble 
    public static double BALL_VELOCITY_DRIBBLE_TACT = BALL_VELOCITY_DRIBBLE * TIME_TACT; 
    public static double BALL_VELOCITY_PASS = 70;       // m/s initial velocity for pass
    public static double BALL_VELOCITY_PASS_TACT = BALL_VELOCITY_PASS * TIME_TACT;
    public static double BALL_VELOCITY_TURN = 10;       // m/s initial velocity for rotation player with ball  
    public static double BALL_VELOCITY_TURN_TACT = BALL_VELOCITY_TURN  * TIME_TACT;       
    
            
    
            
    //the goalkeeper has to be this close to the ball to be able to interact with it        
    public static double KEEPER_IN_BALL_RANGE = 1.5;    // 1.5 м
    public static double KEEPER_IN_BALL_RANGE_SQ = KEEPER_IN_BALL_RANGE * KEEPER_IN_BALL_RANGE;
    public static double PLAYER_IN_TARGET_RANGE = 1.5;  // 1.5 м
    public static double PLAYER_IN_TARGET_RANGE_SQ = PLAYER_IN_TARGET_RANGE * PLAYER_IN_TARGET_RANGE;
       
    //player has to be this close to the ball to be able to kick it. The higher the value this gets, the easier it gets to tackle. 
    public static double PLAYER_KICKING_DIST = 0.5 + BALL_SIZE; // metrs
    public static double PLAYER_KICKING_DIST_SQ = PLAYER_KICKING_DIST * PLAYER_KICKING_DIST;
    
    public static double PLAYER_MASS = 3.0; // ??? TODO перевести в кг
    public static double PLAYER_MAX_FORCE = 1.0; // ???        
    public static double PLAYER_MAX_SPEED_WITH_BALL = 1.2; // ??? перевести в метры/такт или в секунду       
    public static double PLAYER_MAX_SPEED_WITHOUT_BALL = 1.6; // ??? перевести в метры/такт или в секунд       
    public static double PLAYER_MAX_TURN_RATE = 0.2; // rad/sec перевести в такт?      
    
    //when an opponents comes within this range the player will attempt to pass the ball. Players tend to pass more often, the higher the value
    public static double PLAYER_THREAT_ZONE = 5.0; // metr
    public static double PLAYER_THREAT_ZONE_SQ = PLAYER_THREAT_ZONE * PLAYER_THREAT_ZONE;
   
    //in the range zero to 1.0. adjusts the amount of noise added to a kick, the lower the value the worse the players get.           
    public static double PLAYER_KICKING_ACCURACY = 0.99;

    //the number of times the SoccerTeam::CanShoot method attempts to find a valid shot
    public static int NUM_ATTEMPTS_TO_FIND_VALID_STRIKE = 5;
         
    //the minimum distance a receiving player must be from the passing player
    public static double MIN_PASS_DIST = 3.0; //metrs
    public static double MIN_PASS_DIST_SQ = MIN_PASS_DIST * MIN_PASS_DIST;
    public static double GOALKEEPER_MIN_PASS_DIST = 5.0;//metrs
    public static double GOALKEEPER_MIN_PASS_DIST_SQ = GOALKEEPER_MIN_PASS_DIST * GOALKEEPER_MIN_PASS_DIST;
    
    //this is the distance the keeper puts between the back of the net and the ball when using the interpose steering behavior
    public static double GOALKEEPER_TENDING_DIST = 3.0; // metrs
    
    //when the ball becomes within this distance of the goalkeeper he changes state to intercept the ball
    public static double GOALKEEPER_INTERCEPT_RANGE = 12.0; // metrs
    public static double GOALKEEPER_INTERCEPT_RANGE_SQ = GOALKEEPER_INTERCEPT_RANGE * GOALKEEPER_INTERCEPT_RANGE;
       
    //how close the ball must be to a receiver before he starts chasing it
    public static double BALL_WITHIN_RECEIVING_RANGE = 4.0; // metrs
    public static double BALL_WITHIN_RECEIVING_RANGE_SQ = BALL_WITHIN_RECEIVING_RANGE * BALL_WITHIN_RECEIVING_RANGE;
     
    public static boolean NON_PENETRATION_CONSTRAINT = true;           
    public static double SEPARATION_COEFF = 10.0;
    public static double DIST_TO_PLAYERS_FOR_SEPARATION_BEHAVIOR = 3.0; // metrs
    public static double DIST_TO_PLAYERS_FOR_SEPARATION_BEHAVIOR_SQ = DIST_TO_PLAYERS_FOR_SEPARATION_BEHAVIOR * DIST_TO_PLAYERS_FOR_SEPARATION_BEHAVIOR;
   
    
    /////////////////////////////////////////////////////////////////
    
    public static double PLAYER_BOUNDING_RADIUS = 1.5;
    // макс. дистанция от игрока с мячом до спот-позиции при поиске и оценке спот позиции поддерживающего игрока
    public static double MAX_DIST_TO_SPOT = 40.0; // 40 м
   
         
}
