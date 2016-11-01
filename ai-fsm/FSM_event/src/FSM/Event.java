
package FSM;

public class Event<T> {
    
    private EventTypeEnum eventType;
    private State<T> state;
    private boolean handler;
    
    public Event(EventTypeEnum eventType, State<T> state, boolean handler) {
        this.eventType = eventType;
        this.state = state;
        this.handler = handler;
    }

    public EventTypeEnum getEventType() {
        return eventType;
    }

    public void setEventType(EventTypeEnum eventType) {
        this.eventType = eventType;
    }

    public State getState() {
        return state;
    }

    public void setState(State<T> state) {
        this.state = state;
    }

    public boolean isHandler() {
        return handler;
    }

    public void setHandler(boolean handler) {
        this.handler = handler;
    }
      
}
