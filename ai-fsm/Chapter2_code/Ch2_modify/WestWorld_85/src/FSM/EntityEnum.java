
package FSM;

public enum EntityEnum {

    // without switch 
    MINER_BOB("Miner Bob"),
    ELSA("Elsa");
    
    private final String name;
    
    private EntityEnum(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    /*
    // with switch
    MINER_BOB,
    ELSA;

    public static String getName(EntityEnum entity) {
        switch (entity) {
            case MINER_BOB:
                return "Miner Bob";
            case ELSA:
                return "Elsa";
            default:
                return "UNKNOWN!";    
        }
    }
    
    @Override
    public String toString() {
        return EntityEnum.getName(this);
    }
    */
    
}
