
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;
import FSM.LocationEnum;

/**
 * Entity will go to a bank and deposit any nuggets he is carrying.
 */
public class VisitBank extends State<Miner> {
    
    public VisitBank() {}

    @Override
    public void enter(Miner miner) {
        if (miner.getLocation() != LocationEnum.BANK) {
            miner.setLocation(LocationEnum.BANK);
            System.out.println(miner.getEntity() + ": " + "Goin' to the bank. Yes siree");
        }
    }

    @Override
    public void execute(Miner miner) {        
        miner.setMoneyInBank(miner.getMoneyInBank() + miner.getGoldCarried());
        miner.setGoldCarried(0);
        miner.setFatigue(miner.getFatigue() + 1);
        miner.setThirst(miner.getThirst() + 1);
        miner.setHunger(miner.getHunger() + 1);
        System.out.println(miner.getEntity() + ": " + "Depositing gold. Total savings now: " + miner.getMoneyInBank());  
        
        if (exitCondition(miner)) {
            if (transit(miner)) {
                debugTransition("VisitBank");
            }
        }
    }

    @Override
    public void exit(Miner miner) {
        System.out.println(miner.getEntity() + ": " + "Leavin' the bank");
    }
    
    @Override
    public boolean exitCondition(Miner miner){
        return !miner.isPocketsFull();
    }
    
    @Override
    public boolean transit(Miner miner) {
        return false
            || miner.getTransition().toGoHome()  
            || miner.getTransition().toDigNugget()
            || miner.getTransition().toQuenchThirst()    
        ;
    }
    
}
