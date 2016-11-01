
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;
import FSM.LocationEnum;

/**
 * In this state the miner will walk to a gold mine and pick up a nugget of gold.
 */
class DigNugget extends State {
 
    private Miner miner;
    
    private DigNugget() {}
    
    DigNugget(Miner miner) {
        this.miner = miner;
    }

    public Miner getMiner() {
        return miner;
    }
       
    /**
     * If miner has not full pockets and has not enough money then to dig nugget. 
     */
    @Override
    public boolean enterCondition() {
        if (!miner.isPocketsFull() && !miner.isEnoughMoney()) {
            miner.changeState(this);
            debugEnterCondition("DigNugget: goldCarried = " + miner.getGoldCarried());
            return true;
        } 
        return false;
    }
   
    @Override
    public void enter() {
        if (miner.getLocation() != LocationEnum.GOLDMINE) {
            miner.setLocation(LocationEnum.GOLDMINE); 
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
           
        if (exitCondition()) {
            if (transit()) {
                debugTransition("DigNugget");
            }
        }
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
    public boolean transit() {
        return false 
            || miner.getStateManager().getState(MinerStateEnum.GO_HOME).enterCondition()
            || miner.getStateManager().getState(MinerStateEnum.QUENCH_THIRST).enterCondition()     
            || miner.getStateManager().getState(MinerStateEnum.VISIT_BANK).enterCondition()     
        ;
    }
    
}
