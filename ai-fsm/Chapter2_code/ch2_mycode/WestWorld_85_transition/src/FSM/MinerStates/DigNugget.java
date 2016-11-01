
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;
import FSM.LocationEnum;

/**
 * In this state the miner will walk to a gold mine and pick up a nugget of gold.
 */
public class DigNugget extends State<Miner> {
    
    public DigNugget() {}
    
    @Override
    public void enter(Miner miner) {
        if (miner.getLocation() != LocationEnum.GOLDMINE) {
            miner.setLocation(LocationEnum.GOLDMINE); 
            System.out.println(miner.getEntity() + ": " + "Walkin' to the goldmine");      
        }
    }

    @Override
    public void execute(Miner miner) {
        System.out.println(miner.getEntity() + ": " + "Pickin' up a nugget");
        miner.setGoldCarried(miner.getGoldCarried() + 1);
        miner.setFatigue(miner.getFatigue() + 2);
        miner.setThirst(miner.getThirst() + 2);
        miner.setHunger(miner.getHunger() + 2);
           
        if (exitCondition(miner)) {
            if (transit(miner)) {
                debugTransition("DigNugget");
            }
        }
    }

    @Override
    public void exit(Miner miner) {
        System.out.println(miner.getEntity() + ": " + "Ah'm leavin' the goldmine with mah pockets full o' sweet gold");
    }
    
    @Override
    public boolean exitCondition(Miner miner){
        return miner.isPocketsFull();
    }
    
    @Override
    public boolean transit(Miner miner) {
        return false 
            || miner.getTransition().toGoHome()
            || miner.getTransition().toQuenchThirst()    
            || miner.getTransition().toVisitBank()                       
        ;
    }
    
}