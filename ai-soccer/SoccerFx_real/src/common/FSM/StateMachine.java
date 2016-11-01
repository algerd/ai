
package common.FSM;

import common.Messaging.Telegram;

public class StateMachine<T> {
    
    private T owner;
    private State<T> currentState;
    //a record of the last state the agent was in
    private State<T> previousState;
    //this is called every time the FSM is updated
    private State<T> globalState;

    public StateMachine(T owner) {
        this.owner = owner;      
    }

    public void update() {
        
        //if a global state exists, call its execute method, else do nothing
        if (globalState != null) {
            globalState.execute(owner);
        }
        //same for the current state
        if (currentState != null) {
            currentState.execute(owner);
        }   
        
    }

    public boolean handleMessage(Telegram msg) {
        //first see if the current state is valid and that it can handle the message
        if (currentState != null && currentState.onMessage(owner, msg)) {
            return true;
        }
        //if not, and if a global state has been implemented, send the message to the global state      
        return globalState != null && globalState.onMessage(owner, msg);
    }

    //change to a new state
    public void changeState(State<T> pNewState) {       
        //keep a record of the previous state
        previousState = currentState;
        //call the exit method of the existing state
        currentState.exit(owner);
        //change state to the new state
        currentState = pNewState;
        //call the entry method of the new state
        currentState.enter(owner);
    }

    //change state back to the previous state
    public void revertToPreviousState() {
        changeState(previousState);
    }
    
    //only ever used during debugging to grab the name of the current state
    public String GetNameOfCurrentState() {
        String [] s = currentState.getClass().getName().split("\\.");       
        return (s.length > 0) ? s[s.length-1] : currentState.getClass().getName();
    }

    //returns true if the current state's type is equal to the type of the class passed as a parameter. 
    public boolean isInState(State<T> st) {
        return currentState.getClass() == st.getClass();
    }
      
    /**
     ***************************** Getters & Setters *********************************.
     */
    public void setCurrentState(State<T> s) {
        currentState = s;
    }

    public void setGlobalState(State<T> s) {
        globalState = s;
    }

    public void setPreviousState(State<T> s) {
        previousState = s;
    }

    public State<T> getCurrentState() {
        return currentState;
    }

    public State<T> getGlobalState() {
        return globalState;
    }

    public State<T> getPreviousState() {
        return previousState;
    }
          
}
