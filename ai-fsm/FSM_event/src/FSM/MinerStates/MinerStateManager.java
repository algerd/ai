
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;
import FSM.StateManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MinerStateManager extends StateManager<MinerStateEnum> {
    private Map<MinerStateEnum, State> stateMap = new HashMap<>();
    
    public MinerStateManager(Miner miner) {
        for(MinerStateEnum stateEnum : MinerStateEnum.values()) {
            State state = stateEnum.get(miner);
            state.setStateEnum(stateEnum);
            this.stateMap.put(stateEnum, state);
        }    
    }
    
    @Override
    public State getState(MinerStateEnum stateEnum) {
        return this.stateMap.get(stateEnum);
    }
       
    @Override
    public Set getStateEnumSet() {
        return this.stateMap.keySet();
    }
    
}
