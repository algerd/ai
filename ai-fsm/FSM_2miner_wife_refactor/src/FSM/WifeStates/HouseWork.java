
package FSM.WifeStates;

import FSM.LocationEnum;
import FSM.State;
import FSM.Wife;
import java.util.Random;

public class HouseWork extends State<WifeStateEnum> {
  
    private Wife wife;
    private final WifeStateEnum[] transitionArray = new WifeStateEnum[]{
        WifeStateEnum.VISIT_BATHROOM    
    };
  
    private HouseWork() {}
    
    HouseWork(Wife wife) {
        this.wife = wife;
    }
    
    @Override
    public boolean enterCondition() {
        return true;
    }

    @Override
    public void enter(State prevState) {}

    @Override
    public void execute() {
        switch (new Random().nextInt(3)) {
            case 0:
                System.out.println(wife.getEntity() + ": Moppin' the floor");
                break;
            case 1:
                System.out.println(wife.getEntity() + ": Washin' the dishes");
                break;
            case 2:
                System.out.println(wife.getEntity() + ": Makin' the bed");
                break;
        }
    }
    
    @Override
    public boolean exitCondition() {
        return true;
    }

    @Override
    public void exit() {}
    
    @Override
    public WifeStateEnum[] getTransitionArray() {
        return this.transitionArray;
    }
    
    public LocationEnum getLocation() { 
        return null;
    } 
       
}
