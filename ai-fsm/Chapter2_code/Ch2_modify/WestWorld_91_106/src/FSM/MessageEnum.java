
package FSM;

public enum MessageEnum {

    IM_HOME("HiHoneyImHome"),
    STEW_READY("StewReady");
    
    private final String msg;
    
    private MessageEnum(String msg) {
        this.msg = msg;
    }
    
    @Override
    public String toString() {
        return this.msg;
    }
      
}