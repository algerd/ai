
package soccer;

import soccer.FieldPlayerStates.ReturnToHomeRegion;
import soccer.FieldPlayerStates.Wait;
import soccer.TeamStates.Defending;
import soccer.GoalKeeperStates.TendGoal;
import common.D2.Vector;
import common.D2.Transformation;
import common.D2.Geometry;
import common.FSM.StateMachine;
import common.Game.EntityManager;
import common.Messaging.MessageDispatcher;
import common.misc.CppToJava;
import common.misc.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * class to define a team of soccer playing agents.A SoccerTeam
 * is implemented as a finite state machine and has states fo attacking, defending, and KickOff.
 */
public class SoccerTeam {

    public enum TeamColorType {
        BLUE, RED
    };
    public static TeamColorType blue = TeamColorType.BLUE;
    public static TeamColorType red = TeamColorType.RED;
    private TeamColorType color;
    private StateMachine<SoccerTeam> stateMachine; 
    private List<PlayerBase> playerList = new ArrayList<>();
    private SoccerPitch pitch;
    private Goal opponentsGoal;
    private Goal homeGoal;
    private SoccerTeam opponent;
    private PlayerBase controllingPlayer;
    private PlayerBase supportingPlayer;
    private PlayerBase receivingPlayer;
    private PlayerBase playerClosestToBall;
    private double distSqToBallOfClosestPlayer;
    
    private SupportSpotCalculator supportSpotCalc;
    // количество тактов паузы после последнего поиска
    private final int PAUSE_SPOT = 10;
    // счётчик тактов после удара
    private int spotTimer;
       
    public SoccerTeam(Goal home_goal, Goal opponents_goal, SoccerPitch pitch, TeamColorType color) {
        this.opponentsGoal = opponents_goal;
        this.homeGoal = home_goal;
        this.opponent = null;
        this.pitch = pitch;
        this.color = color;
        
        stateMachine = new StateMachine<>(this);
        stateMachine.setCurrentState(Defending.getInstance());
        stateMachine.setPreviousState(Defending.getInstance());
        stateMachine.setGlobalState(null);

        //create the players and goalkeeper
        createPlayers();

        //set default steering behaviors      
        for (PlayerBase player : playerList) {
            player.getSteering().separationOn();
        }
        //create the sweet spot calculator
        supportSpotCalc = new SupportSpotCalculator(ParamLoader.getInstance().NumSupportSpotsX, ParamLoader.getInstance().NumSupportSpotsY, this);
    }

    /**
     * creates all the players for this team.
     */
    private void createPlayers() {
        if (getColor() == blue) {
            //goalkeeper
            playerList.add(new GoalKeeper(
                this,
                1,
                TendGoal.getInstance(),
                new Vector(0, 1),
                new Vector(0.0, 0.0),
                ParamLoader.getInstance().PlayerMass,
                ParamLoader.getInstance().PlayerMaxForce,
                ParamLoader.getInstance().PlayerMaxSpeedWithoutBall,
                ParamLoader.getInstance().PlayerMaxTurnRate,
                ParamLoader.getInstance().PlayerScale));

            //create the players
            playerList.add(new FieldPlayer(
                this,
                6,
                Wait.getInstance(),
                new Vector(0, 1),
                new Vector(0.0, 0.0),
                ParamLoader.getInstance().PlayerMass,
                ParamLoader.getInstance().PlayerMaxForce,
                ParamLoader.getInstance().PlayerMaxSpeedWithoutBall,
                ParamLoader.getInstance().PlayerMaxTurnRate,
                ParamLoader.getInstance().PlayerScale,
                PlayerBase.PlayerRoleType.ATTACKER));

            playerList.add(new FieldPlayer(this,
                8,
                Wait.getInstance(),
                new Vector(0, 1),
                new Vector(0.0, 0.0),
                ParamLoader.getInstance().PlayerMass,
                ParamLoader.getInstance().PlayerMaxForce,
                ParamLoader.getInstance().PlayerMaxSpeedWithoutBall,
                ParamLoader.getInstance().PlayerMaxTurnRate,
                ParamLoader.getInstance().PlayerScale,
                PlayerBase.PlayerRoleType.ATTACKER));

            playerList.add(new FieldPlayer(
                this,
                3,
                Wait.getInstance(),
                new Vector(0, 1),
                new Vector(0.0, 0.0),
                ParamLoader.getInstance().PlayerMass,
                ParamLoader.getInstance().PlayerMaxForce,
                ParamLoader.getInstance().PlayerMaxSpeedWithoutBall,
                ParamLoader.getInstance().PlayerMaxTurnRate,
                ParamLoader.getInstance().PlayerScale,
                PlayerBase.PlayerRoleType.DEFFENDER));

            playerList.add(new FieldPlayer(
                this,
                5,
                Wait.getInstance(),
                new Vector(0, 1),
                new Vector(0.0, 0.0),
                ParamLoader.getInstance().PlayerMass,
                ParamLoader.getInstance().PlayerMaxForce,
                ParamLoader.getInstance().PlayerMaxSpeedWithoutBall,
                ParamLoader.getInstance().PlayerMaxTurnRate,
                ParamLoader.getInstance().PlayerScale,
                PlayerBase.PlayerRoleType.DEFFENDER));
        } 
        else {
            //goalkeeper
            playerList.add(new GoalKeeper(
                this,
                16,
                TendGoal.getInstance(),
                new Vector(0, -1),
                new Vector(0.0, 0.0),
                ParamLoader.getInstance().PlayerMass,
                ParamLoader.getInstance().PlayerMaxForce,
                ParamLoader.getInstance().PlayerMaxSpeedWithoutBall,
                ParamLoader.getInstance().PlayerMaxTurnRate,
                ParamLoader.getInstance().PlayerScale));

            //create the players
            playerList.add(new FieldPlayer(
                this,
                9,
                Wait.getInstance(),
                new Vector(0, -1),
                new Vector(0.0, 0.0),
                ParamLoader.getInstance().PlayerMass,
                ParamLoader.getInstance().PlayerMaxForce,
                ParamLoader.getInstance().PlayerMaxSpeedWithoutBall,
                ParamLoader.getInstance().PlayerMaxTurnRate,
                ParamLoader.getInstance().PlayerScale,
                PlayerBase.PlayerRoleType.ATTACKER));

            playerList.add(new FieldPlayer(
                this,
                11,
                Wait.getInstance(),
                new Vector(0, -1),
                new Vector(0.0, 0.0),
                ParamLoader.getInstance().PlayerMass,
                ParamLoader.getInstance().PlayerMaxForce,
                ParamLoader.getInstance().PlayerMaxSpeedWithoutBall,
                ParamLoader.getInstance().PlayerMaxTurnRate,
                ParamLoader.getInstance().PlayerScale,
                PlayerBase.PlayerRoleType.ATTACKER));

            playerList.add(new FieldPlayer(
                this,
                12,
                Wait.getInstance(),
                new Vector(0, -1),
                new Vector(0.0, 0.0),
                ParamLoader.getInstance().PlayerMass,
                ParamLoader.getInstance().PlayerMaxForce,
                ParamLoader.getInstance().PlayerMaxSpeedWithoutBall,
                ParamLoader.getInstance().PlayerMaxTurnRate,
                ParamLoader.getInstance().PlayerScale,
                PlayerBase.PlayerRoleType.DEFFENDER));

            playerList.add(new FieldPlayer(
                this,
                14,
                Wait.getInstance(),
                new Vector(0, -1),
                new Vector(0.0, 0.0),
                ParamLoader.getInstance().PlayerMass,
                ParamLoader.getInstance().PlayerMaxForce,
                ParamLoader.getInstance().PlayerMaxSpeedWithoutBall,
                ParamLoader.getInstance().PlayerMaxTurnRate,
                ParamLoader.getInstance().PlayerScale,
                PlayerBase.PlayerRoleType.DEFFENDER));
        }
        //register the players with the entity manager       
        for (PlayerBase player : playerList) {
            EntityManager.EntityMgr.registerEntity(player);
        }
    }

    /**
     * called each frame. Sets m_pClosestPlayerToBall to point to the player closest to the ball. 
     */
    private void calculateClosestPlayerToBall() {
        double closestSoFar = Float.MAX_VALUE;       
        for (PlayerBase player : playerList) {
            double dist = player.getPosition().distSq(getPitch().getBall().getPosition());
            //keep a record of this value for each player
            player.setDistSqToBall(dist);
            if (dist < closestSoFar) {
                closestSoFar = dist;
                playerClosestToBall = player;
            }
        }        
        distSqToBallOfClosestPlayer = closestSoFar;
    }

    /**
     *  renders the players and any team related info.
     */
    public void render(GraphicsContext gc) {        
        for (PlayerBase player : playerList) {
            player.render(gc);
        }       
        //show the controlling team and player at the top of the display
        if (ParamLoader.getInstance().bShowControllingTeam) {
            gc.setStroke(Color.WHITE);
            String str;
            if ((getColor() == blue) && isControllingPlayer()) {
                str = "Blue in Control";
                gc.strokeText("Blue in Control", 20, 11);
            } 
            else if ((getColor() == red) && isControllingPlayer()) {
                str = "Red in Control";
                gc.strokeText("Red in Control", 20, 11);
            }
            
            if (controllingPlayer != null) {
                gc.strokeText("Controlling Player: " + Integer.toString(controllingPlayer.getId()), getPitch().getXClient() - 150, 11);
            }
        }
        //render the sweet spots
        if (ParamLoader.getInstance().bSupportSpots && isControllingPlayer()) {
            supportSpotCalc.render(gc);
        }       
    }

    /**
     *  iterates through each player's update function and calculates frequently accessed info.
     */
    public void update() {
        ++spotTimer;
        //this information is used frequently so it's more efficient to calculate it just once each frame
        calculateClosestPlayerToBall();
        
        //the team state machine switches between attack/defense behavior. It
        //also handles the 'kick off' state where a team must return to their
        //kick off positions before the whistle is blown
        stateMachine.update();

        //now update each player
        for (PlayerBase player : playerList) {
            player.update();
        }
    }

    /**
     * calling this changes the state of all field players to that of 
     * ReturnToHomeRegion. Mainly used when a goal keeper has
     * possession
     */
    public void returnAllFieldPlayersToHome() {
        ListIterator<PlayerBase> it = playerList.listIterator();
        for (PlayerBase player : playerList) {
            if (player.getPlayerRole() != PlayerBase.PlayerRoleType.GOAL_KEEPER) {
                MessageDispatcher.dispatcher.dispatchMsg(
                    MessageDispatcher.SEND_MSG_IMMEDIATELY,
                    1,
                    player.getId(),
                    MessageType.GO_HOME,
                    null
                );
            }
        }      
    }

    /**
     *  Given a ball position, a kicking power and a reference to a vector2D
     *  this function will sample random positions along the opponent's goal-
     *  mouth and check to see if a goal can be scored if the ball was to be
     *  kicked in that direction with the given power. If a possible shot is 
     *  found, the function will immediately return true, with the target 
     *  position stored in the vector ShotTarget.
    
     * returns true if player has a clean shot at the goal and sets ShotTarget
     * to a normalized vector pointing in the direction the shot should be
     * made. Else returns false and sets heading to a zero vector
     */
    public boolean canShoot(Vector BallPos, double power) {
        return canShoot(BallPos, power, new Vector());
    }

    public boolean canShoot(Vector BallPos, double power, Vector ShotTarget) {
        //the number of randomly created shot targets this method will test 
        int NumAttempts = ParamLoader.getInstance().NumAttemptsToFindValidStrike;

        while (NumAttempts-- > 0) {
            //choose a random position along the opponent's goal mouth. (making sure the ball's radius is taken into account)
            ShotTarget.set(getOpponentsGoal().getCenter());

            //the y value of the shot position should lay somewhere between two goalposts (taking into consideration the ball diameter)
            int MinYVal = (int)(getOpponentsGoal().getLeftPost().y + getPitch().getBall().getBoundingRadius());
            int MaxYVal = (int)(getOpponentsGoal().getRightPost().y - getPitch().getBall().getBoundingRadius());

            ShotTarget.y = Utils.randInt(MinYVal, MaxYVal);

            //make sure striking the ball with the given power is enough to drive the ball over the goal line.
            double time = getPitch().getBall().timeToCoverDistance(BallPos, ShotTarget, power);

            //if it is, this shot is then tested to see if any of the opponent can intercept it.
            if (time >= 0) {
                if (isPassSafeFromAllOpponents(BallPos, ShotTarget, null, power)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * The best pass is considered to be the pass that cannot be intercepted 
     * by an opponent and that is as far forward of the receiver as possible  
     * If a pass is found, the receiver's address is returned in the 
     * reference, 'receiver' and the position the pass will be made to is 
     * returned in the  reference 'PassTarget'
     */
    public boolean findPass(
            final PlayerBase passer,
            CppToJava.ObjectRef<PlayerBase> receiver,
            Vector PassTarget,
            double power,
            double MinPassingDistance) 
    {
        double closestToGoalSoFar = Float.MAX_VALUE;
        Vector target = new Vector();
        boolean finded = false;
        
        for (PlayerBase player : playerList) {
            //make sure the potential receiver being examined is not this player
            //and that it is further away than the minimum pass dist
            if ((player != passer)
                && (passer.getPosition().distSq(player.getPosition()) > MinPassingDistance * MinPassingDistance)
                && getBestPassToReceiver(passer, player, target, power)) 
            {                                
                double Dist2Goal = Math.abs(target.x - getOpponentsGoal().getCenter().x);
                if (Dist2Goal < closestToGoalSoFar) {
                    closestToGoalSoFar = Dist2Goal;
                    receiver.set(player);
                    PassTarget.set(target);
                    finded = true;
                }          
            }
        }
        return finded;
    }

    /**
     *  Three potential passes are calculated. One directly toward the receiver's
     *  current position and two that are the tangents from the ball position
     *  to the circle of radius 'range' from the receiver.
     *  These passes are then tested to see if they can be intercepted by an
     *  opponent and to make sure they terminate within the playing area. If
     *  all the passes are invalidated the function returns false. Otherwise
     *  the function returns the pass that takes the ball closest to the 
     *  opponent's goal area.
     */
    public boolean getBestPassToReceiver(final PlayerBase passer, final PlayerBase receiver, Vector PassTarget, double power) {
        //assert (PassTarget != null);
        //first, calculate how much time it will take for the ball to reach 
        //this receiver, if the receiver was to remain motionless 
        double time = getPitch().getBall().timeToCoverDistance(
                getPitch().getBall().getPosition(),
                receiver.getPosition(),
                power);

        //return false if ball cannot reach the receiver after having been kicked with the given power
        if (time < 0) {
            return false;
        }

        //the maximum dist the receiver can cover in this time
        double interceptRange = time * receiver.getMaxSpeed();

        //Scale the intercept range
        final double scalingFactor = 0.3;
        interceptRange *= scalingFactor;

        //now calculate the pass targets which are positioned at the intercepts of the tangents from the ball to the receiver's range circle.
        Vector ip1 = new Vector();
        Vector ip2 = new Vector();
        Geometry.getTangentPoints(
                receiver.getPosition(),
                interceptRange,
                getPitch().getBall().getPosition(),
                ip1,
                ip2);

        Vector Passes[] = {ip1, receiver.getPosition(), ip2};
        final int numPassesToTry = Passes.length;

        // this pass is the best found so far if it is:
        //  1. Further upfield than the closest valid pass for this receiver found so far
        //  2. Within the playing area
        //  3. Cannot be intercepted by any opponent

        double closestSoFar = Float.MAX_VALUE;
        boolean bResult = false;

        for (int pass = 0; pass < numPassesToTry; ++pass) {
            double dist = Math.abs(Passes[pass].x - getOpponentsGoal().getCenter().x);
            if ((dist < closestSoFar)
                && getPitch().getPlayingArea().inside(Passes[pass])
                && isPassSafeFromAllOpponents(getPitch().getBall().getPosition(),
                    Passes[pass],
                    receiver,
                    power)) 
            {
                closestSoFar = dist;
                PassTarget.set(Passes[pass]);
                bResult = true;
            }
        }
        return bResult;
    }

    /**
     * test if a pass from positions 'from' to 'target' kicked with force 
     * 'PassingForce'can be intercepted by an opposing player
     */
    public boolean isPassSafeFromOpponent(
            Vector from,
            Vector target,
            final PlayerBase receiver,
            final PlayerBase opp,
            double PassingForce) 
    {
        //move the opponent into local space.
        Vector toTargetNormalized = target.subn(from).normalize();
        Vector LocalPosOpp = Transformation.pointToLocalSpace(opp.getPosition(), toTargetNormalized, from);

        //if opponent is behind the kicker then pass is considered okay(this is 
        //based on the assumption that the ball is going to be kicked with a 
        //velocity greater than the opponent's max velocity)
        if (LocalPosOpp.x < 0) {
            return true;
        }

        //if the opponent is further away than the target we need to consider if
        //the opponent can reach the position before the receiver.
        if (from.distSq(target) < opp.getPosition().distSq(from)) {  
            return (receiver != null) ? target.distSq(opp.getPosition()) > target.distSq(receiver.getPosition()) : true;
        }

        //calculate how long it takes the ball to cover the dist to the 
        //position orthogonal to the opponent position
        double TimeForBall = getPitch().getBall().timeToCoverDistance(
            new Vector(0, 0),
            new Vector(LocalPosOpp.x, 0),
            PassingForce);

        //now calculate how far the opponent can run in this time
        double reach = opp.getMaxSpeed() 
            * TimeForBall
            + getPitch().getBall().getBoundingRadius()
            + opp.getBoundingRadius();

        //if the dist to the opponent's y position is less than his running
        //range plus the radius of the ball and the opponent radius then the ball can be intercepted
        return Math.abs(LocalPosOpp.y) >= reach;
    }

    /**
     * tests a pass from position 'from' to position 'target' against each member
     * of the opposing team. Returns true if the pass can be made without
     * getting intercepted
     */
    public boolean isPassSafeFromAllOpponents(
            Vector from,
            Vector target,
            final PlayerBase receiver,
            double PassingForce) 
    {
        for (PlayerBase player : getOpponent().getPlayerList()) {
            if (!isPassSafeFromOpponent(from, target, receiver, player, PassingForce)) {
                return false;
            }
        }
        return true;
    }

    /**
     * returns true if an opposing player is within the radius of the position given as a par ameter.
     */
    public boolean isOpponentWithinRadius(Vector pos, double rad) {       
        for (PlayerBase player : getOpponent().getPlayerList()) { 
            if (pos.distSq(player.getPosition()) < rad * rad) {
                return true;
            }
        }
        return false;
    }

    /**
     * this tests to see if a pass is possible between the requester and
     * the controlling player. If it is possible a message is sent to the
     * controlling player to pass the ball asap.
     */
    public void requestPass(FieldPlayer requester) {
        //maybe put a restriction here
        if (Utils.randFloat() > 0.1) {
            return;
        }
        if (isPassSafeFromAllOpponents(
                getControllingPlayer().getPosition(),
                requester.getPosition(),
                requester,
                ParamLoader.getInstance().MaxPassingForce)) 
        {
            //tell the player to make the pass let the receiver know a pass is coming 
            MessageDispatcher.dispatcher.dispatchMsg(
                    MessageDispatcher.SEND_MSG_IMMEDIATELY,
                    requester.getId(),
                    getControllingPlayer().getId(),
                    MessageType.PASS_TO_ME,
                    requester);
        }
    }

    /**
     * calculate the closest player to the SupportSpot.
     */
    public PlayerBase determineBestSupportingAttacker() {
        double closestSoFar = Float.MAX_VALUE;
        PlayerBase bestPlayer = null;
        for (PlayerBase player : playerList) {
            if ((player.getPlayerRole() == PlayerBase.PlayerRoleType.ATTACKER) && (player != controllingPlayer)) {
                //calculate the dist. Use the squared value to avoid sqrt
                double dist = player.getPosition().distSq(supportSpotCalc.getBestSupportingSpot());
                //if the dist is the closest so far and the player is not a
                //goalkeeper and the player is not the one currently controlling the ball, keep a record of this player
                if ((dist < closestSoFar)) {
                    closestSoFar = dist;
                    bestPlayer = player;
                }
            }
        }
        return bestPlayer;
    }
    
    public void determineBestSupportingPosition() {
        supportSpotCalc.determineBestSupportingPosition();
    }
    
    public void updateTargetsOfWaitingPlayers() {
        for (PlayerBase player : playerList) { 
            if (player.getPlayerRole() != PlayerBase.PlayerRoleType.GOAL_KEEPER) {
                //cast to a field player
                FieldPlayer fplayer = (FieldPlayer)player;

                if (fplayer.getStateMachine().isInState(Wait.getInstance()) || fplayer.getStateMachine().isInState(ReturnToHomeRegion.getInstance())) {
                    fplayer.getSteering().setTarget(fplayer.getHomeRegion().center());
                }
            }
        }
    }

    /**
     * return false if any of the team are not located within their home region.
     */
    public boolean isAllPlayersAtHome() {
        for (PlayerBase player : playerList) {   
            if (player.isInHomeRegion() == false) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isReadyForNextSpot() {
        return spotTimer > PAUSE_SPOT;          
    }
     
    public void resetSpotTimer() {
        this.spotTimer = 0;
    }
    
    /**
     * return name of the team ("Red" or "Blue").
     */
    public String name() {
        return (color == TeamColorType.BLUE) ? "Blue" : "Red";
    }

    /**
     ************************ Getters & Setters **********************************.
     */
    
    public List<PlayerBase> getPlayerList() {
        return playerList;
    }

    public StateMachine<SoccerTeam> getStateMachine() {
        return stateMachine;
    }

    public Goal getHomeGoal() {
        return homeGoal;
    }

    public Goal getOpponentsGoal() {
        return opponentsGoal;
    }

    public SoccerPitch getPitch() {
        return pitch;
    }

    public SoccerTeam getOpponent() {
        return opponent;
    }

    public void setOpponent(SoccerTeam opps) {
        opponent = opps;
    }

    public TeamColorType getColor() {
        return color;
    }

    public void setPlayerClosestToBall(PlayerBase plyr) {
        playerClosestToBall = plyr;
    }

    public PlayerBase getPlayerClosestToBall() {
        return playerClosestToBall;
    }

    public double getDistSqToBallOfClosestPlayer() {
        return distSqToBallOfClosestPlayer;
    }

    public Vector getSupportSpot() {
        return new Vector(supportSpotCalc.getBestSupportingSpot());
    }

    public PlayerBase getSupportingPlayer() {
        return supportingPlayer;
    }

    public void setSupportingPlayer(PlayerBase plyr) {
        supportingPlayer = plyr;
    }

    public PlayerBase getReceivingPlayer() {
        return receivingPlayer;
    }

    public void setReceivingPlayer(PlayerBase plyr) {
        receivingPlayer = plyr;
    }

    public PlayerBase getControllingPlayer() {
        return controllingPlayer;
    }

    public void setControllingPlayer(PlayerBase plyr) {
        controllingPlayer = plyr;
        //rub it in the opponent faces!
        getOpponent().removeControllingPlayer();
    }

    public boolean isControllingPlayer() {
        return controllingPlayer != null;
    }

    public void removeControllingPlayer() {
        controllingPlayer = null;
    }

    public PlayerBase getPlayerFromID(int id) {      
        for (PlayerBase player : playerList) {
            if (player.getId() == id) {
                return player;
            }
        }
        return null;
    }

    public void setPlayerHomeRegion(int plyr, int region) {
        playerList.get(plyr).setHomeRegion(region);
    }
    
}
