
package FSM.WifeStates;

import FSM.Event;
import FSM.EventTypeEnum;
import FSM.LocationEnum;
import FSM.State;
import FSM.Wife;

class CookStew extends State<WifeStateEnum> {   
    // флаг для внешнего, принудительного указания входа в состояние (для эвентов)
    private boolean enter;
    private WifeStateEnum stateEnum;
    private Wife wife;
    private boolean isCooking;
    private final WifeStateEnum[] transitionArray = new WifeStateEnum[]{
        WifeStateEnum.HOUSE_WORK   
    };
    
    CookStew(Wife wife) {
        this.wife = wife;
    }
    
    @Override
    public boolean enterCondition() {
        if(isEnter() && !this.isCooking) {    
            setEnter(false);
            return true;
        }
        return false;
    }

    @Override
    public Event enter(State prevState) {
        System.out.println(wife.getName() + ": Putting the stew in the oven");     
        return new Event(EventTypeEnum.ENTER, this, true);
    }

    @Override
    public Event execute() {
        System.out.println(wife.getName() + ": Fussin' over food");
        this.isCooking = true;
        return new Event(EventTypeEnum.EXECUTE, this, true);
    }
    
    @Override
    public boolean exitCondition() {
        return true;
    }

    @Override
    public Event exit() {
        System.out.println(wife.getName() + ": Puttin' the stew on the table");
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
