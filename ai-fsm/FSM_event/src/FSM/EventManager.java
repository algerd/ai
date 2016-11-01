package FSM;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class EventManager<T> {
    
    private SortedSet<HandlerEvent<T>> handlerSet = new TreeSet<>();    
    // очередь выполнения задержанных обработчиков
    private SortedSet<HandlerEvent<T>> delayedHandlerSet = new TreeSet<>();
    
    public EventManager() {}
    
    /**
     * Добавление Обработчиков событий.
     */
    public HandlerEvent addHandler(HandlerEvent handler) {
        handlerSet.add(handler);
        return handler;
    } 
    public HandlerEvent addHandler(EventTypeEnum eventType, T state, Runnable callback) {
        return addHandler(new HandlerEvent(eventType, state, callback));     
    }
    public HandlerEvent addHandler(EventTypeEnum eventType, T state, Runnable callback, boolean remove) {
        return addHandler(new HandlerEvent(eventType, state, callback, remove));     
    }
    public HandlerEvent addHandler(EventTypeEnum eventType, T state, Runnable callback, boolean remove, int priority) {
        return addHandler(new HandlerEvent(eventType, state, callback, remove, priority));     
    }
    public HandlerEvent addHandler(EventTypeEnum eventType, T state, Runnable callback, boolean remove, int priority, int delay) {
        return addHandler(new HandlerEvent(eventType, state, callback, remove, priority, delay));     
    }
    
    /**
     * Удаление обработчика из набора.
     */
    public void removeHandler(HandlerEvent handler) {
        if (handlerSet.contains(handler)) {
            handlerSet.remove(handler);
        }
    }
    
    /**
     * Активация и удаление Обработчиков событий.
     */
    public void handleEvent(Event event) {
        Iterator<HandlerEvent<T>> handlerIterator = handlerSet.iterator();       
        while(handlerIterator.hasNext()) {
            HandlerEvent handler = handlerIterator.next();
            if ((handler.getEventType() == event.getEventType()) && (handler.getState() == event.getState().getStateEnum())) {
                if (handler.getDelay() <= 0) {
                    handler.getClosure().run();                   
                    System.out.println(handler);  //debug
                } 
                else {
                    delayedHandlerSet.add(handler.clone());
                }
                if (handler.isRemove()) {
                    handlerIterator.remove();
                }
            }
        }      
    } 
    
    /**
     * Активация задержанных обработчиков.
     */
    public void runDelayedHandlers() {
        Iterator<HandlerEvent<T>> handlerIterator = delayedHandlerSet.iterator();
        while(handlerIterator.hasNext()) {
            HandlerEvent handler = handlerIterator.next();
            if (handler.getDelay() <= 0 ) {
                handler.getClosure().run();
                System.out.println(handler);    //debug:
                handlerIterator.remove();
            } 
            else {
                handler.decreaseDelay();
            }        
        }     
    }
        
}

