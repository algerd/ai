
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;
import FSM.LocationEnum;
import FSM.Messages.Telegram;

/**
 * In this state the miner will walk to a gold mine and pick up a nugget of gold.
 * If the miner already has a nugget of gold he'll change state to VisitBankAndDepositGold.
 * If he gets thirsty he'll change state to QuenchThirst
 * EnterMineAndDigForNugget is singleton.
 */
public class EnterMineAndDigForNugget extends State<Miner> {

    static final EnterMineAndDigForNugget instance = new EnterMineAndDigForNugget();

    private EnterMineAndDigForNugget() {}

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not allowed");
    }

    public static EnterMineAndDigForNugget getInstance() {
        return instance;
    }

    /**
     * if the miner is not already located at the gold mine, he must change location to the gold mine.
     */
    @Override
    public void enter(Miner miner) {
        if (miner.getLocation() != LocationEnum.GOLDMINE) {
            miner.setLocation(LocationEnum.GOLDMINE); 
            System.out.println(miner.getEntity() + ": " + "Walkin' to the goldmine");      
        }
    }

    /**
     * the miner digs for gold until he is carrying in excess of MaxNuggets.
     * if enough gold mined, go and put it in the bank (VisitBankAndDepositGold).
     * if thirsty go and get a whiskey (QuenchThirst).
     */
    @Override
    public void execute(Miner miner) {
        miner.addToGoldCarried(1);
        miner.increaseFatigue();
        if (miner.isPocketsFull()) {
            miner.getStateMachine().changeState(VisitBankAndDepositGold.getInstance());
        }
        if (miner.isThirst()) {
            miner.getStateMachine().changeState(QuenchThirst.getInstance());
        }       
        System.out.println(miner.getEntity() + ": " + "Pickin' up a nugget");
    }

    /**
     * gold miner is leaving the mine. 
     */
    @Override
    public void exit(Miner miner) {
        System.out.println(miner.getEntity() + ": " + "Ah'm leavin' the goldmine with mah pockets full o' sweet gold");
    }
    
    @Override
    public boolean onMessage(Miner pMiner, Telegram msg) {
        return false;
    }
    
}