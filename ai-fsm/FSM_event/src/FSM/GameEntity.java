
package FSM;

public abstract class GameEntity { 
      
    private String name;
    
    public GameEntity(String name) {
        this.name = name;
    }
      
    public String getName() {
        return name;
    }

    abstract StateMachine getStateMachine(); 
    
}
