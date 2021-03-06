
package FSM.MinersWifeStates;

import FSM.Messages.Telegram;
import FSM.MinersWife;
import FSM.State;
import java.util.Random;

public class DoHouseWork extends State<MinersWife> {

    static final DoHouseWork instance = new DoHouseWork();

    private DoHouseWork() {}

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not allowed");
    }

    public static DoHouseWork getInstance() {
        return instance;
    }

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
