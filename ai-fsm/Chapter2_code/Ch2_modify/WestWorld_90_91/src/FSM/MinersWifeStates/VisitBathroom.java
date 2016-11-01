
package FSM.MinersWifeStates;

import FSM.MinersWife;
import FSM.State;

public class VisitBathroom extends State<MinersWife> {

    static final VisitBathroom instance = new VisitBathroom();

    private VisitBathroom() {}

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not allowed");
    }

    public static VisitBathroom getInstance() {
        return instance;
    }

    @Override
    public void enter(MinersWife wife) {
        System.out.println(wife.getEntity() + ": Walkin' to the can. Need to powda mah pretty li'lle nose");
    }

    @Override
    public void execute(MinersWife wife) {
        wife.getStateMachine().revertToPreviousState();
        System.out.println(wife.getEntity() + ": Ahhhhhh! Sweet relief!");
    }

    @Override
    public void exit(MinersWife wife) {
        System.out.println(wife.getEntity() + ": Leavin' the Jon");
    }
}