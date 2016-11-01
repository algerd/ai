
package SimpleSoccer.GoalKeeperStates;

import SimpleSoccer.GoalKeeper;
import SimpleSoccer.ParamLoader;
import SimpleSoccer.MessageType;
import SimpleSoccer.PlayerBase;
import common.D2.Vector;
import common.FSM.State;
import common.Messaging.Telegram;
import common.Messaging.MessageDispatcher;
import common.misc.CppToJava.ObjectRef;

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
        Vector BallTarget = new Vector();
        ObjectRef<PlayerBase> receiverRef = new ObjectRef<>(receiver);

        //test if there are players further forward on the field we might
        //be able to pass to. If so, make a pass.
        if (keeper.getTeam().findPass(
                keeper,
                receiverRef,
                BallTarget,
                ParamLoader.getInstance().MaxPassingForce,
                ParamLoader.getInstance().GoalkeeperMinPassDist)) {
            receiver = receiverRef.get();
            //make the pass   
            keeper.getBall().kick(Vector.normalize(Vector.sub(BallTarget, keeper.getBall().getPosition())),
                    ParamLoader.getInstance().MaxPassingForce);

            //goalkeeper no longer has ball 
            keeper.getPitch().setGoalKeeperHasBall(false);

            //let the receiving player know the ball's comin' at him
            MessageDispatcher.dispatcher.dispatchMsg(
                    MessageDispatcher.SEND_MSG_IMMEDIATELY,
                    keeper.getId(),
                    receiver.getId(),
                    MessageType.RECEIVE_BALL,
                    BallTarget);

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
