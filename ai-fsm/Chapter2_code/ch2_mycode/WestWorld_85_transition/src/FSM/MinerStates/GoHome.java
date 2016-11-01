
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;
import FSM.LocationEnum;

/**
 * Miner will go home and sleep until his fatigue is decreased sufficiently.
 */
public class GoHome extends State<Miner> {
    
    public GoHome() {}

    @Override
    public void enter(Miner miner) {
        if (miner.getLocation() != LocationEnum.SHACK) {
            miner.setLocation(LocationEnum.SHACK);
            System.out.println(miner.getEntity() + ": " + "Walk to home.");
        }
    }

    @Override
    public void execute(Miner miner) {
        System.out.println(miner.getEntity() + ": " + "is in the home.");
        miner.setThirst(miner.getThirst() + 1);
        miner.setHunger(miner.getHunger() + 1);
        miner.setFatigue(miner.getFatigue() + 1);
        
        if (exitCondition(miner)) {
            if (transit(miner)) {
                debugTransition("GoHome");
            }
        }      
    }

    @Override
    public void exit(Miner miner) {
        System.out.println(miner.getEntity() + ": " + "Leaving the home");
    }
    
    @Override
    public boolean exitCondition(Miner miner) {
        return true;
    }
    
    @Override
    public boolean transit(Miner miner) {
        return false
            || miner.getTransition().toSleep() 
            || miner.getTransition().toEating() 
            || miner.getTransition().toQuenchThirst()    
            || miner.getTransition().toDigNugget()
            || miner.getTransition().toVisitBank()    
        ;
    }   
    
}
