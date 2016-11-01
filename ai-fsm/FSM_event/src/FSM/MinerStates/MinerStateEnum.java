
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;
import java.util.function.Function;

public enum MinerStateEnum {
    
    DIG_NUGGET(DigNugget::new),     // new DigNugget(miner)
    EATING(Eating::new),
    GO_HOME(GoHome::new),
    QUENCH_THIRST(QuenchThirst::new),
    VISIT_BANK(VisitBank::new),
    SLEEP(Sleep::new);
    
    private final Function<Miner,State> closure;
    
    private MinerStateEnum(Function<Miner,State> callback) {
        this.closure = callback;    
    }
    
    /**
     * Get new instance of State abstract class.
     */
    public State get(Miner miner) {
        return closure.apply(miner);
    }
       
    /*
    // Громоздкий и неуклюжий способ без использования лямбды.
    // Если количество состояний вырастет до десятков и сотен, то размер класса будет запредельный, что резко снизит его читаемость
    DIG_NUGGET(){
        @Override
        public DigNugget get(Miner miner){ return new DigNugget(miner); }
    },
    EATING(){
        @Override
        public Eating get(Miner miner){ return new Eating(miner); }
    };
    public abstract State get(Miner miner);
    */
    
}
