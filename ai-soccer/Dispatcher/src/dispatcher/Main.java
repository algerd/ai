
package dispatcher;

public class Main {

    public static void main(String[] args) {
      
        Dispatcher dispatcher = new Dispatcher();
        Client alex = new Client(dispatcher, "Alex");
        Client kate = new Client(dispatcher, "Kate");
        
        alex.send("Hello!", kate);
        kate.send("Hi!", alex);
        
    }
 
}
