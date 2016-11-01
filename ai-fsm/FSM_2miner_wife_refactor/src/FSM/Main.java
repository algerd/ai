
package FSM;

import FSM.MinerStates.MinerStateEnum;
import FSM.WifeStates.WifeStateEnum;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        
        StateMachine minerFSM = new Miner(EntityEnum.MINER_BOB).getStateMachine(MinerStateEnum.GO_HOME);
        StateMachine wifeFSM = new Wife(EntityEnum.ELSA).getStateMachine(WifeStateEnum.HOUSE_WORK);
        
        for (int i = 0; i < 50; ++i) {
            minerFSM.update();
            wifeFSM.update();
            Thread.sleep(50);
        }
    }
}
