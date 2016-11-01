
package FSM.MinersWifeStates;

import FSM.Messages.Telegram;
import FSM.MinersWife;
import FSM.State;
import FSM.Utils.Timer;
import java.util.Random;

public class WifesGlobalState extends State<MinersWife> {

    static final WifesGlobalState instance = new WifesGlobalState();

    private WifesGlobalState() {}
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not allowed");
    }

    public static WifesGlobalState getInstance() {
        return instance;
    }

    @Override
    public void enter(MinersWife wife) {}

    @Override
    public void execute(MinersWife wife) {
        if (new Random().nextDouble() < 0.1) {
            wife.getStateMachine().changeState(VisitBathroom.getInstance());
        }
    }

    @Override
    public void exit(MinersWife wife) {}
    
    @Override
    public boolean onMessage(MinersWife wife, Telegram msg) {
        switch (msg.msg) {
            case IM_HOME: {
                System.out.println("Message handled by " + wife.getEntity() + " at time: " + Timer.getInstance().getCurrentTime());
                System.out.println(wife.getEntity() + ": Hi honey. Let me make you some of mah fine country stew");
                wife.getStateMachine().changeState(CookStew.getInstance());
            }
            return true;
        }
        return false;
    }
    
}
