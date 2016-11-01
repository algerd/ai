
package SimpleSoccer.GoalKeeperStates;

import SimpleSoccer.GoalKeeper;
import common.FSM.State;
import common.Messaging.Telegram;

public class GlobalKeeperState extends State<GoalKeeper> {

    private static GlobalKeeperState instance = new GlobalKeeperState();

    private GlobalKeeperState() {}

    public static GlobalKeeperState getInstance() {
        return instance;
    }

    @Override
    public void enter(GoalKeeper keeper) {}

    @Override
    public void execute(GoalKeeper keeper) {}

    @Override
    public void exit(GoalKeeper keeper) {}

    @Override
    public boolean onMessage(GoalKeeper keeper, final Telegram telegram) {
        switch (telegram.msg) {
            case GO_HOME:
                keeper.setDefaultHomeRegion();
                keeper.getStateMachine().changeState(ReturnHome.getInstance());         
                break;

            case RECEIVE_BALL:
                keeper.getStateMachine().changeState(InterceptBall.getInstance());           
                break;
        }
        return false;
    }
    
}
