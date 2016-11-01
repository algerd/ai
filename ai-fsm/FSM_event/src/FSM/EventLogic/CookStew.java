
package FSM.EventLogic;

import FSM.EntityEnum;
import FSM.EventTypeEnum;
import FSM.HandlerEvent;
import FSM.MinerStates.MinerStateEnum;
import FSM.StateMachine;
import FSM.WifeStates.WifeStateEnum;

/**
 * Приготовить утку!.
 */
public class CookStew implements Runnable {

    @Override
    public void run() {
        StateMachine minerFSM = EntityEnum.MINER_BOB.getStateMachine();
        StateMachine wifeFSM = EntityEnum.ELSA.getStateMachine();
        
        // Майнер сообщает жене, что он идёт домой и просит приготовить ему утку.
        // Тип события: вход мужа (ENTER) в состояние "дома" (GO_HOME)
        // Вешается флаг входа setEnter(true) в состояние COOK_STEW для жены.
        // После обработки события - колбек удалить(true)
        minerFSM.getEventManager().addHandler(
            EventTypeEnum.ENTER,    
            MinerStateEnum.GO_HOME, 
            ()->wifeFSM.getStateManager().getState(WifeStateEnum.COOK_STEW).setEnter(true),
            true);
        
        // Зададим приоритет 20 и не удалять обработчик после его выполнения (false)
        minerFSM.getEventManager().addHandler(
            EventTypeEnum.ENTER,    
            MinerStateEnum.GO_HOME, 
            ()->wifeFSM.getStateManager().getState(WifeStateEnum.COOK_STEW).setEnter(true),
            false,
            20);
        
        // Зададим задержку 5 тактов и не удалять - обработчик будет запускаться через 5 циклов каждый раз после наступления события
        HandlerEvent minerHandler = minerFSM.getEventManager().addHandler(
            EventTypeEnum.ENTER,    
            MinerStateEnum.GO_HOME, 
            ()->wifeFSM.getStateManager().getState(WifeStateEnum.COOK_STEW).setEnter(true),
            false,
            30,
            20);
        
        // удалить или модифицировать обработчик событий из набора
        //minerFSM.getEventManager().removeHandler(minerHandler);
        //minerHandler.setRemove(true);
        
        // Жена сообщает что утка готова и приглашает мужа отобедать.
        // Тип события: выход жены (EXIT) из состояния приготовления утки(COOK_STEW)
        // Вешается флаг на вход в состояние EATING для мужа
        // После обработки события - колбек удалить(true)
        wifeFSM.getEventManager().addHandler(
            EventTypeEnum.EXIT,
            WifeStateEnum.COOK_STEW,    
            ()->{
                minerFSM.getStateManager().getState(MinerStateEnum.EATING).setEnter(true);
                // Из лямбды модифицировать обработчик
                minerFSM.getEventManager().removeHandler(minerHandler);
                //minerHandler.setRemove(true);
            },
            true,
            0,
            0);
        
    }
        
}
