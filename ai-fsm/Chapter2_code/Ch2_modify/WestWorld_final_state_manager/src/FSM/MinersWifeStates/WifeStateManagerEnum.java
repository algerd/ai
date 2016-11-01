
package FSM.MinersWifeStates;

import FSM.MinersWife;
import FSM.State;

public enum WifeStateManagerEnum {
    
    COOK_STEW(new CookStew()),
    DO_HOUSE_WORK(new DoHouseWork()),
    VISIT_BATHROOM(new VisitBathroom()),
    WIFES_GLOBAL_STATE(new WifesGlobalState());
     
    private final State<MinersWife> state;
    
    private WifeStateManagerEnum(State<MinersWife> state) {
        this.state = state;
    }
    
    public State<MinersWife> getState() {
        return this.state;
    }
    
}
