
package dispatcher;

public class Dispatcher extends AbstrDispatcher{

    @Override
    public void send(String message, AbstrClient sender, AbstrClient receiver) {
        receiver.notify(message, sender);
    }
    
}
