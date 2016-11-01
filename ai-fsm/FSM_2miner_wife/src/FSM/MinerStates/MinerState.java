
package FSM.MinerStates;

import FSM.LocationEnum;
import FSM.State;

abstract class MinerState extends State {
     
    /**
     * Execute when the state is entered.
     */
    abstract public void enter(MinerState prevState);
        
    /**
     * Get array MinerStateEnum for transition to new state.
     */
    abstract public MinerStateEnum[] getTransitionArray();
    
    /**
     * Get location of state.
     */
    abstract public LocationEnum getLocation();
     
}
