
package FSM.MinerStates;

import FSM.Miner;
import FSM.LocationEnum;

class DigNugget extends MinerState {
   
    private Miner miner;
    private final LocationEnum location = LocationEnum.GOLDMINE;
    private final MinerStateEnum[] transitionArray = new MinerStateEnum[]{
        MinerStateEnum.GO_HOME,
        MinerStateEnum.QUENCH_THIRST,
        MinerStateEnum.VISIT_BANK    
    };
      
    private DigNugget() {}
     
    DigNugget(Miner miner) {
        this.miner = miner;
    }
       
    @Override
    public boolean enterCondition() {
        if (!miner.isPocketsFull() && !miner.isEnoughMoney()) {
            debugEnterCondition("DigNugget: goldCarried = " + miner.getGoldCarried());
            return true;
        } 
        return false;
    }
   
    @Override
    public void enter(MinerState prevState) {
        if (prevState.getLocation() != this.location) {
            System.out.println(miner.getEntity() + ": " + "Walkin' to the goldmine");      
        }
    }

    @Override
    public void execute() {
        System.out.println(miner.getEntity() + ": " + "Pickin' up a nugget");
        miner.setGoldCarried(miner.getGoldCarried() + 1);
        miner.setFatigue(miner.getFatigue() + 2);
        miner.setThirst(miner.getThirst() + 2);
        miner.setHunger(miner.getHunger() + 2);
    }

    @Override
    public boolean exitCondition(){
        return miner.isPocketsFull();
    }
    
    @Override
    public void exit() {
        System.out.println(miner.getEntity() + ": " + "Ah'm leavin' the goldmine with mah pockets full o' sweet gold");
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
