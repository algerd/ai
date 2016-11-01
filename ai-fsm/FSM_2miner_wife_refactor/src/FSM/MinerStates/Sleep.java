
package FSM.MinerStates;

import FSM.LocationEnum;
import FSM.Miner;
import FSM.State;

class Sleep extends State<MinerStateEnum> {
   
    private Miner miner;
    private final LocationEnum location = LocationEnum.SHACK;
    private final MinerStateEnum[] transitionArray = new MinerStateEnum[]{
        MinerStateEnum.GO_HOME,
        MinerStateEnum.QUENCH_THIRST,
        MinerStateEnum.EATING,
        MinerStateEnum.DIG_NUGGET,
        MinerStateEnum.VISIT_BANK     
    };
    
    Sleep() {}
        
    Sleep(Miner miner) {
        this.miner = miner;
    }
        
    @Override
    public boolean enterCondition() {
        if (miner.isFatigued()) {
            debugEnterCondition("Sleep: Fatigue = " + miner.getFatigue());
            return true;
        } 
        return false;
    }
    
    @Override
    public void enter(State prevState) {
        if (prevState.getLocation() != this.location) {
            System.out.println(miner.getEntity() + ": " + "Go to sleep");
        }
    }
    
    @Override
    public void execute() {
        System.out.println(miner.getEntity() + ": " + "ZZZZ... ");
        miner.setThirst(miner.getThirst() + 1);
        miner.setHunger(miner.getHunger() + 1);
        miner.setFatigue(miner.getFatigue() - 3);  
    }
    
    @Override
    public boolean exitCondition() {
        return miner.isRested();
    }
    
    @Override
    public void exit() {   
        System.out.println(miner.getEntity() + ": " + "Wake up!");
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
