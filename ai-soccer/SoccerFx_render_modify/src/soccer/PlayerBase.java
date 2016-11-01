
package soccer;

import common.D2.Vector;
import java.util.List;
import common.Game.Region;
import common.Messaging.MessageDispatcher;
import java.util.ArrayList;

/**
 *  Definition of a soccer player base class. 
 */
abstract public class PlayerBase extends MovingEntity {
    
    static public enum PlayerRoleType {
        GOAL_KEEPER, ATTACKER, DEFFENDER
    };
    
    public static List<PlayerBase> playerList = new ArrayList<>();
    //this player's role in the team
    protected PlayerRoleType playerRole;
    //a pointer to this player's team
    protected SoccerTeam team;
    //the steering behaviors
    protected SteeringBehaviors steering;
    //the region that this player is assigned to.
    protected int homeRegion;
    //the region this player moves to before kickoff
    protected int defaultRegion;
    //the dist to the ball (in squared-space).
    protected double distSqToBall;
    
    // хранилище положений игрока 
    protected List<Vector> positionList = new ArrayList<>();
    // хранилище направлений движения игрока
    protected List<Vector> headingList = new ArrayList<>();
    // хранилище состояний игрока
    protected List<Integer> stateList = new ArrayList<>();
    protected int prevState = -1;

    public PlayerBase(
            SoccerTeam home_team,
            int home_region,
            Vector heading,
            Vector velocity,
            double mass,
            double max_force,
            double max_speed,
            double max_turn_rate,
            PlayerRoleType role) {

        super(home_team.getPitch().getRegionFromIndex(home_region).center(),
                Params.PLAYER_BOUNDING_RADIUS,
                velocity,
                max_speed,
                heading,
                mass,
                max_turn_rate,
                max_force);
        this.team = home_team;
        this.distSqToBall = Float.MAX_VALUE;
        this.homeRegion = home_region;
        this.defaultRegion = home_region;
        this.playerRole = role;
       
        //set up the steering behavior class
        steering = new SteeringBehaviors(this, team.getPitch(), getBall());

        //a player's start target is its start position (because it's just waiting)
        steering.setTarget(team.getPitch().getRegionFromIndex(homeRegion).center());
        
        positionList.add(0, getPosition());
        headingList.add(0, getHeading());
        playerList.add(this);
    }

    /**
     *  returns true if there is an opponent within this player's comfort zone.
     */
    public boolean isThreatened() {       
        for (PlayerBase opponent : getTeam().getOpponent().getPlayerList()) {
            if (isPositionInFrontOfPlayer(opponent.getPosition()) && (getPosition().distSq(opponent.getPosition()) < Params.PLAYER_THREAT_ZONE_SQ)) {    
                return true;
            }
        }
        return false;
    }

    /**
     *  rotates the player to face the ball.
     */
    public void trackBall() {
        rotateHeadingToFacePosition(getBall().getPosition());
    }

    /**
     * determines the player who is closest to the SupportSpot and messages him
     * to tell him to change state to SupportAttacker.
     */
    public void findSupport() {
        PlayerBase bestSupportPly = getTeam().determineBestSupportingAttacker();
        
        //if there is no support we need to find a suitable player.
        if (getTeam().getSupportingPlayer() == null) {
            getTeam().setSupportingPlayer(bestSupportPly);
            MessageDispatcher.dispatcher.dispatchMsg(
                MessageDispatcher.SEND_MSG_IMMEDIATELY,
                getId(),
                getTeam().getSupportingPlayer().getId(),
                MessageType.SUPPORT_ATTACKER,
                null);
        }

        //if the best player available to support the ATTACKER changes, update
        //the pointers and send messages to the relevant players to update their states
        if (bestSupportPly != null && (bestSupportPly != getTeam().getSupportingPlayer())) {
            if (getTeam().getSupportingPlayer() != null) {
                MessageDispatcher.dispatcher.dispatchMsg(
                    MessageDispatcher.SEND_MSG_IMMEDIATELY,
                    getId(),
                    getTeam().getSupportingPlayer().getId(),
                    MessageType.GO_HOME,
                    null);
            }
            getTeam().setSupportingPlayer(bestSupportPly);
            MessageDispatcher.dispatcher.dispatchMsg(
                MessageDispatcher.SEND_MSG_IMMEDIATELY,
                getId(),
                getTeam().getSupportingPlayer().getId(),
                MessageType.SUPPORT_ATTACKER,
                null);
        }
    }
    
    /**
     *  calculate dist to opponent's/home goal. Used frequently by the passing methods
     */
    public double getDistToOppGoal() {
        return Math.abs(getPosition().x - getTeam().getOpponentsGoal().getCenter().x);
    }
    
    /**
     ************************ Is?. *****************************  
     */
    /** 
     * return true if the ball can be grabbed by the goalkeeper. 
     */
    public boolean isBallWithinKeeperRange() {
        return getPosition().distSq(getBall().getPosition()) < Params.KEEPER_IN_BALL_RANGE_SQ;
    }

    /**
     * return true if the ball is within kicking range.
     */
    public boolean isBallWithinKickingRange() {
        return getPosition().distSq(getBall().getPosition()) < Params.PLAYER_KICKING_DIST_SQ;
    }

    /** 
     * return true if a ball comes within range of a receiver.
     */
    public boolean isBallWithinReceivingRange() {
        return getPosition().distSq(getBall().getPosition()) < Params.BALL_WITHIN_RECEIVING_RANGE_SQ;
    }

    /**
     * return true if the player is located within the boundaries of his home region.
     */
    public boolean isInHomeRegion() {
        return (playerRole == PlayerRoleType.GOAL_KEEPER) ?
           getPitch().getRegionFromIndex(homeRegion).inside(getPosition(), Region.normal) :
           getPitch().getRegionFromIndex(homeRegion).inside(getPosition(), Region.halfsize);
    }

    /**
     * return true if this player is ahead of the ATTACKER.
     */
    public boolean isAheadOfAttacker() {
        return Math.abs(getPosition().x - getTeam().getOpponentsGoal().getCenter().x)
                < Math.abs(getTeam().getControllingPlayer().getPosition().x - getTeam().getOpponentsGoal().getCenter().x);
    }

    /**
     * return true if the player is located at his steering target.
     */
    public boolean isAtTarget() {
        return getPosition().distSq(getSteering().getTarget()) < Params.PLAYER_IN_TARGET_RANGE_SQ;
    }

    /**
     * return true if the player is the closest player in his team to the ball.
     */
    public boolean isClosestTeamMemberToBall() {
        return getTeam().getPlayerClosestToBall() == this;
    }

    /**
     * return true if the point specified by 'position' is located in front of the player.
     */
    public boolean isPositionInFrontOfPlayer(Vector position) {   
        return position.subn(getPosition()).dot(getHeading()) > 0;
    }

    /**
     * return true if the player is the closest player on the pitch to the ball.
     */
    public boolean isClosestPlayerOnPitchToBall() {
        return isClosestTeamMemberToBall() && (getDistSqToBall() < getTeam().getOpponent().getDistSqToBallOfClosestPlayer());
    }

    /** 
     * return true if this player is the controlling player.
     */
    public boolean isControllingPlayer() {
        return getTeam().getControllingPlayer() == this;
    }

    /** 
     * return true if the player is located in the designated 'hot region' - the area close to the opponent's goal. 
     */
    public boolean isInHotRegion() {
        return Math.abs(getPosition().x - getTeam().getOpponentsGoal().getCenter().x) < Field.LENGTH_FIELD / 3.0;
    }
   
    /**
     ************************ Getters & Setters. *****************************  
     */
    public PlayerRoleType getPlayerRole() {
        return playerRole;
    }

    public double getDistSqToBall() {
        return distSqToBall;
    }

    public void setDistSqToBall(double val) {
        distSqToBall = val;
    }

    public void setDefaultHomeRegion() {
        homeRegion = defaultRegion;
    }
    
    public SoccerTeam getTeam() {
        return team;
    }
    
    public SoccerPitch getPitch() {
        return getTeam().getPitch();
    }
    
    public SoccerBall getBall() {
        return getTeam().getPitch().getBall();
    }
   
    public SteeringBehaviors getSteering() {
        return steering;
    }

    public Region getHomeRegion() {
        return getPitch().getRegionFromIndex(homeRegion);
    }

    public void setHomeRegion(int newRegion) {
        homeRegion = newRegion;
    }

    public List<Vector> getPositionList() {
        return positionList;
    }

    public List<Vector> getHeadingList() {
        return headingList;
    }

    public List<Integer> getStateList() {
        return stateList;
    }
       
}
