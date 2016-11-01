
package FSM;

import FSM.Messages.Telegram;
import FSM.MinersWifeStates.DoHouseWork;
import FSM.MinersWifeStates.WifesGlobalState;

public class MinersWife extends BaseGameEntity {

    private StateMachine<MinersWife> stateMachine;
    private LocationEnum location;

    public MinersWife(EntityEnum entity) {
        super(entity);
        this.location = LocationEnum.SHACK;
        this.stateMachine = new StateMachine<>(this);
        this.stateMachine.setCurrentState(DoHouseWork.getInstance());
        this.stateMachine.setGlobalState(WifesGlobalState.getInstance());
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