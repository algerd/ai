/**
 *  In this state the GP will attempt to intercept the ball using the
 *  pursuit steering behavior, but he only does so so long as he remains
 *  within his home region.
 */
package SimpleSoccer.GoalKeeperStates;

import SimpleSoccer.GoalKeeper;
import common.FSM.State;
import common.Messaging.Telegram;

public class InterceptBall extends State<GoalKeeper> {

    private static InterceptBall instance = new InterceptBall();

    private InterceptBall() {}

    //this is a singleton
    public static InterceptBall getInstance() {
        return instance;
    }

    @Override
    public void enter(GoalKeeper keeper) {
        keeper.getSteering().pursuitOn();
    }

    @Override
    public void execute(GoalKeeper keeper) {
        //if the goalkeeper moves to far away from the goal he should return to his
        //home region UNLESS he is the closest player to the ball, in which case,
        //he should keep trying to intercept it.
        if (keeper.isTooFarFromGoalMouth() && !keeper.isClosestPlayerOnPitchToBall()) {
            keeper.getStateMachine().changeState(ReturnHome.getInstance());
            return;
        }

        //if the ball becomes in range of the goalkeeper's hands he traps the 
        //ball and puts it back in play
        if (keeper.isBallWithinKeeperRange()) {
            keeper.getBall().trap();
            keeper.getPitch().setGoalKeeperHasBall(true);
            keeper.getStateMachine().changeState(PutBallBackInPlay.getInstance());
            return;
        }
    }

    @Override
    public void exit(GoalKeeper keeper) {
        keeper.getSteering().pursuitOff();
    }

    @Override
    public boolean onMessage(GoalKeeper e, final Telegram t) {
        return false;
    }
}
