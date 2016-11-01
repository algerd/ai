
package soccer.FieldPlayerStates;

import common.D2.Vector;
import soccer.ParamLoader;
import soccer.FieldPlayer;
import common.FSM.State;
import common.Messaging.Telegram;
import common.misc.Utils;

public class ReceiveBall extends State<FieldPlayer> {

    private static ReceiveBall instance = new ReceiveBall();

    private ReceiveBall() {}

    public static ReceiveBall getInstance() {
        return instance;
    }

    @Override
    public void enter(FieldPlayer player) {
        //let the team know this player is receiving the ball
        player.getTeam().setReceivingPlayer(player);

        //this player is also now the controlling player
        player.getTeam().setControllingPlayer(player);

        //there are two types of receive behavior. One uses arrive to direct
        //the receiver to the position sent by the passer in its telegram. The
        //other uses the pursuit behavior to pursue the ball. 
        //This statement selects between them dependent on the probability
        //ChanceOfUsingArriveTypeReceiveBehavior, whether or not an opposing
        //player is close to the receiving player, and whether or not the receiving
        //player is in the opponents 'hot region' (the third of the pitch closest
        //to the opponent's goal
        final double PassThreatRadius = 70.0;

        if ((player.isInHotRegion()
            || Utils.randFloat() < ParamLoader.getInstance().ChanceOfUsingArriveTypeReceiveBehavior)
            && !player.getTeam().isOpponentWithinRadius(player.getPosition(), PassThreatRadius)) 
        {
            player.getSteering().arriveOn();       
        } 
        else {
            player.getSteering().pursuitOn();          
        }
    }

    @Override
    public void execute(FieldPlayer player) {
        //if the ball comes close enough to the player or if his team lose control
        //he should change state to chase the ball
        if (player.isBallWithinReceivingRange() || !player.getTeam().isControllingPlayer()) {
            player.getStateMachine().changeState(ChaseBall.getInstance());
            return;
        }
        if (player.getSteering().pursuitIsOn()) {
            player.getSteering().setTarget(player.getBall().getPosition());
        }

        //if the player has 'arrived' at the steering target he should wait and turn to face the ball
        if (player.isAtTarget()) {
            player.getSteering().arriveOff();
            player.getSteering().pursuitOff();
            player.trackBall();
            player.setVelocity(new Vector(0, 0));
        }
    }

    @Override
    public void exit(FieldPlayer player) {
        player.getSteering().arriveOff();
        player.getSteering().pursuitOff();
        player.getTeam().setReceivingPlayer(null);
    }

    @Override
    public boolean onMessage(FieldPlayer e, final Telegram t) {
        return false;
    }
}
