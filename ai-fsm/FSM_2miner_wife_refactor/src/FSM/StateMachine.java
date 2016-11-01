
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
    
    private StateMachine() {}
    
    public StateMachine(StateManager msm, T stateEnum) {
        stateManager = msm;
        currentState = previousState = stateManager.getState(stateEnum);
    }
     
    /**
     * 1. Исполнить execute() метод текущего состояния 
     * 2. Получить массив возможных переходов в другие состояния из метода getTransitionArray() текушего состояния
     * 3. Проверить условие входа в состояние каждого перехода if (state.enterCondition())
     * 4. Если условие входа в состояние true:
     *  - выйти из текущего состояния через метод exit()
     *  - сделать текущим состоянием состояние перехода, которое имеет true условие входа
     *  - войти в новое текущее состояние через метод enter(previousState)
     */
    public void update() {
        currentState.execute();
        if (currentState.exitCondition()){
            for (T stateEnum : currentState.getTransitionArray()) {
                State state = stateManager.getState(stateEnum);
                if (state.enterCondition()){         
                    previousState = currentState;
                    currentState.exit();
                    currentState = state;
                    currentState.enter(previousState); 
                    break;
                }
            }           
        }
    }
      
}
