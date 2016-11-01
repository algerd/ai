
package FSM;

import FSM.MinerStates.MinerStateEnum;

public abstract class State {
    
    /**
     * This is condition for enter to state.
     */
    abstract public boolean enterCondition();
    
    /**
     * Execute when the state is entered.
     */
    abstract public void enter(LocationEnum prevLocation);

    /**
     * This is the state's normal update function.
     */
    abstract public void execute();

    /**
     * This is condition for exit from state.
     */
    abstract public boolean exitCondition();
    
    /** 
     * Execute when the state is exited. 
     */
    abstract public void exit();
       
    /**
     * Get array MinerStateEnum for transition to new state.
     */
    abstract public MinerStateEnum[] getTransitionArray();
    
    /**
     * Get location of state.
     */
    abstract public LocationEnum getLocation();
    
    
    /**
     * Debug method. 
     */  
    public void debugEnterCondition(String state) {
        System.out.println("********** Transition to state " + state + " **************");
    }
    
}
