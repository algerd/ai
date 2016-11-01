
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;
import java.util.HashMap;
import java.util.Map;

public class MinerStateManager {
     
    /**
     * Карта состояний хранит состояния с учётом их предыдущего использования.
     */
    private Map<MinerStateEnum, State> stateMap = new HashMap<>();
      
    public MinerStateManager(Miner miner) {    
        for(MinerStateEnum stateEnum : MinerStateEnum.values()) {
            this.stateMap.put(stateEnum, stateEnum.get(miner));
        }       
    }
    
    /**
     * Get virgin state.
     */
    public State getNewState(MinerStateEnum stateEnum, Miner miner) { 
        return stateEnum.get(miner);
    }
    
    /**
     * Вернуть состояние с учётом его предыдущего использования.
     */
    public State getState(MinerStateEnum stateEnum) {
        return this.stateMap.get(stateEnum);
    }
    
    /**
     * Сбросить состояние в начальное.
     */
    public void initState(MinerStateEnum stateEnum) {
        this.stateMap.put(stateEnum, stateEnum.get(this.stateMap.get(stateEnum).getMiner()));
    }
   
}
