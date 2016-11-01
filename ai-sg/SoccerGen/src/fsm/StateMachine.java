
package fsm;

import generator.Updatable;

public class StateMachine<T> implements Updatable {
    
    private State state;
    private final StateManager stateManager;
    
    // stateEnum для задания стартового состояния персонажа
    public StateMachine(StateManager sm, T stateEnum) {
        stateManager = sm;
        state = stateManager.getState(stateEnum);
        state.enter();
    }
        
    @Override
    public void update() {
        state.execute();        
    }

    public void changeState(T stateEnum) {       
        state.exit();
        state = stateManager.getState(stateEnum);
        state.enter();
    }
    
    public String getNameOfCurrentState() {
        String [] s = state.getClass().getName().split("\\.");       
        return (s.length > 0) ? s[s.length-1] : state.getClass().getName();
    }
    
    public boolean isInState(State st) {
        return state.getClass() == st.getClass();
    }

    public State getState() {
        return state;
    }

    public StateManager getStateManager() {
        return stateManager;
    }
             
}
