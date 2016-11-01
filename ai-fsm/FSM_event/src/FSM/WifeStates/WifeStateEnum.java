
package FSM.WifeStates;

import FSM.State;
import FSM.Wife;
import java.util.function.Function;

public enum WifeStateEnum {
   
    HOUSE_WORK(HouseWork::new),           // new HouseWork(wife)
    VISIT_BATHROOM(VisitBathroom::new),
    COOK_STEW(CookStew::new);
    
    private final Function<Wife, State> closure;
    
    private WifeStateEnum(Function<Wife, State> callback) {
        this.closure = callback;
    }
    
    /**
     * Get new instance of State abstract class.
     */
    public State get(Wife wife) {     
        return closure.apply(wife);      
    }
    
}
