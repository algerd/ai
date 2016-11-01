
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;
import FSM.StateManager;
import java.util.HashMap;
import java.util.Map;

public class MinerStateManager extends StateManager<MinerStateEnum> {
    private Map<MinerStateEnum, State<MinerStateEnum>> stateMap = new HashMap<>();
    
    public MinerStateManager(Miner miner) {
        for(MinerStateEnum stateEnum : MinerStateEnum.values()) {
            this.stateMap.put(stateEnum, stateEnum.get(miner));
        }    
    }
    
    @Override
    public State<MinerStateEnum> getState(MinerStateEnum stateEnum) {
        return this.stateMap.get(stateEnum);
    }
    
}
