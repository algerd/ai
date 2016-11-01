
package FSM.MinerStates;

import FSM.LocationEnum;
import FSM.Miner;
import FSM.State;

public class Eating extends State<Miner> {
    
    public Eating() {}
    
    @Override
    public void enter(Miner miner) {
        if (miner.getLocation() != LocationEnum.SHACK) {
            miner.setLocation(LocationEnum.SHACK);
            System.out.println(miner.getEntity() + ": " + "go to eat.");
        }
    }
    
    @Override
    public void execute(Miner miner) {
        System.out.println(miner.getEntity() + ": " + "is eating beacon.");
        miner.setThirst(miner.getThirst() + 1);
        miner.setHunger(0);
        miner.setFatigue(miner.getFatigue() + 1);
          
        if (exitCondition(miner)) {
            if (transit(miner)) {          
                debugTransition("Eating");
            }
        }
      
    }
    
    @Override
    public void exit(Miner miner) {   
        System.out.println(miner.getEntity() + ": " + "This was tasty beacon!!!");
    }
    
    @Override
    public boolean exitCondition(Miner miner) {
        return !miner.isHungry();
    }
    
    @Override
    public boolean transit(Miner miner) {
        return false           
            || miner.getTransition().toGoHome()
            || miner.getTransition().toQuenchThirst()
            || miner.getTransition().toSleep()           
            || miner.getTransition().toDigNugget()
            || miner.getTransition().toVisitBank()        
        ;
    }
     
}
