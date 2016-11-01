
package FSM.MinerStates;

import FSM.LocationEnum;
import FSM.Miner;
import FSM.State;

class Eating extends State {
    
    private Miner miner;
    
    Eating() {}
        
    Eating(Miner miner) {
        this.miner = miner;
    }
    
    public Miner getMiner() {
        return miner;
    }
    
    /**
     * Enter Condition: If miner is hungry then to eat.
     */
    @Override
    public boolean enterCondition() {
         if (miner.isHungry()) {
            miner.changeState(this);
            debugEnterCondition("Eating: Hunger = " + miner.getHunger());
            return true;
        } 
        return false;
    }
   
    @Override
    public void enter() {
        if (miner.getLocation() != LocationEnum.SHACK) {
            miner.setLocation(LocationEnum.SHACK);
            System.out.println(miner.getEntity() + ": " + "go to eat.");
        }
    }
    
    @Override
    public void execute() {
        System.out.println(miner.getEntity() + ": " + "is eating beacon.");
        miner.setThirst(miner.getThirst() + 1);
        miner.setHunger(0);
        miner.setFatigue(miner.getFatigue() + 1);
          
        if (exitCondition()) {
            if (transit()) {          
                debugTransition("Eating");
            }
        }
      
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
    public boolean transit() {
        return false 
            || miner.getStateManager().getState(MinerStateEnum.GO_HOME).enterCondition()    
            || miner.getStateManager().getState(MinerStateEnum.QUENCH_THIRST).enterCondition()    
            || miner.getStateManager().getState(MinerStateEnum.SLEEP).enterCondition()    
            || miner.getStateManager().getState(MinerStateEnum.DIG_NUGGET).enterCondition()    
            || miner.getStateManager().getState(MinerStateEnum.VISIT_BANK).enterCondition()
        ;
    }
     
}
