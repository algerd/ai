
package FSM.EventLogic;


public class MainLogic {
    
    public MainLogic() {}
    /**
     * Запуск всех логических классов в логическом порядке
     */
    public void launch() {
        // приготовить утку!
        new CookStew().run();
    }
    
}
