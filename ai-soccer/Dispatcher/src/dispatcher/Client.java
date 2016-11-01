
package dispatcher;

public class Client extends AbstrClient{
    
    private String name;
    
    public Client(AbstrDispatcher dispatcher, String name) {
        super(dispatcher);            
        this.name = name;
    }
    
    @Override
    public void notify(String message, AbstrClient sender) {
        System.err.println(name + " принял сообщение: " + message + " от " + ((Client)sender).getName());
    }
    
    public String getName() {
        return name;
    }
    
}
