
package FSM;

public class StateMachine<T> {
    
    private T owner;  
    private State<T> currentState;
    private State<T> previousState;
    private State<T> globalState;

    public StateMachine(T owner) {
        this.owner = owner;
    }

    public void setCurrentState(State<T> s) {
        this.currentState = s;
    }

    public void setGlobalState(State<T> s) {
        this.globalState = s;
    }

    public void setPreviousState(State<T> s) {
        this.previousState = s;
    }

    public void update() {
        if (this.globalState != null) {
            this.globalState.execute(this.owner);
        }
        if (this.currentState != null) {
            this.currentState.execute(this.owner);
        }
    }

    public void changeState(State<T> newState) {
        this.previousState = this.currentState;
        this.currentState.exit(this.owner);
        this.currentState = newState;
        this.currentState.enter(this.owner);
    }

    /**
     * change state back to the previous state.
     */
    public void revertToPreviousState() {
        changeState(this.previousState);
    }

    /**
     * first see if the current state is valid and that it can handle the message,
     * if not, and if a global state has been implemented, send the message to the global state.
     */
    public boolean HandleMessage(Telegram msg) {
        if (this.currentState != null && this.currentState.onMessage(this.owner, msg)) {
            return true;
        }
        if (this.globalState != null && this.globalState.onMessage(this.owner, msg)) {
            return true;
        }
        return false;
    }
    
    /**
     * returns true if the current state's type is equal to the type of the class passed as a parameter.
     */ 
    public boolean isInState(State<T> state) {
        System.out.println(this.currentState.getClass());
        return this.currentState.getClass() == state.getClass();
    }

    public State<T> getCurrentState() {
        return this.currentState;
    }

    public State<T> getGlobalState() {
        return this.globalState;
    }

    public State<T> getPreviousState() {
        return this.previousState;
    }
    
}
