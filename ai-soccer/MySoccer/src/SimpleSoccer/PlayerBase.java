
package SimpleSoccer;

import java.util.ListIterator;
import common.D2.Vector;
import common.misc.AutoList;
import java.util.LinkedList;
import java.util.List;
import common.Game.Region;
import common.Messaging.MessageDispatcher;
import common.misc.Utils;

/**
 *  Desc: Definition of a soccer player base class. 
 */
abstract public class PlayerBase extends MovingEntity {

    static public enum PlayerRoleType {
        GOAL_KEEPER, ATTACKER, DEFFENDER
    };
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
    //the dist to the ball (in squared-space). This value is queried 
    //a lot so it's calculated once each time-step and stored here.
    protected double distSqToBall;
    //the vertex buffer
    protected List<Vector> vecPlayerVB = new LinkedList<Vector>();
    //the buffer for the transformed vertices
    protected List<Vector> vecPlayerVBTrans = new LinkedList<Vector>();

    public PlayerBase(
            SoccerTeam home_team,
            int home_region,
            Vector heading,
            Vector velocity,
            double mass,
            double max_force,
            double max_speed,
            double max_turn_rate,
            double scale,
            PlayerRoleType role) {

        super(home_team.getPitch().getRegionFromIndex(home_region).center(),
                scale * 10.0,
                velocity,
                max_speed,
                heading,
                mass,
                new Vector(scale, scale),
                max_turn_rate,
                max_force);
        this.team = home_team;
        this.distSqToBall = Utils.MaxFloat;
        this.homeRegion = home_region;
        this.defaultRegion = home_region;
        this.playerRole = role;

        //setup the vertex buffers and calculate the bounding radius
        final Vector player[] = {
            new Vector(-3, 8),
            new Vector(3, 10),
            new Vector(3, -10),
            new Vector(-3, -8)
        };
        final int NumPlayerVerts = player.length;

        for (int vtx = 0; vtx < NumPlayerVerts; ++vtx) {
            vecPlayerVB.add(player[vtx]);
            if (Math.abs(player[vtx].x) > boundingRadius) {
                boundingRadius = Math.abs(player[vtx].x);
            }
            if (Math.abs(player[vtx].y) > boundingRadius) {
                boundingRadius = Math.abs(player[vtx].y);
            }
        }
        //set up the steering behavior class
        steering = new SteeringBehaviors(this, team.getPitch(), getBall());

        //a player's start target is its start position (because it's just waiting)
        steering.setTarget(team.getPitch().getRegionFromIndex(homeRegion).center());
        new AutoList<PlayerBase>().add(this);
    }

    /**
     *  returns true if there is an opponent within this player's comfort zone.
     */
    public boolean isThreatened() {
        //check against all opponents to make sure non are within this player's comfort zone
        ListIterator<PlayerBase> iterator = getTeam().getOpponent().getPlayerList().listIterator();
        while (iterator.hasNext()) {
            PlayerBase curOpp = iterator.next();
            //calculate dist to the player. if dist is less than our
            //comfort zone, and the opponent is infront of the player, return true
            if (isPositionInFrontOfPlayer(curOpp.getPosition()) && (Vector.distSq(getPosition(), curOpp.getPosition()) < ParamLoader.instance.PlayerComfortZoneSq)) {
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
     * sets the player's heading to point at the current target.
     */
    public void trackTarget() {
        setHeading(Vector.normalize(Vector.sub(getSteering().getTarget(), getPosition())));
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
            MessageDispatcher.dispatcher.dispatchMsg(MessageDispatcher.SEND_MSG_IMMEDIATELY,
                    getId(),
                    getTeam().getSupportingPlayer().getId(),
                    MessageType.SUPPORT_ATTACKER,
                    null);
        }

        //if the best player available to support the ATTACKER changes, update
        //the pointers and send messages to the relevant players to update their states
        if (bestSupportPly != null && (bestSupportPly != getTeam().getSupportingPlayer())) {
            if (getTeam().getSupportingPlayer() != null) {
                MessageDispatcher.dispatcher.dispatchMsg(MessageDispatcher.SEND_MSG_IMMEDIATELY,
                        getId(),
                        getTeam().getSupportingPlayer().getId(),
                        MessageType.GO_HOME,
                        null);
            }
            getTeam().setSupportingPlayer(bestSupportPly);
            MessageDispatcher.dispatcher.dispatchMsg(MessageDispatcher.SEND_MSG_IMMEDIATELY,
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

    public double getDistToHomeGoal() {
        return Math.abs(getPosition().x - getTeam().getHomeGoal().getCenter().x);
    }
    
    /**
     * binary predicates for sort (see CanPassForward/Backward)
     */
    static public boolean sortByDistanceToOpponentsGoal(PlayerBase p1, PlayerBase p2) {
        return (p1.getDistToOppGoal() < p2.getDistToOppGoal());
    }

    static public boolean sortByReversedDistanceToOpponentsGoal(PlayerBase p1, PlayerBase p2) {
        return (p1.getDistToOppGoal() > p2.getDistToOppGoal());
    }

    /**
     ************************ Is?. *****************************  
     */
    /** 
     * return true if the ball can be grabbed by the goalkeeper. 
     */
    public boolean isBallWithinKeeperRange() {
        return (Vector.distSq(getPosition(), getBall().getPosition()) < ParamLoader.instance.KeeperInBallRangeSq);
    }

    /**
     * return true if the ball is within kicking range.
     */
    public boolean isBallWithinKickingRange() {
        return (Vector.distSq(getBall().getPosition(), getPosition()) < ParamLoader.instance.PlayerKickingDistanceSq);
    }

    /** 
     * return true if a ball comes within range of a receiver.
     */
    public boolean isBallWithinReceivingRange() {
        return (Vector.distSq(getPosition(), getBall().getPosition()) < ParamLoader.instance.BallWithinReceivingRangeSq);
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
        return (Vector.distSq(getPosition(), getSteering().getTarget()) < ParamLoader.instance.PlayerInTargetRangeSq);
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
        Vector ToSubject = Vector.sub(position, getPosition());      
        return ToSubject.dot(getHeading()) > 0;
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
        return Math.abs(getPosition().x - getTeam().getOpponentsGoal().getCenter().x) < getPitch().getPlayingArea().length() / 3.0;
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

}
