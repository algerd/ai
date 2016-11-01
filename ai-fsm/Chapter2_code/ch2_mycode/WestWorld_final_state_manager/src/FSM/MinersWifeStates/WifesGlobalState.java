
package FSM.MinersWifeStates;

import FSM.Telegram;
import FSM.MinersWife;
import FSM.State;
import FSM.Utils.Timer;
import java.util.Random;

class WifesGlobalState extends State<MinersWife> {
    public WifesGlobalState() {}

    @Override
    public void enter(MinersWife wife) {}

    @Override
    public void execute(MinersWife wife) {
        if (new Random().nextDouble() < 0.1) {
            wife.getStateMachine().changeState(WifeStateManagerEnum.VISIT_BATHROOM.getState());
        }
    }

    @Override
    public void exit(MinersWife wife) {}
    
    @Override
    public boolean onMessage(MinersWife wife, Telegram msg) {
        switch (msg.getMsg()) {
            case IM_HOME: {
                System.out.println("Message handled by " + wife.getEntity() + " at time: " + Timer.getInstance().getCurrentTime());
                System.out.println(wife.getEntity() + ": Hi honey. Let me make you some of mah fine country stew");
                wife.getStateMachine().changeState(WifeStateManagerEnum.COOK_STEW.getState());
            }
            return true;
        }
        return false;
    }
    
}
