
package fsm;

import java.util.Set;

public abstract class StateManager<T> {
    
    public abstract State getState(T stateEnum);
    
    public abstract Set getStateEnumSet();
    
}
