
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;
import FSM.LocationEnum;

/**
 * Miner will go home and sleep until his fatigue is decreased sufficiently.
 * This is a singleton.
 */
public class GoHomeAndSleepTilRested extends State<Miner> {

    static final GoHomeAndSleepTilRested instance = new GoHomeAndSleepTilRested();

    private GoHomeAndSleepTilRested() {}

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not allowed");
    }

    static public GoHomeAndSleepTilRested Instance() {
        return instance;
    }

    @Override
    public void enter(Miner miner) {
        if (miner.getLocation() != LocationEnum.SHACK) {
            miner.setLocation(LocationEnum.SHACK);
            System.out.println(miner.getEntity() + ": " + "Walkin' home");
        }
    }

    @Override
    public void execute(Miner miner) {
        //if miner is not fatigued start to dig for nuggets again.
        if (!miner.isFatigued()) {
            miner.changeState(EnterMineAndDigForNugget.Instance());
            System.out.println(miner.getEntity() + ": " + "What a God darn fantastic nap! Time to find more gold");
        } 
        //sleep
        else {           
            miner.decreaseFatigue();
            System.out.println(miner.getEntity() + ": " + "ZZZZ... ");
        }
    }

    @Override
    public void exit(Miner miner) {
        System.out.println(miner.getEntity() + ": " + "Leaving the house");
    }
    
}
