
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;
import FSM.LocationEnum;

/**
 * Entity will go to a bank and deposit any nuggets he is carrying.
 */
class VisitBank extends State {
    
    private Miner miner;
    
    VisitBank() {}
    
    VisitBank(Miner miner) {
        this.miner = miner;
    }
    
    public Miner getMiner() {
        return miner;
    }
    
    /**
     * If the miner has full pockets then to VisitBank.
     */
    @Override
    public boolean enterCondition() {     
        if (miner.isPocketsFull()) {
            miner.changeState(this);
            debugEnterCondition("VisitBank:  moneyInBank = " + miner.getMoneyInBank());
            return true;
        }      
        return false;
    }

    @Override
    public void enter() {
        if (miner.getLocation() != LocationEnum.BANK) {
            miner.setLocation(LocationEnum.BANK);
            System.out.println(miner.getEntity() + ": " + "Goin' to the bank. Yes siree");
        }
    }

    @Override
    public void execute() {        
        miner.setMoneyInBank(miner.getMoneyInBank() + miner.getGoldCarried());
        miner.setGoldCarried(0);
        miner.setFatigue(miner.getFatigue() + 1);
        miner.setThirst(miner.getThirst() + 1);
        miner.setHunger(miner.getHunger() + 1);
        System.out.println(miner.getEntity() + ": " + "Depositing gold. Total savings now: " + miner.getMoneyInBank());  
        
        if (exitCondition()) {
            if (transit()) {
                debugTransition("VisitBank");
            }
        }
    }
    
    @Override
    public boolean exitCondition(){
        return !miner.isPocketsFull();
    }

    @Override
    public void exit() {
        System.out.println(miner.getEntity() + ": " + "Leavin' the bank");
    }
    
    @Override
    public boolean transit() {
        return false
            || miner.getStateManager().getState(MinerStateEnum.GO_HOME).enterCondition()     
            || miner.getStateManager().getState(MinerStateEnum.DIG_NUGGET).enterCondition() 
            || miner.getStateManager().getState(MinerStateEnum.QUENCH_THIRST).enterCondition()    
        ;
    }
    
}
