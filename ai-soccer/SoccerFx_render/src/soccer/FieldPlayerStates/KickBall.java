
package soccer.FieldPlayerStates;

import common.misc.CppToJava.ObjectRef;
import common.Messaging.Telegram;
import soccer.PlayerBase;
import common.D2.Vector;
import soccer.FieldPlayer;
import soccer.MessageType;
import soccer.ParamLoader;
import soccer.SoccerBall;
import common.FSM.State;
import common.Messaging.MessageDispatcher;
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
    }

    @Override
    public void execute(FieldPlayer player) {
        // обнулить счётчик тактов после удара
        player.resetKickTimer();
        
        //calculate the dot product of the vector pointing to the ball and the player's heading
        Vector toBall = player.getBall().getPosition().subn(player.getPosition());
        double dot = player.getHeading().dot(toBall.normalize());

        //cannot kick the ball if the goalkeeper is in possession or if it is 
        //behind the player or if there is already an assigned receiver. So just continue chasing the ball
        if (player.getTeam().getReceivingPlayer() != null || player.getPitch().getGoalKeeperHasBall() || (dot < 0)) {
            player.getStateMachine().changeState(ChaseBall.getInstance());
            return;
        }

        /* Attempt a shot at the goal */
        //if a shot is possible, this vector will hold the position along the opponent's goal line the player should aim for.
        Vector ballTarget = new Vector();

        //The more directly the ball is ahead, the more forceful the kick
        double power = ParamLoader.getInstance().MaxShootingForce * dot;

        //if it is determined that the player could score a goal from this position
        //OR if he should just kick the ball anyway, the player will attempt to make the shot
        if (player.getTeam().canShoot(
                player.getBall().getPosition(),
                power,
                ballTarget)
                || (Utils.randFloat() < ParamLoader.getInstance().ChancePlayerAttemptsPotShot)) 
        {
            //add some noise to the kick. We don't want players who are 
            //too accurate! The amount of noise can be adjusted by altering
            //Prm.PlayerKickingAccuracy
            ballTarget = SoccerBall.addNoiseToKick(player.getBall().getPosition(), ballTarget);

            //this is the direction the ball will be kicked in
            Vector kickDirection = ballTarget.subn(player.getBall().getPosition());
            player.getBall().kick(kickDirection, power);
            
            //change state   
            player.getStateMachine().changeState(Wait.getInstance());
            player.findSupport();
            return;
        }

        /* Attempt a pass to a player */

        //if a receiver is found this will point to it
        PlayerBase receiver = null;

        power = ParamLoader.getInstance().MaxPassingForce * dot;
        ObjectRef<PlayerBase> receiverRef = new ObjectRef<>();
        //test if there are any potential candidates available to receive a pass
        if (player.isThreatened() && player.getTeam().findPass(
                player,
                receiverRef,
                ballTarget,
                power,
                ParamLoader.getInstance().MinPassDist)) 
        {
            receiver = receiverRef.get();
            //add some noise to the kick
            ballTarget = SoccerBall.addNoiseToKick(player.getBall().getPosition(), ballTarget);

            Vector kickDirection = ballTarget.subn(player.getBall().getPosition());
            player.getBall().kick(kickDirection, power);

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
