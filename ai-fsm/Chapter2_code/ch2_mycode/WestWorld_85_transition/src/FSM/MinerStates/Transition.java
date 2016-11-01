
package FSM.MinerStates;

import FSM.Miner;

public class Transition {
    
    private Miner miner;
    
    public Transition(Miner miner) {
        this.miner = miner;
    }
    
    private void debugTransition(String state) {
        System.out.println("++++++++ Transition to state " + state + " +++++++++");
    }
    
    
    /**
     * If miner is fatigued then to sleep.
     */
    public boolean toSleep() {
        if (miner.isFatigued()) {
            miner.changeState(new Sleep());
            debugTransition("Sleep: Fatigue = " + miner.getFatigue());
            return true;
        } 
        return false;
    }
    
    /**
     * If miner is hungry then to eat.
     */
    public boolean toEating() {
        if (miner.isHungry()) {
            miner.changeState(new Eating());
            debugTransition("Eating: Hunger = " + miner.getHunger());
            return true;
        } 
        return false;
    }
    
    /**
     * If miner is thirsty then QuenchThirst.
     */
    public boolean toQuenchThirst() {
        if (miner.isThirst()) {
            miner.changeState(new QuenchThirst());
            debugTransition("QuenchThirst: Thirst = " + miner.getThirst());
            return true;
        } 
        return false;
    }
    
    /**
     * Go home with any state and any condition.
     */
    public boolean toGoHome() {                 
        miner.changeState(new GoHome());
        debugTransition("GoHome");
        return true; 
    }
    
    /**
     * If miner has not full pockets and has not enough money then to dig nugget. 
     */
    public boolean toDigNugget() {
        if (!miner.isPocketsFull() && !miner.isEnoughMoney()) {
            miner.changeState(new DigNugget());
            debugTransition("DigNugget: goldCarried = " + miner.getGoldCarried());
            return true;
        } 
        return false;
    }
      
    /**
     * If the miner has full pockets then to VisitBank.
     */
    public boolean toVisitBank() {     
        if (miner.isPocketsFull()) {
            miner.changeState(new VisitBank());
            debugTransition("VisitBank:  moneyInBank = " + miner.getMoneyInBank());
            return true;
        }      
        return false;
    }
     
}
