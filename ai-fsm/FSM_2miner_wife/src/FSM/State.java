
package FSM;

public abstract class State {
    
    /**
     * This is condition for enter to state.
     */
    public abstract boolean enterCondition();
    
    /**
     * Execute when the state is entered.
     */
    //public abstract void enter();

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
     * Debug method. 
     */  
    public void debugEnterCondition(String state) {
        System.out.println("********** Transition to state " + state + " **************");
    }
            
}
