
package fsm;

public abstract class State {

    /**
     * this will execute when the state is entered.
     */
    abstract public void enter();

    /**
     * this is the state's normal update function.
     */ 
    abstract public void execute();

    /**
     * this will execute when the state is exited.
     */
    abstract public void exit();
 
}
