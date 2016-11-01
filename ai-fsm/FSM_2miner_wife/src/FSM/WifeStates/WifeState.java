
package FSM.WifeStates;

import FSM.State;

abstract class WifeState extends State {
    
    /**
     * Execute when the state is entered.
     */
    abstract public void enter(WifeState prevState);
   
    /**
     * Get array MinerStateEnum for transition to new state.
     */
    abstract public WifeStateEnum[] getTransitionArray();
     
}
