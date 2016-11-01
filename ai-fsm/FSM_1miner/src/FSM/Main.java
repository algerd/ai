
package FSM;

import FSM.MinerStates.MinerStateEnum;
import FSM.MinerStates.MinerStateMachine;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        StateMachine minerFSM = new MinerStateMachine(EntityEnum.MINER_BOB, MinerStateEnum.GO_HOME);
        
        for (int i = 0; i < 50; ++i) {
            minerFSM.update();
            Thread.sleep(100);
        }
    }
}
