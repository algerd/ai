
package dispatcher;

public abstract class AbstrDispatcher {
    
    public abstract void send(String message, AbstrClient sender, AbstrClient receiver);
    
}
