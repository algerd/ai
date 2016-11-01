
package FSM.MinerStates;

import FSM.Event;
import FSM.EventTypeEnum;
import FSM.LocationEnum;
import FSM.Miner;
import FSM.State;

class Eating extends State<MinerStateEnum> {   
    // флаг для внешнего, принудительного указания входа в состояние (для эвентов)
    private boolean enter;
    private MinerStateEnum stateEnum;
    private Miner miner;
    private final LocationEnum location = LocationEnum.SHACK;
    private final MinerStateEnum[] transitionArray = new MinerStateEnum[]{
        MinerStateEnum.GO_HOME,
        MinerStateEnum.QUENCH_THIRST,
        MinerStateEnum.SLEEP,
        MinerStateEnum.DIG_NUGGET,
        MinerStateEnum.VISIT_BANK    
    };
    
    Eating(Miner miner) {
        this.miner = miner;
    }
    
    @Override
    public boolean enterCondition() {
         if (isEnter() || miner.isHungry()) {
            //debugEnterCondition("Eating: Hunger = " + miner.getHunger());
            setEnter(false);
            return true;
        } 
        return false;
    }
   
    @Override
    public Event enter(State prevState) {
        Event event = new Event(EventTypeEnum.ENTER, this, false);
        if (prevState.getLocation() != this.location) {
            System.out.println(miner.getName() + ": " + "go to eat.");
            event.setHandler(true);
            return event;
        }
        return event;
    }
    
    @Override
    public Event execute() {
        System.out.println(miner.getName() + ": " + "is eating beacon.");
        miner.setThirst(miner.getThirst() + 1);
        miner.setHunger(0);
        miner.setFatigue(miner.getFatigue() + 1);
        return new Event(EventTypeEnum.EXECUTE, this, true);
    }
     
    @Override
    public boolean exitCondition() {
        return !miner.isHungry();
    }
    
    @Override
    public Event exit() {   
        System.out.println(miner.getName() + ": " + "This was tasty beacon!!!");
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
