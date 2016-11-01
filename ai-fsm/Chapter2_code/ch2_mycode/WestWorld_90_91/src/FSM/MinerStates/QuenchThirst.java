
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;
import FSM.LocationEnum;

/**
 * Miner changes location to the saloon and keeps buying Whiskey until his thirst is quenched.
 * When satisfied he returns to the gold mine and resumes his quest for nuggets.
 * This is a singleton.
 */
class QuenchThirst extends State<Miner> {

    static final QuenchThirst instance = new QuenchThirst();

    private QuenchThirst() {}

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not allowed");
    }

    static public QuenchThirst getInstance() {
        return instance;
    }

    @Override
    public void enter(Miner miner) {
        if (miner.getLocation() != LocationEnum.SALOON) {
            miner.setLocation(LocationEnum.SALOON);
            System.out.println(miner.getEntity() + ": " + "Boy, ah sure is thusty! Walking to the saloon");
        }
    }

    @Override
    public void execute(Miner miner) {
        if (miner.isThirst()) {
            miner.buyAndDrinkAWhiskey();
            miner.getStateMachine().changeState(EnterMineAndDigForNugget.getInstance());
            System.out.println(miner.getEntity() + ": " + "That's mighty fine sippin liquer");       
        } 
        else {
            System.out.println("ERROR! ERROR! ERROR!");
        }
    }

    @Override
    public void exit(Miner miner) {
        System.out.println(miner.getEntity() + ": " + "Leaving the saloon, feelin' good");
    }
    
}