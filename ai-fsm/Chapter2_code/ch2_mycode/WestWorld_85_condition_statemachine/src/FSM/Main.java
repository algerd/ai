
package FSM;

import FSM.MinerStates.MinerStateEnum;
import FSM.MinerStates.MinerStateMachine;
import FSM.WifeStates.WifeStateEnum;
import FSM.WifeStates.WifeStateMachine;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        StateMachine minerFSM = new MinerStateMachine(EntityEnum.MINER_BOB, MinerStateEnum.GO_HOME);
        StateMachine wifeFSM = new WifeStateMachine(EntityEnum.ELSA, WifeStateEnum.HOUSE_WORK);
        
        for (int i = 0; i < 50; ++i) {
            //minerFSM.update();
            wifeFSM.update();
            Thread.sleep(100);
        }
    }
}
