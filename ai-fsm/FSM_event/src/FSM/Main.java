
package FSM;

import FSM.EventLogic.MainLogic;

public class Main {
    public static void main(String[] args) throws InterruptedException {
              
        // Запуск логики событий.
        new MainLogic().launch();
              
        for (int i = 0; i < 50; ++i) {
            //EntityEnum.MINER_JOHN.getStateMachine().update();
            EntityEnum.MINER_BOB.getStateMachine().update();
            EntityEnum.ELSA.getStateMachine().update();
            Thread.sleep(50);
        }
             
    }
  
}
