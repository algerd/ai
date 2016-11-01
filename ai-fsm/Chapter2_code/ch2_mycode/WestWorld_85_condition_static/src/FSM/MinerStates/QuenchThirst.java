
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;
import FSM.LocationEnum;

/**
 * Miner changes location to the saloon and keeps buying Whiskey until his thirst is quenched.
 * When satisfied he returns to the gold mine and resumes his quest for nuggets.
 */
public class QuenchThirst extends State<Miner> {

    public QuenchThirst() {}
    
    @Override
    public void enter(Miner miner) {
        if (miner.getLocation() != LocationEnum.SALOON) {
            miner.setLocation(LocationEnum.SALOON);
            System.out.println(miner.getEntity() + ": " + "Boy, ah sure is thusty! Walking to the saloon");
        }
    }

    @Override
    public void execute(Miner miner) {
        System.out.println(miner.getEntity() + ": " + "That's mighty fine sippin liquer"); 
        miner.setThirst(0);
        miner.setMoneyInBank(miner.getMoneyInBank() - 1);
        miner.setHunger(miner.getHunger() + 1);
        miner.setFatigue(miner.getFatigue() + 1);
       
        if (exitCondition(miner)) {
            if (transit(miner)) {
                debugTransition("QuenchThirst");
            }
        }
    }

    @Override
    public void exit(Miner miner) {
        System.out.println(miner.getEntity() + ": " + "Leaving the saloon, feelin' good");
    }
    
    @Override
    public boolean exitCondition(Miner miner){
        return !miner.isThirst();
    }
    
    /**
     * Enter Condition: If miner is thirsty then QuenchThirst.
     */
    public static boolean enterCondition(Miner miner) {
        if (miner.isThirst()) {
            miner.changeState(new QuenchThirst());
            debugEnterCondition("QuenchThirst: Thirst = " + miner.getThirst());
            return true;
        } 
        return false;
    }
    
    @Override
    public boolean transit(Miner miner) {
        return false   
            || GoHome.enterCondition(miner)
            || DigNugget.enterCondition(miner)
            || VisitBank.enterCondition(miner)
        ;
    }
    
}
