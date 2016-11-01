
package FSM;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Miner bob = new Miner(EntityEnum.MINER_BOB);
        MinersWife elsa = new MinersWife(EntityEnum.ELSA);
        
        //register them with the entity manager
        EntityManager.getInstance().registerEntity(bob);
        EntityManager.getInstance().registerEntity(elsa);
        
        for (int i = 0; i < 20; ++i) {
            bob.update();
            elsa.update();
            //dispatch any delayed messages
            MessageDispatcher.getInstance().dispatchDelayedMessages();
                        
            Thread.sleep(800);
        }
    }
}
