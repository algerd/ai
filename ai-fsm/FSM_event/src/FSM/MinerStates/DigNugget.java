
package FSM.MinerStates;

import FSM.Event;
import FSM.EventTypeEnum;
import FSM.Miner;
import FSM.LocationEnum;
import FSM.State;

class DigNugget extends State<MinerStateEnum> {
    // флаг для внешнего, принудительного указания входа в состояние (для эвентов)
    private boolean enter;
    private MinerStateEnum stateEnum;  
    private Miner miner;  
    private final LocationEnum location = LocationEnum.GOLDMINE;
    private final MinerStateEnum[] transitionArray = new MinerStateEnum[]{
        MinerStateEnum.GO_HOME,
        MinerStateEnum.QUENCH_THIRST,
        MinerStateEnum.VISIT_BANK    
    };
       
    DigNugget(Miner miner) {
        this.miner = miner;
    }
       
    @Override
    public boolean enterCondition() {
        if (isEnter() || (!miner.isPocketsFull() && !miner.isEnoughMoney())) {
            //debugEnterCondition("DigNugget: goldCarried = " + miner.getGoldCarried());
            setEnter(false);
            return true;
        } 
        return false;
    }
   
    @Override
    public Event enter(State prevState) {
        Event event = new Event(EventTypeEnum.ENTER, this, false);
        if (prevState.getLocation() != this.location) {
            System.out.println(miner.getName() + ": " + "Walkin' to the goldmine");
            event.setHandler(true);
            return event;    
        }
        return event;
    }

    @Override
    public Event execute() {
        System.out.println(miner.getName() + ": " + "Pickin' up a nugget");
        miner.setGoldCarried(miner.getGoldCarried() + 1);
        miner.setFatigue(miner.getFatigue() + 2);
        miner.setThirst(miner.getThirst() + 2);
        miner.setHunger(miner.getHunger() + 2);
        return new Event(EventTypeEnum.EXECUTE, this, true);
    }

    @Override
    public boolean exitCondition(){
        return miner.isPocketsFull();
    }
    
    @Override
    public Event exit() {
        System.out.println(miner.getName() + ": " + "Ah'm leavin' the goldmine with mah pockets full o' sweet gold");
        return new Event(EventTypeEnum.EXIT, this, true);
    }
    
    @Override
    public MinerStateEnum[] getTransitionArray() {
        return this.transitionArray;
    }
    
    @Override
    public LocationEnum getLocation() {
        return this.location;
    }
    
    @Override
    public boolean isEnter() {
        return enter;
    }
    
    @Override
    public void setEnter(boolean enter) {
        this.enter = enter;
    }
 
    @Override
    public MinerStateEnum getStateEnum() {
        return stateEnum;
    }

    @Override
    public void setStateEnum(MinerStateEnum stateEnum) {
        this.stateEnum = stateEnum;
    }

    @Override
    public Miner getEntity() {
        return miner;
    }
   
}
