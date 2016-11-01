
package FSM.MinerStates;

import FSM.Event;
import FSM.EventTypeEnum;
import FSM.Miner;
import FSM.LocationEnum;
import FSM.State;

class VisitBank extends State<MinerStateEnum> {
    // флаг для внешнего, принудительного указания входа в состояние (для эвентов)
    private boolean enter;
    private MinerStateEnum stateEnum;
    private Miner miner;
    private final LocationEnum location = LocationEnum.BANK;
    private final MinerStateEnum[] transitionArray = new MinerStateEnum[]{
        MinerStateEnum.GO_HOME,
        MinerStateEnum.DIG_NUGGET,
        MinerStateEnum.QUENCH_THIRST
    };
    
    VisitBank(Miner miner) {
        this.miner = miner;
    }
        
    @Override
    public boolean enterCondition() {     
        if (isEnter() || miner.isPocketsFull()) {
            //debugEnterCondition("VisitBank:  moneyInBank = " + miner.getMoneyInBank());
            setEnter(false);
            return true;
        }      
        return false;
    }

    @Override
    public Event enter(State prevState) {
        Event event = new Event(EventTypeEnum.ENTER, this, false);
        if (prevState.getLocation() != this.location) {
            System.out.println(miner.getName() + ": " + "Goin' to the bank. Yes siree");
            event.setHandler(true);
            return event;
        }
        return event;
    }

    @Override
    public Event execute() {        
        miner.setMoneyInBank(miner.getMoneyInBank() + miner.getGoldCarried());
        miner.setGoldCarried(0);
        miner.setFatigue(miner.getFatigue() + 1);
        miner.setThirst(miner.getThirst() + 1);
        miner.setHunger(miner.getHunger() + 1);
        System.out.println(miner.getName() + ": " + "Depositing gold. Total savings now: " + miner.getMoneyInBank()); 
        return new Event(EventTypeEnum.EXECUTE, this, true);
    }
    
    @Override
    public boolean exitCondition(){
        return !miner.isPocketsFull();
    }

    @Override
    public Event exit() {
        System.out.println(miner.getName() + ": " + "Leavin' the bank");
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
