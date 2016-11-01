
package FSM.MinerStates;

import FSM.Event;
import FSM.EventTypeEnum;
import FSM.Miner;
import FSM.LocationEnum;
import FSM.State;

class QuenchThirst extends State<MinerStateEnum> {
    // флаг для внешнего, принудительного указания входа в состояние (для эвентов)
    private boolean enter;
    private MinerStateEnum stateEnum;
    private Miner miner;
    private final LocationEnum location = LocationEnum.SALOON;
    private final MinerStateEnum[] transitionArray = new MinerStateEnum[]{
        MinerStateEnum.GO_HOME,
        MinerStateEnum.DIG_NUGGET,
        MinerStateEnum.VISIT_BANK
    };
        
    QuenchThirst(Miner miner) {
        this.miner = miner;
    }
        
    @Override
    public boolean enterCondition() {
        if (isEnter() || miner.isThirst()) {
            //debugEnterCondition("QuenchThirst: Thirst = " + miner.getThirst());
            setEnter(false);
            return true;
        } 
        return false;
    }
    
    @Override
    public Event enter(State prevState) {
        Event event = new Event(EventTypeEnum.ENTER, this, false);
        if (prevState.getLocation() != this.location) {
            System.out.println(miner.getName() + ": " + "Boy, ah sure is thusty! Walking to the saloon");
            event.setHandler(true);
            return event;
        }
        return event;
    }

    @Override
    public Event execute() {
        System.out.println(miner.getName() + ": " + "That's mighty fine sippin liquer"); 
        miner.setThirst(0);
        miner.setMoneyInBank(miner.getMoneyInBank() - 1);
        miner.setHunger(miner.getHunger() + 1);
        miner.setFatigue(miner.getFatigue() + 1);
        return new Event(EventTypeEnum.EXECUTE, this, true);
    }

    @Override
    public boolean exitCondition(){
        return !miner.isThirst();
    }
    
    @Override
    public Event exit() {
        System.out.println(miner.getName() + ": " + "Leaving the saloon, feelin' good");
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
