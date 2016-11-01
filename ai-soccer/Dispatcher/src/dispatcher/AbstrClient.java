
package dispatcher;

public abstract class AbstrClient {
    
    protected AbstrDispatcher dispatcher;

    public AbstrClient(AbstrDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * Отправка сообщения посредством посредника.
     */
    public void send(String message, AbstrClient receiver) {
        dispatcher.send(message, this, receiver);
    }

    /**
     * Обработка полученного сообщения реализуется каждым конкретным наследником.
     */
    public abstract void notify(String message, AbstrClient sender);
    
}
