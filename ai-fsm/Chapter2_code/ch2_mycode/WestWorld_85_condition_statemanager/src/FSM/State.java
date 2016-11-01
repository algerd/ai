
package FSM;

public abstract class State {
    
    /**
     * ???
     */
    abstract public Miner getMiner();
    
    /**
     *  
     */
    abstract public boolean enterCondition();
    
    /**
     * Execute when the state is entered.
     */
    abstract public void enter();

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
     * Try to transit to other state.
     */
    abstract public boolean transit();
    
    
    /**
     * Debug methods. 
     */
    public void debugTransition(String state) {
        System.out.println("++++++++ Transition from state " + state + " +++++++++");
    }   
    public void debugEnterCondition(String state) {
        System.out.println("********** Transition to state " + state + " **************");
    }
    
}
