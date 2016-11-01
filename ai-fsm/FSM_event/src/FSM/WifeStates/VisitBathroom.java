
package FSM.WifeStates;

import FSM.Event;
import FSM.EventTypeEnum;
import FSM.LocationEnum;
import FSM.State;
import FSM.Wife;
import java.util.Random;

class VisitBathroom extends State<WifeStateEnum> {
    // флаг для внешнего, принудительного указания входа в состояние (для эвентов)
    private boolean enter;
    private WifeStateEnum stateEnum;
    private Wife wife;
    private final WifeStateEnum[] transitionArray = new WifeStateEnum[]{
        WifeStateEnum.HOUSE_WORK,
        WifeStateEnum.COOK_STEW
    };
    
    VisitBathroom(Wife wife) {
        this.wife = wife;
    }
    
    @Override
    public boolean enterCondition() {
        //1 in 10 chance of needing the bathroom
        if (isEnter() || new Random().nextDouble() < 0.1) {
            setEnter(false);
            return true;
        }
        return false;
    }

    @Override
    public Event enter(State prevState) {
        System.out.println(wife.getName() + ": Walkin' to the can. Need to powda mah pretty li'lle nose");
        return new Event(EventTypeEnum.ENTER, this, true);
    }

    @Override
    public Event execute() {
        System.out.println(wife.getName() + ": Ahhhhhh! Sweet relief!");
        return new Event(EventTypeEnum.EXECUTE, this, true);
    }
    
    @Override
    public boolean exitCondition() {
            return true;
    }

    @Override
    public Event exit() {
        System.out.println(wife.getName() + ": Leavin' the Jon");
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
