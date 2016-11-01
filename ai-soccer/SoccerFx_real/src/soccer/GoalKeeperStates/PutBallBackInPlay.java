
package soccer.GoalKeeperStates;

import soccer.GoalKeeper;
import soccer.MessageType;
import soccer.PlayerBase;
import common.D2.Vector;
import common.FSM.State;
import common.Messaging.Telegram;
import common.Messaging.MessageDispatcher;
import java.util.concurrent.atomic.AtomicReference;
import soccer.Params;

public class PutBallBackInPlay extends State<GoalKeeper> {

    private static PutBallBackInPlay instance = new PutBallBackInPlay();

    private PutBallBackInPlay() {}

    public static PutBallBackInPlay getInstance() {
        return instance;
    }

    @Override
    public void enter(GoalKeeper keeper) {
        //let the team know that the keeper is in control
        keeper.getTeam().setControllingPlayer(keeper);
        //send all the players home
        keeper.getTeam().getOpponent().returnAllFieldPlayersToHome();
        keeper.getTeam().returnAllFieldPlayersToHome();
    }

    @Override
    public void execute(GoalKeeper keeper) {
        PlayerBase receiver = null;
        Vector ballTarget = new Vector();
        AtomicReference<PlayerBase> receiverRef = new AtomicReference<>();

        //test if there are players further forward on the field we might be able to pass to. If so, make a pass.
        if (keeper.getTeam().findPass(keeper,
                receiverRef,
                ballTarget,
                Params.BALL_VELOCITY_PASS_TACT,
                Params.GOALKEEPER_MIN_PASS_DIST_SQ)) {
            receiver = receiverRef.get();
            //make the pass  
            keeper.getBall().setInitialVelocity(ballTarget.subn(keeper.getBall().getPosition()).normalize(), Params.BALL_VELOCITY_PASS_TACT);
            
            //goalkeeper no longer has ball 
            keeper.getPitch().setGoalKeeperHasBall(false);

            //let the receiving player know the ball's comin' at him
            MessageDispatcher.dispatcher.dispatchMsg(
                    MessageDispatcher.SEND_MSG_IMMEDIATELY,
                    keeper.getId(),
                    receiver.getId(),
                    MessageType.RECEIVE_BALL,
                    ballTarget);

            //go back to tending the goal   
            keeper.getStateMachine().changeState(TendGoal.getInstance());
            return;
        }
        keeper.setVelocity(new Vector());
    }

    @Override
    public void exit(GoalKeeper keeper) {}

    @Override
    public boolean onMessage(GoalKeeper e, final Telegram t) {
        return false;
    }
}
