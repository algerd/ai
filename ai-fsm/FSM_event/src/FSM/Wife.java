
package FSM;

import FSM.WifeStates.WifeStateEnum;
import FSM.WifeStates.WifeStateManager;

public class Wife extends GameEntity {
    
    private WifeStateEnum startStateEnum;

    public Wife(String name, WifeStateEnum startStateEnum) {
        super(name);
        this.startStateEnum = startStateEnum;
    }
    
    @Override
    public StateMachine getStateMachine() {
        return new StateMachine(new WifeStateManager(this), startStateEnum);
    }
 
}
