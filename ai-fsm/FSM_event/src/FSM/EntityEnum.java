
package FSM;

import FSM.MinerStates.MinerStateEnum;
import FSM.WifeStates.WifeStateEnum;

public enum EntityEnum {

    MINER_JOHN(new Miner("Miner John", MinerStateEnum.SLEEP)),
    MINER_BOB(new Miner("Miner Bob", MinerStateEnum.GO_HOME)),
    ELSA(new Wife("Elsa", WifeStateEnum.HOUSE_WORK));
    
    private final GameEntity entity;
    private StateMachine stateMachine;
    
    private EntityEnum(GameEntity entity) {
        this.entity = entity;
        stateMachine = entity.getStateMachine();
    }
    
    public void setStateMachine(StateMachine sm) {
        if (stateMachine == null) {
            stateMachine = sm;
        }
    }

    public GameEntity getEntity() {
        return entity;
    } 
    
    public StateMachine getStateMachine() {
        return stateMachine;
    }
  
}
