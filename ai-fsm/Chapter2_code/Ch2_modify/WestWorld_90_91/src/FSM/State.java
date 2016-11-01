
package FSM;

public abstract class State<T> {

    /**
     * this will execute when the state is entered.
     */
    abstract public void enter(T var);

    /**
     * this is the state's normal update function.
     */
    abstract public void execute(T var);

    /** 
     * this will execute when the state is exited. 
     */
    abstract public void exit(T var);
    
}
