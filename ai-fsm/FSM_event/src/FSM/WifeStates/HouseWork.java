
package FSM.WifeStates;

import FSM.Event;
import FSM.EventTypeEnum;
import FSM.LocationEnum;
import FSM.State;
import FSM.Wife;
import java.util.Random;

public class HouseWork extends State<WifeStateEnum> {
    // флаг для внешнего, принудительного указания входа в состояние (для эвентов)
    private boolean enter;
    private WifeStateEnum stateEnum;
    private Wife wife;
    private final WifeStateEnum[] transitionArray = new WifeStateEnum[]{
        WifeStateEnum.VISIT_BATHROOM,
        WifeStateEnum.COOK_STEW
    };
    
    HouseWork(Wife wife) {
        this.wife = wife;
    }
    
    @Override
    public boolean enterCondition() {
        if(isEnter() || true) {
            setEnter(false);
            return true;
        }
        return false;
    }

    @Override
    public Event enter(State prevState) {
        return new Event(EventTypeEnum.ENTER, this, true);
    }

    @Override
    public Event execute() {
        switch (new Random().nextInt(3)) {
            case 0:
                System.out.println(wife.getName() + ": Moppin' the floor");
                break;
            case 1:
                System.out.println(wife.getName() + ": Washin' the dishes");
                break;
            case 2:
                System.out.println(wife.getName() + ": Makin' the bed");
                break;
        }
        return new Event(EventTypeEnum.EXECUTE, this, true);
    }
    
    @Override
    public boolean exitCondition() {
        return true;
    }

    @Override
    public Event exit() {
        return new Event(EventTypeEnum.EXIT, this, true);
    }
    
    @Override
    public WifeStateEnum[] getTransitionArray() {
        return this.transitionArray;
    }
    
    @Override
    public LocationEnum getLocation() { 
        return null;
    }
    
    @Override
    public boolean isEnter() {
        return enter;
    }
    
    @Override
    public void setEnter(boolean enter) {
        this.enter = enter;
    }
    
    @Override
    public WifeStateEnum getStateEnum() {
        return stateEnum;
    }

    @Override
    public void setStateEnum(WifeStateEnum stateEnum) {
        this.stateEnum = stateEnum;
    }

    @Override
    public Wife getEntity() {
        return wife;
    } 
}
