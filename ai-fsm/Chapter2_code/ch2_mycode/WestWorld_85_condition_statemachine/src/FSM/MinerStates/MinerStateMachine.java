
package FSM.MinerStates;

import FSM.EntityEnum;
import FSM.Miner;
import FSM.StateMachine;
import java.util.HashMap;
import java.util.Map;

public class MinerStateMachine implements StateMachine {
    private Miner miner; 
    private MinerState currentState;
    private MinerState previousState;
    private Map<MinerStateEnum, MinerState> stateMap = new HashMap<>();
    
    private MinerStateMachine() {}
    
    public MinerStateMachine(Miner miner, MinerStateEnum state) {
        this.miner = miner;
        for(MinerStateEnum stateEnum : MinerStateEnum.values()) {
            this.stateMap.put(stateEnum, stateEnum.get(this.miner));
        }
        this.currentState = this.previousState = getState(state);     
    } 
   
    public MinerStateMachine(EntityEnum entity, MinerStateEnum state) { 
        this(new Miner(entity), state);
    }
   
    /**
     * Get state from state map. 
     */
    private MinerState getState(MinerStateEnum stateEnum) {
        return this.stateMap.get(stateEnum);
    }
     
    @Override
    public void update() {
        this.currentState.execute();
        if (this.currentState.exitCondition()){
            for (MinerStateEnum stateEnum : this.currentState.getTransitionArray()) {
                // Try to go to other state.
                MinerState state = getState(stateEnum);
                if (state.enterCondition()){         
                    changeState(state);
                    break;
                }
            }           
        }
    }
    
    private void changeState(MinerState newState) { 
        this.previousState = this.currentState;
        this.currentState.exit();
        this.currentState = newState;
        this.currentState.enter(this.previousState);    
    }
    
}
