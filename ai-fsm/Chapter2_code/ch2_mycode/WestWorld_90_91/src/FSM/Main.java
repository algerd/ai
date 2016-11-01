
package FSM;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Miner miner = new Miner(EntityEnum.MINER_BOB);
        MinersWife elsa = new MinersWife(EntityEnum.ELSA);
        
        for (int i = 0; i < 20; ++i) {
            miner.update();
            elsa.update();
            Thread.sleep(800);
        }
    }
}
