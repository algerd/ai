
package FSM.MinerStates;

import FSM.LocationEnum;
import FSM.Miner;
import FSM.State;

class Sleep extends State {
   
    private Miner miner;
    
    Sleep() {}
        
    Sleep(Miner miner) {
        this.miner = miner;
    }
    
    public Miner getMiner() {
        return miner;
    }
    
    /**
     * Enter Condition: If miner is fatigued then to sleep.
     */
    @Override
    public boolean enterCondition() {
        if (miner.isFatigued()) {
            miner.changeState(this);
            debugEnterCondition("Sleep: Fatigue = " + miner.getFatigue());
            return true;
        } 
        return false;
    }
    
    @Override
    public void enter() {
        if (miner.getLocation() != LocationEnum.SHACK) {
            miner.setLocation(LocationEnum.SHACK);
            System.out.println(miner.getEntity() + ": " + "Go to sleep");
        }
    }
    
    @Override
    public void execute() {
        System.out.println(miner.getEntity() + ": " + "ZZZZ... ");
        miner.setThirst(miner.getThirst() + 1);
        miner.setHunger(miner.getHunger() + 1);
        miner.setFatigue(miner.getFatigue() - 3);
        
        if (exitCondition()) {
            if (transit()) {          
                debugTransition("Sleep");
            }
        }    
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
    public boolean transit() {
        return false
            || miner.getStateManager().getState(MinerStateEnum.GO_HOME).enterCondition()     
            || miner.getStateManager().getState(MinerStateEnum.QUENCH_THIRST).enterCondition()         
            || miner.getStateManager().getState(MinerStateEnum.EATING).enterCondition()     
            || miner.getStateManager().getState(MinerStateEnum.DIG_NUGGET).enterCondition()         
            || miner.getStateManager().getState(MinerStateEnum.VISIT_BANK).enterCondition()         
        ;
    }
     
}
