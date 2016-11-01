
package FSM.MinersWifeStates;

import FSM.Telegram;
import FSM.MinersWife;
import FSM.State;
import java.util.Random;

class DoHouseWork extends State<MinersWife> {
    public DoHouseWork() {}

    @Override
    public void enter(MinersWife wife) {}

    @Override
    public void execute(MinersWife wife) { 
        switch (new Random().nextInt(3)) {
            case 0:
                System.out.println(wife.getEntity() + ": Moppin' the floor");
                break;
            case 1:
                System.out.println(wife.getEntity() + ": Washin' the dishes");
                break;
            case 2:
                System.out.println(wife.getEntity() + ": Makin' the bed");
                break;
        }
    }

    @Override
    public void exit(MinersWife wife) {}
    
    @Override
    public boolean onMessage(MinersWife wife, Telegram msg) {
        return false;
    }
    
}
