
package FSM.MinerStates;

import FSM.Messages.Telegram;
import FSM.Miner;
import FSM.State;

/**
 * this is implemented as a state blip. 
 * The miner eats the stew, gives Elsa some compliments and then returns to his previous state
 */
public class EatStew extends State<Miner> {

    static final EatStew instance = new EatStew();

    private EatStew() {}

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not allowed");
    }

    public static EatStew getInstance() {
        return instance;
    }

    @Override
    public void enter(Miner miner) {
        System.out.println(miner.getEntity() + ": " + "Smells Reaaal goood Elsa!");
    }

    @Override
    public void execute(Miner miner) {
        System.out.println(miner.getEntity() + ": " + "Tastes real good too!");
        miner.getStateMachine().revertToPreviousState();
    }

    @Override
    public void exit(Miner miner) {
        System.out.println(miner.getEntity() + ": " + "Thankya li'lle lady. Ah better get back to whatever ah wuz doin'");
    }

    @Override
    public boolean onMessage(Miner miner, Telegram msg) {
        return false;
    }
}