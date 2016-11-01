
package FSM;

public enum EntityEnum {

    MINER_BOB("Miner Bob"),
    ELSA("Elsa");
    
    private final String name;
    
    private EntityEnum(String name) {
        this.name = name;
    }
   
    @Override
    public String toString() {
        return this.name;
    }

}
