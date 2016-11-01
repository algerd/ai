
package FSM.WifeStates;

import FSM.State;
import FSM.StateManager;
import FSM.Wife;
import java.util.HashMap;
import java.util.Map;

public class WifeStateManager extends StateManager<WifeStateEnum>  {
    private Map<WifeStateEnum, State<WifeStateEnum>> stateMap = new HashMap<>();
    
    public WifeStateManager(Wife wife) {
        for(WifeStateEnum stateEnum : WifeStateEnum.values()) {
            this.stateMap.put(stateEnum, stateEnum.get(wife));
        }    
    }
    
    public State<WifeStateEnum> getState(WifeStateEnum stateEnum) {
        return this.stateMap.get(stateEnum);
    }
    
}
