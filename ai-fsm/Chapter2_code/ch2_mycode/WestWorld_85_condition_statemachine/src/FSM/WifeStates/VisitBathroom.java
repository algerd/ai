
package FSM.WifeStates;

import FSM.Wife;
import java.util.Random;

class VisitBathroom extends WifeState {
    
    private Wife wife;
    private final WifeStateEnum[] transitionArray = new WifeStateEnum[]{
        WifeStateEnum.HOUSE_WORK    
    };
    
    public VisitBathroom() {}
    
    VisitBathroom(Wife wife) {
        this.wife = wife;
    }
    
    @Override
    public boolean enterCondition() {
        //1 in 10 chance of needing the bathroom
        if (new Random().nextDouble() < 0.1) {
            return true;
        }
        return false;
    }

    @Override
    public void enter(WifeState prevState) {
        System.out.println(wife.getEntity() + ": Walkin' to the can. Need to powda mah pretty li'lle nose");
    }

    @Override
    public void execute() {
        System.out.println(wife.getEntity() + ": Ahhhhhh! Sweet relief!");
    }
    
    @Override
    public boolean exitCondition() {
            return true;
    }

    @Override
    public void exit() {
        System.out.println(wife.getEntity() + ": Leavin' the Jon");
    }
    
    @Override
    public WifeStateEnum[] getTransitionArray() {
        return this.transitionArray;
    }
 
}
