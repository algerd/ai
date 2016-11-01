
package generator;

public class Params {
       
    public static double TIME_TACT = 0.015;                 // длительность такта в сек.
    
    public static double PLAYER_COEFF_FORCE = 100;          // условный делитель 
    public static double PLAYER_DIST_TAKE_BALL = 1.0;       // metrs, дистанция приёма мяча
    public static double PLAYER_DIST_SQ_TAKE_BALL = PLAYER_DIST_TAKE_BALL * PLAYER_DIST_TAKE_BALL;
    
    public static double BALL_DOWN_SPEED_KICK_GROUND = 0.55;// коэффициент снижния скорости мяча при ударе об землю
    public static double BALL_VELOCITY_Z_MIN = 2;           // минимальная скорость мяча, при которой при ударе об землю мяч подпрыгивает
    public static double BALL_DECELERATION = 1;             // замедление скорости мяча при движении по земле (м/c в секунду)
    public static double BALL_DECELERATION_TACT = BALL_DECELERATION * TIME_TACT;
    public static double BALL_DECELERATION_TACT_SQ = BALL_DECELERATION_TACT * BALL_DECELERATION_TACT;
    
}
