
package FSM;

import FSM.MinersWifeStates.WifeStateManagerEnum;

public class MinersWife extends BaseGameEntity {

    private StateMachine<MinersWife> stateMachine;
    private LocationEnum location;

    public MinersWife(EntityEnum entity) {
        super(entity);
        this.location = LocationEnum.SHACK;
        this.stateMachine = new StateMachine<>(this);
        this.stateMachine.setCurrentState(WifeStateManagerEnum.DO_HOUSE_WORK.getState());
        this.stateMachine.setGlobalState(WifeStateManagerEnum.WIFES_GLOBAL_STATE.getState());
    }

    @Override
    public void update() {
        this.stateMachine.update();
    }
    
    @Override
    public boolean handleMessage(Telegram msg) {
      return this.stateMachine.HandleMessage(msg);
    }
    
    public StateMachine<MinersWife> getStateMachine() {
        return this.stateMachine;
    }

    public LocationEnum getLocation() {
        return this.location;
    }

    public void setLocation(LocationEnum loc) {
        this.location = loc;
    }
    
}