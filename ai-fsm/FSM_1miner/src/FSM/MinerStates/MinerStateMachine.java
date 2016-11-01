
package FSM.MinerStates;

import FSM.EntityEnum;
import FSM.Miner;
import FSM.State;
import FSM.StateMachine;
import java.util.HashMap;
import java.util.Map;

public class MinerStateMachine extends StateMachine{
    private Miner miner;  
    private State currentState;
    private State previousState;   
    private Map<MinerStateEnum, State> stateMap = new HashMap<>();
    
    private MinerStateMachine() {}
    
    public MinerStateMachine(Miner miner, MinerStateEnum state) {
        this.miner = miner;
        for(MinerStateEnum stateEnum : MinerStateEnum.values()) {
            this.stateMap.put(stateEnum, stateEnum.get(this.miner));
        }
        this.currentState =  this.previousState = getState(state);     
    }
   
    public MinerStateMachine(EntityEnum entity, MinerStateEnum state) { 
        this(new Miner(entity), state);
    }
   
    /**
     * Get new instance of state. 
     */
    protected State getNewState(MinerStateEnum stateEnum) { 
        return stateEnum.get(this.miner);
    }
    
    /**
     * Get state from state map. 
     */
    protected State getState(MinerStateEnum stateEnum) {
        return this.stateMap.get(stateEnum);
    }
    
    /**
     * Reset state to initial state.
     */
    protected void initState(MinerStateEnum stateEnum) {
        this.stateMap.put(stateEnum, stateEnum.get(this.miner));
    }
    
    @Override
    public void update() {
        this.currentState.execute();
        if (this.currentState.exitCondition()){
            for (MinerStateEnum stateEnum : this.currentState.getTransitionArray()) {
                // Try to go to other state.
                State state = getState(stateEnum);
                if (state.enterCondition()){         
                    changeState(state);
                    break;
                }
            }           
        }
    }
    
    @Override
    protected void changeState(State newState) { 
        this.previousState = this.currentState;
        this.currentState.exit();
        this.currentState = newState;
        this.currentState.enter(this.previousState.getLocation());    
    }

    /**
     * change state back to the previous state.
     */
    @Override
    protected void revertToPreviousState() {
        changeState(this.previousState);
    }
 
    @Override
    protected State getCurrentState() {
        return this.currentState;
    }
   
    @Override
    protected State getPreviousState() {
        return this.previousState;
    }
    @Override
    protected void setCurrentState(State s) {
        this.currentState = s;
    }
    
    @Override
    protected void setPreviousState(State s) {
        this.previousState = s;
    }
    
}
