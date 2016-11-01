
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
    
    /**
    * Go home with any state and any condition.
    */
    public static boolean enterCondition(Miner miner) {                 
        miner.changeState(new GoHome());
        debugEnterCondition("GoHome");
        return true; 
    }
    
    @Override
    public boolean transit(Miner miner) {
        return false
            || Sleep.enterCondition(miner) 
            || Eating.enterCondition(miner)
            || QuenchThirst.enterCondition(miner)
            || DigNugget.enterCondition(miner)
            || VisitBank.enterCondition(miner)
        ;
    }   
    
}
