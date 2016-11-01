
package SimpleSoccer.FieldPlayerStates;

import common.misc.CppToJava.ObjectRef;
import common.Messaging.Telegram;
import SimpleSoccer.PlayerBase;
import common.D2.Vector;
import SimpleSoccer.Define;
import SimpleSoccer.FieldPlayer;
import SimpleSoccer.MessageType;
import SimpleSoccer.ParamLoader;
import SimpleSoccer.SoccerBall;
import common.FSM.State;
import common.Debug.DbgConsole;
import common.Messaging.MessageDispatcher;
import common.misc.StreamUtilityFunction;
import common.misc.Utils;

public class KickBall extends State<FieldPlayer> {

    private static KickBall instance = new KickBall();

    private KickBall() {}

    public static KickBall getInstance() {
        return instance;
    }

    @Override
    public void enter(FieldPlayer player) {
        //let the team know this player is controlling
        player.getTeam().setControllingPlayer(player);

        //the player can only make so many kick attempts per second.
        if (!player.isReadyForNextKick()) {
            player.getStateMachine().changeState(ChaseBall.getInstance());
        }
        if (Define.def(Define.PLAYER_STATE_INFO_ON)) {
            DbgConsole.debugConsole.print("Player ").print(player.getId()).print(" enters kick state").print("");
        }
    }

    @Override
    public void execute(FieldPlayer player) {
        //calculate the dot product of the vector pointing to the ball and the player's heading
        Vector ToBall = Vector.sub(player.getBall().getPosition(), player.getPosition());
        double dot = player.getHeading().dot(Vector.normalize(ToBall));

        //cannot kick the ball if the goalkeeper is in possession or if it is 
        //behind the player or if there is already an assigned receiver. So just continue chasing the ball
        if (player.getTeam().getReceivingPlayer() != null
            || player.getPitch().getGoalKeeperHasBall()
            || (dot < 0)) 
        {
            if (Define.def(Define.PLAYER_STATE_INFO_ON)) {
                DbgConsole.debugConsole.print("Goaly has ball / ball behind player").print("");
            }
            player.getStateMachine().changeState(ChaseBall.getInstance());
            return;
        }

        /* Attempt a shot at the goal */
        //if a shot is possible, this vector will hold the position along the 
        //opponent's goal line the player should aim for.
        Vector ballTarget = new Vector();

        //the dot product is used to adjust the shooting force. The more
        //directly the ball is ahead, the more forceful the kick
        double power = ParamLoader.getInstance().MaxShootingForce * dot;

        //if it is determined that the player could score a goal from this position
        //OR if he should just kick the ball anyway, the player will attempt to make the shot
        if (player.getTeam().canShoot(
                player.getBall().getPosition(),
                power,
                ballTarget)
                || (Utils.randFloat() < ParamLoader.getInstance().ChancePlayerAttemptsPotShot)) 
        {
            if (Define.def(Define.PLAYER_STATE_INFO_ON)) {
                DbgConsole.debugConsole.print("Player ").print(player.getId()).print(" attempts a shot at ").print(ballTarget).print("");
            }

            //add some noise to the kick. We don't want players who are 
            //too accurate! The amount of noise can be adjusted by altering
            //Prm.PlayerKickingAccuracy
            ballTarget = SoccerBall.addNoiseToKick(player.getBall().getPosition(), ballTarget);

            //this is the direction the ball will be kicked in
            Vector KickDirection = Vector.sub(ballTarget, player.getBall().getPosition());
            player.getBall().kick(KickDirection, power);

            //change state   
            player.getStateMachine().changeState(Wait.getInstance());
            player.findSupport();
            return;
        }


        /* Attempt a pass to a player */

        //if a receiver is found this will point to it
        PlayerBase receiver = null;

        power = ParamLoader.getInstance().MaxPassingForce * dot;
        ObjectRef<PlayerBase> receiverRef = new ObjectRef<PlayerBase>();
        //test if there are any potential candidates available to receive a pass
        if (player.isThreatened()
                && player.getTeam().findPass(player,
                receiverRef,
                ballTarget,
                power,
                ParamLoader.getInstance().MinPassDist)) {
            receiver = receiverRef.get();
            //add some noise to the kick
            ballTarget = SoccerBall.addNoiseToKick(player.getBall().getPosition(), ballTarget);

            Vector kickDirection = Vector.sub(ballTarget, player.getBall().getPosition());
            player.getBall().kick(kickDirection, power);

            if (Define.def(Define.PLAYER_STATE_INFO_ON)) {
                DbgConsole.debugConsole.print("Player ").print(player.getId()).print(" passes the ball with force ").print(StreamUtilityFunction.ttos(power,3)).print("  to player ").print(receiver.getId()).print("  Target is ").print(ballTarget).print("");
            }
            //let the receiver know a pass is coming 
            MessageDispatcher.dispatcher.dispatchMsg(
                    MessageDispatcher.SEND_MSG_IMMEDIATELY,
                    player.getId(),
                    receiver.getId(),
                    MessageType.RECEIVE_BALL,
                    ballTarget);
            //the player should wait at his current position unless instrucedotherwise  
            player.getStateMachine().changeState(Wait.getInstance());
            player.findSupport();
            return;
        } 
        //cannot shoot or pass, so dribble the ball upfield
        else {
            player.findSupport();
            player.getStateMachine().changeState(Dribble.getInstance());
        }
    }

    @Override
    public void exit(FieldPlayer player) {}

    @Override
    public boolean onMessage(FieldPlayer e, final Telegram t) {
        return false;
    }
}
