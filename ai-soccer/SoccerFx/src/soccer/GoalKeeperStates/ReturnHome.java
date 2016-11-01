/**
 * 
//------------------------- ReturnHome: ----------------------------------
//
//  In this state the goalkeeper simply returns back to the center of
//  the goal region before changing state back to TendGoal
//------------------------------------------------------------------------
 */
package soccer.GoalKeeperStates;

import soccer.GoalKeeper;
import common.FSM.State;
import common.Messaging.Telegram;

public class ReturnHome extends State<GoalKeeper> {

    private static ReturnHome instance = new ReturnHome();

    private ReturnHome() {}

    public static ReturnHome getInstance() {
        return instance;
    }

    @Override
    public void enter(GoalKeeper keeper) {
        keeper.getSteering().arriveOn();
    }

    @Override
    public void execute(GoalKeeper keeper) {
        keeper.getSteering().setTarget(keeper.getHomeRegion().center());

        //if close enough to home or the opponents get control over the ball, change state to tend goal
        if (keeper.isInHomeRegion() || !keeper.getTeam().isControllingPlayer()) {
            keeper.getStateMachine().changeState(TendGoal.getInstance());
        }
    }

    @Override
    public void exit(GoalKeeper keeper) {
        keeper.getSteering().arriveOff();
    }

    @Override
    public boolean onMessage(GoalKeeper e, final Telegram t) {
        return false;
    }
}
