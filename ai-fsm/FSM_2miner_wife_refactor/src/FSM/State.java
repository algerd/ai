
package FSM;

public abstract class State<T> {
    
    /**
     * This is condition for enter to state.
     */
    public abstract boolean enterCondition();
    
    /**
     * Execute when the state is entered.
     */
    public abstract void enter(State prevState);

    /**
     * This is the state's normal update function.
     */
    public abstract void execute();

    /**
     * This is condition for exit from state.
     */
    public abstract boolean exitCondition();
    
    /** 
     * Execute when the state is exited. 
     */
    public abstract void exit();
          
    /**
     * Get array MinerStateEnum for transition to new state.
     */
    abstract public T[] getTransitionArray();
    
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
