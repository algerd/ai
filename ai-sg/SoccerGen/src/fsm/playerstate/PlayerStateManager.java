
package fsm.playerstate;

import fsm.State;
import fsm.StateManager;
import generator.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerStateManager extends StateManager<PlayerStateEnum> {
    
    private Map<PlayerStateEnum, State> stateMap = new HashMap<>();
    
    public PlayerStateManager(Player player) {
        for(PlayerStateEnum stateEnum : PlayerStateEnum.values()) {
            State state = stateEnum.get(player);
            this.stateMap.put(stateEnum, state);
        }    
    }
    
    @Override
    public State getState(PlayerStateEnum stateEnum) {
        return this.stateMap.get(stateEnum);
    }
       
    @Override
    public Set getStateEnumSet() {
        return this.stateMap.keySet();
    }
    
}
