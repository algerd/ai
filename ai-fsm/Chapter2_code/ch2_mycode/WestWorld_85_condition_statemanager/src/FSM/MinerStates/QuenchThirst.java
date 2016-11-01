
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;
import FSM.LocationEnum;

/**
 * Miner changes location to the saloon and keeps buying Whiskey until his thirst is quenched.
 * When satisfied he returns to the gold mine and resumes his quest for nuggets.
 */
class QuenchThirst extends State {

    private Miner miner;
    
    QuenchThirst() {}
        
    QuenchThirst(Miner miner) {
        this.miner = miner;
    }
    
    public Miner getMiner() {
        return miner;
    }
    
    /**
     * Enter Condition: If miner is thirsty then QuenchThirst.
     */
    @Override
    public boolean enterCondition() {
        if (miner.isThirst()) {
            miner.changeState(this);
            debugEnterCondition("QuenchThirst: Thirst = " + miner.getThirst());
            return true;
        } 
        return false;
    }
    
    @Override
    public void enter() {
        if (miner.getLocation() != LocationEnum.SALOON) {
            miner.setLocation(LocationEnum.SALOON);
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
       
        if (exitCondition()) {
            if (transit()) {
                debugTransition("QuenchThirst");
            }
        }
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
    public boolean transit() {
        return false 
            || miner.getStateManager().getState(MinerStateEnum.GO_HOME).enterCondition()    
            || miner.getStateManager().getState(MinerStateEnum.DIG_NUGGET).enterCondition()    
            || miner.getStateManager().getState(MinerStateEnum.VISIT_BANK).enterCondition()    
        ;
    }
    
}
