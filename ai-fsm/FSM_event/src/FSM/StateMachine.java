
package FSM;

/**
 * Главный класс, осуществляющий полный контроль над состояниями: 
 *  - вход/выход/действия состояния и переходы между состояниями
 * @author Alex
 * @param <T> FSM.MinerStates.MinerStateEnum | FSM.WifeStates.WifeStateEnum 
 */
public class StateMachine<T> {
    private State<T> currentState;
    private State<T> previousState;
    private StateManager stateManager;
    private EventManager eventManager;
    
    // stateEnum для задания стартового состояния персонажа
    public StateMachine(StateManager msm, T stateEnum) {
        stateManager = msm;
        currentState = previousState = stateManager.getState(stateEnum);
        eventManager = new EventManager();
    }
     
    /**
     * 1. Запустить задержанные обработчики.
     * 2. Исполнить execute() метод текущего состояния 
     * 3. Получить массив возможных переходов в другие состояния из метода getTransitionArray() текушего состояния
     * 4. Проверить условие входа в состояние каждого перехода if (state.enterCondition())
     * 5. Если условие входа в состояние true:
     *  - выйти из текущего состояния через метод exit()
     *  - сделать текущим состоянием состояние перехода, которое имеет true условие входа
     *  - войти в новое текущее состояние через метод enter(previousState)
     * 
     *  execute(), enter() и exit() возвращают Event для запуска обработчиков событий, повешенных на эти методы:
     *  - exitEvent.isHandler() == true - запустить обработчик
     */
    public void update() {
        eventManager.runDelayedHandlers();
        
        Event execEvent = currentState.execute();
        if (execEvent.isHandler()) {
            eventManager.handleEvent(execEvent);
        }
        
        if (currentState.exitCondition()) {
            for (T stateEnum : currentState.getTransitionArray()) {
                State state = stateManager.getState(stateEnum);
                if (state.enterCondition()) {         
                    previousState = currentState;
                    
                    Event exitEvent = currentState.exit();
                    if (exitEvent.isHandler()) {
                        eventManager.handleEvent(exitEvent);
                    }            
                    currentState = state;
                    
                    Event enterEvent = currentState.enter(previousState);
                    if (enterEvent.isHandler()) {
                        eventManager.handleEvent(enterEvent);
                    }      
                    break;
                }
            }           
        }
    }

    public EventManager<T> getEventManager() {
        return eventManager;
    }

    public StateManager getStateManager() {
        return stateManager;
    }
           
}
