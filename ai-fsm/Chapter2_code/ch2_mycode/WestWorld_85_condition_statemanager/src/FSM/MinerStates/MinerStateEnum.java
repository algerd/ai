
package FSM.MinerStates;

import FSM.Miner;
import FSM.State;

public enum MinerStateEnum {
    DIG_NUGGET(){
        public DigNugget get(Miner miner){
            return new DigNugget(miner);
        }
    },
    EATING(){
        public Eating get(Miner miner){
            return new Eating(miner);
        }
    },
    GO_HOME(){
        public GoHome get(Miner miner){
            return new GoHome(miner);
        }
    },
    QUENCH_THIRST(){
        public QuenchThirst get(Miner miner){
            return new QuenchThirst(miner);
        }
    },
    VISIT_BANK(){
        public VisitBank get(Miner miner){
            return new VisitBank(miner);
        }
    },
    SLEEP(){
        public Sleep get(Miner miner){
            return new Sleep(miner);
        }
    };
    
    public abstract State get(Miner miner);
 
}
