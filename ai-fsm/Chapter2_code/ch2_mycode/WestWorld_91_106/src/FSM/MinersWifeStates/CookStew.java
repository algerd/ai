
package FSM.MinersWifeStates;

import FSM.EntityEnum;
import FSM.MessageDispatcher;
import FSM.MessageEnum;
import FSM.Messages.Telegram;
import FSM.MinersWife;
import FSM.State;
import FSM.Utils.Timer;

class CookStew extends State<MinersWife> {

    static final CookStew instance = new CookStew();
    private boolean isCooking;

    private CookStew() {}

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not allowed");
    }

    public static CookStew getInstance() {
        return instance;
    }

    @Override
    public void enter(MinersWife wife) {
        if (!this.isCooking) {
            System.out.println(wife.getEntity() + ": Putting the stew in the oven");
            MessageDispatcher.getInstance().dispatchMessage(1.5,                        //time delay
                wife.getEntity(),           //sender
                wife.getEntity(),           //recipient
                MessageEnum.STEW_READY,    //the message
                MessageDispatcher.NO_ADDITIONAL_INFO
            );
            this.isCooking = true;
        }
    }

    @Override
    public void execute(MinersWife wife) {
        System.out.println(wife.getEntity() + ": Fussin' over food");
    }

    @Override
    public void exit(MinersWife wife) {
        System.out.println(wife.getEntity() + ": Puttin' the stew on the table");
    }

    @Override
    public boolean onMessage(MinersWife wife, Telegram msg) {
        switch (msg.msg) {
            case STEW_READY: {
                System.out.println("Message received by " + wife.getEntity() + " at time: " + Timer.getInstance().getCurrentTime());
                System.out.println(wife.getEntity() + ": StewReady! Lets eat");
                //let hubby know the stew is ready
                MessageDispatcher.getInstance().dispatchMessage(MessageDispatcher.SEND_MSG_IMMEDIATELY, //time delay
                    wife.getEntity(),                       //sender
                    EntityEnum.MINER_BOB,                   //recipient
                    MessageEnum.STEW_READY,                //the message
                    MessageDispatcher.NO_ADDITIONAL_INFO
                );
                this.isCooking = false;
                wife.getStateMachine().changeState(DoHouseWork.getInstance());
            }
            return true;
        }
        return false;
    }
}
