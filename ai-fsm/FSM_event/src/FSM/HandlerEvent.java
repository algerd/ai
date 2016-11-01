
package FSM;

public class HandlerEvent<T> implements Comparable, Cloneable   {
    
    private EventTypeEnum eventType;
    private T state;
    private Runnable closure;
    private boolean remove = true;
    private int priority; 
    /**
     * Задержка запуска обработчика = количество тактов цикла выполнения приложения.
     * Не использовать время, потому что для разных компов в зависимости от их производительности количество тактов в секунду будет разным
     * и поэтому выполнение программы будет зависеть не от логики программы, а от компьютера.
     */
    private int delay;
     
    public HandlerEvent(EventTypeEnum eventType, T state, Runnable callback) {
        this.eventType = eventType;
        this.state = state;
        this.closure = callback;
    }
    public HandlerEvent(EventTypeEnum eventType, T state, Runnable callback, boolean remove) {
        this(eventType, state, callback);
        this.remove = remove;    
    }
    public HandlerEvent(EventTypeEnum eventType, T state, Runnable callback, boolean remove, int priority) {
        this(eventType, state, callback, remove);
        this.priority = priority;     
    }
    public HandlerEvent(EventTypeEnum eventType, T state, Runnable callback, boolean remove, int priority, int delay) {
        this(eventType, state, callback, remove, priority);
        this.delay = delay;       
    }
    
    @Override
	public HandlerEvent<T> clone() {
		HandlerEvent<T> copy = null;
		try {
			copy = (HandlerEvent<T>)super.clone();
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return copy;
	}
    
    @Override
    public int compareTo(Object obj) {
         HandlerEvent<T> handler = (HandlerEvent<T>)obj;
        if (this == handler) {
            return 0;
        } else {
            return (getPriority() > handler.getPriority()) ? -1 : 1;
        }
    }
    
    @Override
    public String toString() {
        return "EventType=" + eventType +
                " StateType=" + state +
                " Remove=" + remove +
                " Priority=" + priority +
                " Delay=" + delay;
    }

    public void decreaseDelay() {
        --this.delay;
    }
    
    public EventTypeEnum getEventType() {
        return eventType;
    }
    public void setEventType(EventTypeEnum eventType) {
        this.eventType = eventType;
    }

    public T getState() {
        return state;
    }
    public void setState(T state) {
        this.state = state;
    }

    public Runnable getClosure() {
        return closure;
    }
    public void setClosure(Runnable closure) {
        this.closure = closure;
    }

    public boolean isRemove() {
        return remove;
    }
    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getDelay() {
        return delay;
    }
    public void setDelay(int delay) {
        this.delay = delay;
    }
   
}
