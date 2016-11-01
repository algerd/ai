
package FSM.MinerStates;

import FSM.Miner;
import FSM.LocationEnum;
import FSM.State;

class VisitBank extends State<MinerStateEnum> {
    
    private Miner miner;
    private final LocationEnum location = LocationEnum.BANK;
    private final MinerStateEnum[] transitionArray = new MinerStateEnum[]{
        MinerStateEnum.GO_HOME,
        MinerStateEnum.DIG_NUGGET,
        MinerStateEnum.QUENCH_THIRST
    };
    
    VisitBank() {}
    
    VisitBank(Miner miner) {
        this.miner = miner;
    }
        
    @Override
    public boolean enterCondition() {     
        if (miner.isPocketsFull()) {
            debugEnterCondition("VisitBank:  moneyInBank = " + miner.getMoneyInBank());
            return true;
        }      
        return false;
    }

    @Override
    public void enter(State prevState) {
        if (prevState.getLocation() != this.location) {
            System.out.println(miner.getEntity() + ": " + "Goin' to the bank. Yes siree");
        }
    }

    @Override
    public void execute() {        
        miner.setMoneyInBank(miner.getMoneyInBank() + miner.getGoldCarried());
        miner.setGoldCarried(0);
        miner.setFatigue(miner.getFatigue() + 1);
        miner.setThirst(miner.getThirst() + 1);
        miner.setHunger(miner.getHunger() + 1);
        System.out.println(miner.getEntity() + ": " + "Depositing gold. Total savings now: " + miner.getMoneyInBank());  
    }
    
    @Override
    public boolean exitCondition(){
        return !miner.isPocketsFull();
    }

    @Override
    public void exit() {
        System.out.println(miner.getEntity() + ": " + "Leavin' the bank");
    }
    
    @Override
    public MinerStateEnum[] getTransitionArray() {
        return this.transitionArray;
    }
    
    @Override
    public LocationEnum getLocation() {
        return this.location;
    }
    
}
