
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;

public enum MinerStateManagerEnum {
    
    EAT_STEW(new EatStew()),
    DIG_NUGGET(new EnterMineAndDigForNugget()),
    GO_HOME_TO_SLEEP(new GoHomeAndSleepTilRested()), 
    QUENCH_THIRST(new QuenchThirst()),     
    VISIT_BANK(new VisitBankAndDepositGold());
   
    private final State<Miner> state;
    
    private MinerStateManagerEnum(State<Miner> state) {
        this.state = state;
    }
    
    public State<Miner> getState() {
        return this.state;
    }
    
}