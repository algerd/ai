
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
    
    /**
     * Enter Condition: If miner is hungry then to eat.
     */
    public static boolean enterCondition(Miner miner) {
         if (miner.isHungry()) {
            miner.changeState(new Eating());
            debugEnterCondition("Eating: Hunger = " + miner.getHunger());
            return true;
        } 
        return false;
    }
      
    @Override
    public boolean exitCondition(Miner miner) {
        return !miner.isHungry();
    }
    
    @Override
    public boolean transit(Miner miner) {
        return false           
            || GoHome.enterCondition(miner)
            || QuenchThirst.enterCondition(miner)
            || Sleep.enterCondition(miner)           
            || DigNugget.enterCondition(miner)
            || VisitBank.enterCondition(miner)
        ;
    }
     
}
