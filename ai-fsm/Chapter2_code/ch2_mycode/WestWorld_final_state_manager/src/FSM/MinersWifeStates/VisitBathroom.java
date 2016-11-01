
package FSM.MinersWifeStates;

import FSM.Telegram;
import FSM.MinersWife;
import FSM.State;

class VisitBathroom extends State<MinersWife> {
    public VisitBathroom() {}

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
    
    @Override
    public boolean onMessage(MinersWife wife, Telegram msg) {
        return false;
    }
}