
package FSM.WifeStates;

import FSM.State;
import FSM.StateManager;
import FSM.Wife;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WifeStateManager extends StateManager<WifeStateEnum>  {
    private Map<WifeStateEnum, State> stateMap = new HashMap<>();
    
    public WifeStateManager(Wife wife) {
        for(WifeStateEnum stateEnum : WifeStateEnum.values()) {
            State state = stateEnum.get(wife);
            state.setStateEnum(stateEnum);
            this.stateMap.put(stateEnum, state);
        }
    }
    
    public State getState(WifeStateEnum stateEnum) {
        return this.stateMap.get(stateEnum);
    }
    
    @Override
    public Set getStateEnumSet() {
        return this.stateMap.keySet();
    }
    
}
