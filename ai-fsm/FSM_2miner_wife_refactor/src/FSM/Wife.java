
package FSM;

import FSM.WifeStates.WifeStateEnum;
import FSM.WifeStates.WifeStateManager;

public class Wife extends GameEntity {

    public Wife(EntityEnum entity) {
        super(entity);
    }
    
    public StateMachine getStateMachine(WifeStateEnum state) {
        return new StateMachine(new WifeStateManager(this), state);
    }
   
}
