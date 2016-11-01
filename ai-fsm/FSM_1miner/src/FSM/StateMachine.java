
package FSM;

public abstract class StateMachine {
    /**
     * Call and execute method of state State::execute().
     */
    public abstract void update(); 

    /**
     * Change current state.
     */
    protected abstract void changeState(State s);
       
    /**
     * Change state back to the previous state.
     */
    protected abstract void revertToPreviousState(); 

    protected abstract State getCurrentState();

    protected abstract State getPreviousState();
    
    protected abstract void setCurrentState(State s);

    protected abstract void setPreviousState(State s);
    
}
