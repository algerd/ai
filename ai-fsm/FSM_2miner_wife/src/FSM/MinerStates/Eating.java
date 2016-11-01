
package FSM.MinerStates;

import FSM.LocationEnum;
import FSM.Miner;

class Eating extends MinerState {
    
    private Miner miner;
    private final LocationEnum location = LocationEnum.SHACK;
    private final MinerStateEnum[] transitionArray = new MinerStateEnum[]{
        MinerStateEnum.GO_HOME,
        MinerStateEnum.QUENCH_THIRST,
        MinerStateEnum.SLEEP,
        MinerStateEnum.DIG_NUGGET,
        MinerStateEnum.VISIT_BANK    
    };
    
    Eating() {}
        
    Eating(Miner miner) {
        this.miner = miner;
    }
    
    @Override
    public boolean enterCondition() {
         if (miner.isHungry()) {
            debugEnterCondition("Eating: Hunger = " + miner.getHunger());
            return true;
        } 
        return false;
    }
   
    @Override
    public void enter(MinerState prevState) {
        if (prevState.getLocation() != this.location) {
            System.out.println(miner.getEntity() + ": " + "go to eat.");
        }
    }
    
    @Override
    public void execute() {
        System.out.println(miner.getEntity() + ": " + "is eating beacon.");
        miner.setThirst(miner.getThirst() + 1);
        miner.setHunger(0);
        miner.setFatigue(miner.getFatigue() + 1);
    }
     
    @Override
    public boolean exitCondition() {
        return !miner.isHungry();
    }
    
    @Override
    public void exit() {   
        System.out.println(miner.getEntity() + ": " + "This was tasty beacon!!!");
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
