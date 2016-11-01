
package FSM.MinerStates;

import FSM.Miner;
import FSM.LocationEnum;
import FSM.State;

class GoHome extends State<MinerStateEnum> {
    
    private Miner miner;
    private final LocationEnum location = LocationEnum.SHACK;
    private final MinerStateEnum[] transitionArray = new MinerStateEnum[]{
        MinerStateEnum.SLEEP,
        MinerStateEnum.QUENCH_THIRST,
        MinerStateEnum.EATING,
        MinerStateEnum.DIG_NUGGET,
        MinerStateEnum.VISIT_BANK
    };
    
    GoHome() {}
    
    GoHome(Miner miner) {
        this.miner = miner;
    }
    
    @Override
    public boolean enterCondition() {                 
        debugEnterCondition("GoHome");
        return true; 
    }

    @Override
    public void enter(State prevState) {
        if (prevState.getLocation() != this.location) {
            System.out.println(miner.getEntity() + ": " + "Walk to home.");
        }
    }

    @Override
    public void execute() {
        System.out.println(miner.getEntity() + ": " + "is in the home.");
        miner.setThirst(miner.getThirst() + 1);
        miner.setHunger(miner.getHunger() + 1);
        miner.setFatigue(miner.getFatigue() + 1);     
    }
    
    @Override
    public boolean exitCondition() {
        return true;
    }

    @Override
    public void exit() {
        System.out.println(miner.getEntity() + ": " + "Leaving the home");
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
