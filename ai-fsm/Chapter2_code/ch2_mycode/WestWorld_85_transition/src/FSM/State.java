
package FSM;

public abstract class State<T> {

    /**
     * Execute when the state is entered.
     */
    abstract public void enter(T var);

    /**
     * This is the state's normal update function.
     */
    abstract public void execute(T var);

    /** 
     * Execute when the state is exited. 
     */
    abstract public void exit(T var);
    
    /**
     * Try to transit to other state.
     */
    abstract public boolean transit(T var);
    
    /**
     * This is condition for exit from state.
     */
    abstract public boolean exitCondition(T var);
    
    
    public void debugTransition(String state) {
        System.out.println("++++++++ Transition from state " + state + " +++++++++");
    }
    
}
