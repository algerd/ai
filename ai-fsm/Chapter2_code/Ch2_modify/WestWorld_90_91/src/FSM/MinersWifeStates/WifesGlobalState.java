
package FSM.MinersWifeStates;

import FSM.MinersWife;
import FSM.State;
import FSM.Utils.Utils;

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
    public void enter(MinersWife wife) {
    }

    @Override
    public void execute(MinersWife wife) {
        //1 in 10 chance of needing the bathroom
        if (Utils.RandFloat() < 0.1) {
            wife.getStateMachine().changeState(VisitBathroom.getInstance());
        }
    }

    @Override
    public void exit(MinersWife wife) {}
    
}