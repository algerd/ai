
package FSM.WifeStates;

import FSM.EntityEnum;
import FSM.Wife;
import FSM.StateMachine;
import java.util.HashMap;
import java.util.Map;

public class WifeStateMachine implements StateMachine{
    private Wife wife;  
    private WifeState currentState;
    private WifeState previousState;
    private Map<WifeStateEnum, WifeState> stateMap = new HashMap<>();
    
    private WifeStateMachine() {}
    
    public WifeStateMachine(Wife wife, WifeStateEnum state) {
        this.wife = wife;
        for(WifeStateEnum stateEnum : WifeStateEnum.values()) {
            this.stateMap.put(stateEnum, stateEnum.get(this.wife));
        }
        this.currentState =  this.previousState = getState(state);     
    }
   
    public WifeStateMachine(EntityEnum entity, WifeStateEnum state) { 
        this(new Wife(entity), state);
    }
      
    /**
     * Get state from state map. 
     */
    private WifeState getState(WifeStateEnum stateEnum) {
        return this.stateMap.get(stateEnum);
    }
       
    @Override
    public void update() {
        this.currentState.execute();
        if (this.currentState.exitCondition()){
            for (WifeStateEnum stateEnum : this.currentState.getTransitionArray()) {
                // Try to go to other state.
                WifeState state = getState(stateEnum);
                if (state.enterCondition()){         
                    this.previousState = this.currentState;
                    this.currentState.exit();
                    this.currentState = state;
                    this.currentState.enter(this.previousState); 
                    break;
                }
            }           
        }
    }
      
}
