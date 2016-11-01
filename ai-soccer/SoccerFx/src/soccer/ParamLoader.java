
package soccer;

import common.misc.FileLoaderBase;
import java.io.IOException;

public class ParamLoader extends FileLoaderBase {

    public final static ParamLoader instance;

    static {
        try {
            instance = new ParamLoader();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
  
    public double GoalWidth;

    public int NumSupportSpotsX;
    public int NumSupportSpotsY;

    //these values tweak the various rules used to calculate the support spots
    public double Spot_PassSafeScore;
    public double Spot_CanScoreFromPositionScore;
    public double Spot_DistFromControllingPlayerScore;
    public double Spot_ClosenessToSupportingPlayerScore;
    public double Spot_AheadOfAttackerScore;  

    public double SupportSpotUpdateFreq ;

    public double ChancePlayerAttemptsPotShot; 
    public double ChanceOfUsingArriveTypeReceiveBehavior;

    public double BallSize;
    public double BallMass;
    public double Friction;

    public double KeeperInBallRange;
    public double KeeperInBallRangeSq;

    public double PlayerInTargetRange;
    public double PlayerInTargetRangeSq;

    public double PlayerMass;

    //max steering force
    public double PlayerMaxForce; 
    public double PlayerMaxSpeedWithBall;
    public double PlayerMaxSpeedWithoutBall;
    public double PlayerMaxTurnRate;
    public double PlayerScale;
    public double PlayerComfortZone;

    public double PlayerKickingDistance;
    public double PlayerKickingDistanceSq;

    public double PlayerKickFrequency; 

    public double  MaxDribbleForce;
    public double  MaxShootingForce;
    public double  MaxPassingForce;

    public double  PlayerComfortZoneSq;

    //in the range zero to 1.0. adjusts the amount of noise added to a kick,
    //the lower the value the worse the players get
    public double  PlayerKickingAccuracy;

    //the number of times the SoccerTeam::CanShoot method attempts to find
    //a valid shot
    public int    NumAttemptsToFindValidStrike;

    //the distance away from the center of its home region a playern must be to be considered at home
    public double WithinRangeOfHome;

    //how close a player must get to a sweet spot before he can change state
    public double WithinRangeOfSupportSpot;
    public double WithinRangeOfSupportSpotSq;

    //the minimum distance a receiving player must be from the passing player
    public double   MinPassDist;
    public double   GoalkeeperMinPassDist;

    //this is the distance the keeper puts between the back of the net
    //and the ball when using the interpose steering behavior
    public double  GoalKeeperTendingDistance;

    //when the ball becomes within this distance of the goalkeeper he changes state to intercept the ball
    public double  GoalKeeperInterceptRange;
    public double  GoalKeeperInterceptRangeSq;

    //how close the ball must be to a receiver before he starts chasing it
    public double  BallWithinReceivingRange;
    public double  BallWithinReceivingRangeSq;

    //these values control what debug info you can see
    public boolean  bStates;
    public boolean  bIDs;
    public boolean  bSupportSpots;
    public boolean  bRegions;
    public boolean  bShowControllingTeam;
    public boolean  bViewTargets;
    public boolean  bHighlightIfThreatened;

    public int FrameRate; 
    public double SeparationCoefficient;

    //how close a neighbour must be before an agent perceives it
    public double ViewDistance;

    //zero this to turn the constraint off
    public boolean nonPenetrationConstraint;

    private ParamLoader() throws IOException {
        super(ParamLoader.class.getResourceAsStream("Params.ini"));
        GoalWidth = getNextParameterDouble(); 
    
        NumSupportSpotsX = getNextParameterInt();    
        NumSupportSpotsY = getNextParameterInt();  

        Spot_PassSafeScore = getNextParameterDouble();
        Spot_CanScoreFromPositionScore = getNextParameterDouble();
        Spot_DistFromControllingPlayerScore = getNextParameterDouble();
        Spot_ClosenessToSupportingPlayerScore = getNextParameterDouble();
        Spot_AheadOfAttackerScore = getNextParameterDouble();

        SupportSpotUpdateFreq = getNextParameterDouble(); 

        ChancePlayerAttemptsPotShot = getNextParameterDouble();
        ChanceOfUsingArriveTypeReceiveBehavior = getNextParameterDouble();

        BallSize = getNextParameterDouble();    
        BallMass = getNextParameterDouble();    
        Friction = getNextParameterDouble(); 

        KeeperInBallRange = getNextParameterDouble();    
        PlayerInTargetRange = getNextParameterDouble(); 
        PlayerKickingDistance = getNextParameterDouble(); 
        PlayerKickFrequency = getNextParameterDouble();


        PlayerMass = getNextParameterDouble(); 
        PlayerMaxForce = getNextParameterDouble();    
        PlayerMaxSpeedWithBall = getNextParameterDouble();   
        PlayerMaxSpeedWithoutBall = getNextParameterDouble();   
        PlayerMaxTurnRate = getNextParameterDouble();   
        PlayerScale = getNextParameterDouble();      
        PlayerComfortZone = getNextParameterDouble();  
        PlayerKickingAccuracy = getNextParameterDouble();

        NumAttemptsToFindValidStrike = getNextParameterInt();



        MaxDribbleForce = getNextParameterDouble();    
        MaxShootingForce = getNextParameterDouble();    
        MaxPassingForce = getNextParameterDouble();  

        WithinRangeOfHome = getNextParameterDouble();    
        WithinRangeOfSupportSpot = getNextParameterDouble();    

        MinPassDist = getNextParameterDouble();
        GoalkeeperMinPassDist = getNextParameterDouble();

        GoalKeeperTendingDistance = getNextParameterDouble();    
        GoalKeeperInterceptRange = getNextParameterDouble();
        BallWithinReceivingRange = getNextParameterDouble();

        bStates = getNextParameterBool();    
        bIDs = getNextParameterBool(); 
        bSupportSpots = getNextParameterBool();     
        bRegions = getNextParameterBool();
        bShowControllingTeam = getNextParameterBool();
        bViewTargets = getNextParameterBool();
        bHighlightIfThreatened = getNextParameterBool();

        FrameRate = getNextParameterInt();

        SeparationCoefficient = getNextParameterDouble(); 
        ViewDistance = getNextParameterDouble(); 
        nonPenetrationConstraint = getNextParameterBool(); 


        BallWithinReceivingRangeSq = BallWithinReceivingRange * BallWithinReceivingRange;
        KeeperInBallRangeSq = KeeperInBallRange * KeeperInBallRange;
        PlayerInTargetRangeSq = PlayerInTargetRange * PlayerInTargetRange;   
        PlayerKickingDistance += BallSize;
        PlayerKickingDistanceSq = PlayerKickingDistance * PlayerKickingDistance;
        PlayerComfortZoneSq = PlayerComfortZone * PlayerComfortZone;
        GoalKeeperInterceptRangeSq = GoalKeeperInterceptRange * GoalKeeperInterceptRange;
        WithinRangeOfSupportSpotSq = WithinRangeOfSupportSpot * WithinRangeOfSupportSpot;
    }
    
    public static ParamLoader getInstance() {
        return instance;
    }
}
