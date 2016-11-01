
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;
import FSM.LocationEnum;
import FSM.Telegram;

/**
 * Entity will go to a bank and deposit any nuggets he is carrying.
 * If the miner is subsequently wealthy enough he'll walk home,
 * otherwise he'll keep going to get more gold.
 */
class VisitBankAndDepositGold extends State<Miner> {
    public VisitBankAndDepositGold() {}

    @Override
    public void enter(Miner miner) {
        if (miner.getLocation() != LocationEnum.BANK) {
            miner.setLocation(LocationEnum.BANK);
            System.out.println(miner.getEntity() + ": " + "Goin' to the bank. Yes siree");
        }
    }

    @Override
    public void execute(Miner miner) {
        miner.addToWealth(miner.getGoldCarried());
        miner.setGoldCarried(0);

        System.out.println(miner.getEntity() + ": " + "Depositing gold. Total savings now: " + miner.getMoneyInBank());
        
        if (miner.getMoneyInBank() >= Miner.COMFORT_LEVEL) {
            miner.getStateMachine().changeState(MinerStateManagerEnum.GO_HOME_TO_SLEEP.getState());
            System.out.println(miner.getEntity() + ": " + "WooHoo! Rich enough for now. Back home to mah li'lle lady");
        }
        else {
            miner.getStateMachine().changeState(MinerStateManagerEnum.DIG_NUGGET.getState());
        }
    }

    @Override
    public void exit(Miner miner) {
        System.out.println(miner.getEntity() + ": " + "Leavin' the bank");
    }
    
    @Override
    public boolean onMessage(Miner pMiner, Telegram msg) {
        return false;
    }
}
