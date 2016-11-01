
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;
import FSM.LocationEnum;

/**
 * Miner will go home and sleep until his fatigue is decreased sufficiently.
 */
class GoHome extends State {
    
    private Miner miner;
    
    GoHome() {}
    
    GoHome(Miner miner) {
        this.miner = miner;
    }
    
    public Miner getMiner() {
        return miner;
    }
    
    /**
    * Go home with any state and any condition.
    */
    @Override
    public boolean enterCondition() {                 
        miner.changeState(this);
        debugEnterCondition("GoHome");
        return true; 
    }

    @Override
    public void enter() {
        if (miner.getLocation() != LocationEnum.SHACK) {
            miner.setLocation(LocationEnum.SHACK);
            System.out.println(miner.getEntity() + ": " + "Walk to home.");
        }
    }

    @Override
    public void execute() {
        System.out.println(miner.getEntity() + ": " + "is in the home.");
        miner.setThirst(miner.getThirst() + 1);
        miner.setHunger(miner.getHunger() + 1);
        miner.setFatigue(miner.getFatigue() + 1);
        
        if (exitCondition()) {
            if (transit()) {
                debugTransition("GoHome");
            }
        }      
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
    public boolean transit() {
        return false
            || miner.getStateManager().getState(MinerStateEnum.SLEEP).enterCondition()    
            || miner.getStateManager().getState(MinerStateEnum.QUENCH_THIRST).enterCondition()    
            || miner.getStateManager().getState(MinerStateEnum.EATING).enterCondition()
            || miner.getStateManager().getState(MinerStateEnum.DIG_NUGGET).enterCondition()    
            || miner.getStateManager().getState(MinerStateEnum.VISIT_BANK).enterCondition() 
        ;
    }   
    
}
