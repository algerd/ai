
package FSM.MinerStates;

import FSM.LocationEnum;
import FSM.Miner;
import FSM.State;

public class Sleep extends State<Miner> {
    
    public Sleep() {}
    
    @Override
    public void enter(Miner miner) {
        if (miner.getLocation() != LocationEnum.SHACK) {
            miner.setLocation(LocationEnum.SHACK);
            System.out.println(miner.getEntity() + ": " + "Go to sleep");
        }
    }
    
    @Override
    public void execute(Miner miner) {
        System.out.println(miner.getEntity() + ": " + "ZZZZ... ");
        miner.setThirst(miner.getThirst() + 1);
        miner.setHunger(miner.getHunger() + 1);
        miner.setFatigue(miner.getFatigue() - 3);
        
        if (exitCondition(miner)) {
            if (transit(miner)) {          
                debugTransition("Sleep");
            }
        }    
    }
    
    @Override
    public void exit(Miner miner) {   
        System.out.println(miner.getEntity() + ": " + "Wake up!");
    }
    
    @Override
    public boolean exitCondition(Miner miner) {
        return miner.isRested();
    }
    
    @Override
    public boolean transit(Miner miner) {
        return false
            || miner.getTransition().toGoHome()
            || miner.getTransition().toQuenchThirst()
            || miner.getTransition().toEating()
            || miner.getTransition().toDigNugget()
            || miner.getTransition().toVisitBank()
        ;
    }
     
}
