
package FSM;

public abstract class StateManager<T> {
    
    public abstract State getState(T stateEnum);
    
}
