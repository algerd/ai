
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;

public enum MinerStateEnum {
    DIG_NUGGET(){
        @Override
        public DigNugget get(Miner miner){
            return new DigNugget(miner);
        }
    },
    EATING(){
        @Override
        public Eating get(Miner miner){
            return new Eating(miner);
        }
    },
    GO_HOME(){
        @Override
        public GoHome get(Miner miner){
            return new GoHome(miner);
        }
    },
    QUENCH_THIRST(){
        @Override
        public QuenchThirst get(Miner miner){
            return new QuenchThirst(miner);
        }
    },
    VISIT_BANK(){
        @Override
        public VisitBank get(Miner miner){
            return new VisitBank(miner);
        }
    },
    SLEEP(){
        @Override
        public Sleep get(Miner miner){
            return new Sleep(miner);
        }
    };
    
    public abstract State get(Miner miner);
 
}
