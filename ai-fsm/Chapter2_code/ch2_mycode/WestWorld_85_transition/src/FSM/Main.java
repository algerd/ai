
package FSM;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Miner miner = new Miner(EntityEnum.MINER_BOB);
        
        for (int i = 0; i < 50; ++i) {
            miner.update();
            Thread.sleep(100);
        }
    }
}
