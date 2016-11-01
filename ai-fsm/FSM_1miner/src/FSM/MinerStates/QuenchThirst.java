
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;
import FSM.LocationEnum;

class QuenchThirst extends State {

    private Miner miner;
    private final LocationEnum location = LocationEnum.SALOON;
    private final MinerStateEnum[] transitionArray = new MinerStateEnum[]{
        MinerStateEnum.GO_HOME,
        MinerStateEnum.DIG_NUGGET,
        MinerStateEnum.VISIT_BANK
    };
    
    QuenchThirst() {}
        
    QuenchThirst(Miner miner) {
        this.miner = miner;
    }
        
    @Override
    public boolean enterCondition() {
        if (miner.isThirst()) {
            debugEnterCondition("QuenchThirst: Thirst = " + miner.getThirst());
            return true;
        } 
        return false;
    }
    
    @Override
    public void enter(LocationEnum prevlocation) {
        if (prevlocation != this.location) {
            System.out.println(miner.getEntity() + ": " + "Boy, ah sure is thusty! Walking to the saloon");
        }
    }

    @Override
    public void execute() {
        System.out.println(miner.getEntity() + ": " + "That's mighty fine sippin liquer"); 
        miner.setThirst(0);
        miner.setMoneyInBank(miner.getMoneyInBank() - 1);
        miner.setHunger(miner.getHunger() + 1);
        miner.setFatigue(miner.getFatigue() + 1);
    }

    @Override
    public boolean exitCondition(){
        return !miner.isThirst();
    }
    
    @Override
    public void exit() {
        System.out.println(miner.getEntity() + ": " + "Leaving the saloon, feelin' good");
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
