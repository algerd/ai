
package FSM.MinerStates;

import FSM.EntityEnum;
import FSM.Miner;
import FSM.State;
import FSM.LocationEnum;
import FSM.MessageDispatcher;
import FSM.MessageEnum;
import FSM.Telegram;
import FSM.Utils.Timer;

/**
 * Miner will go home and sleep until his fatigue is decreased sufficiently.
 */
class GoHomeAndSleepTilRested extends State<Miner> {
    public GoHomeAndSleepTilRested() {}

    @Override
    public void enter(Miner miner) {
        if (miner.getLocation() != LocationEnum.SHACK) {
            miner.setLocation(LocationEnum.SHACK);
            System.out.println(miner.getEntity() + ": " + "Walkin' home");
            
            //let the wife know I'm home
            MessageDispatcher.getInstance().dispatchMessage(MessageDispatcher.SEND_MSG_IMMEDIATELY, //time delay
                miner.getEntity(),                      //sender
                EntityEnum.ELSA,                        //recipient
                MessageEnum.IM_HOME,                   //the message
                MessageDispatcher.NO_ADDITIONAL_INFO
            );
        }
    }

    @Override
    public void execute(Miner miner) {
        //if miner is not fatigued start to dig for nuggets again.
        if (!miner.isFatigued()) {
            miner.getStateMachine().changeState(MinerStateManagerEnum.DIG_NUGGET.getState());
            System.out.println(miner.getEntity() + ": " + "What a God darn fantastic nap! Time to find more gold");
        } 
        //sleep
        else {           
            miner.decreaseFatigue();
            System.out.println(miner.getEntity() + ": " + "ZZZZ... ");
        }
    }

    @Override
    public void exit(Miner miner) {
        System.out.println(miner.getEntity() + ": " + "Leaving the house");
    }
    
    @Override
    public boolean onMessage(Miner miner, Telegram msg) {
        switch (msg.getMsg()) {
            case STEW_READY:
                System.out.println("Message handled by " + miner.getEntity() + " at time: " + Timer.getInstance().getCurrentTime());
                System.out.println(miner.getEntity() + ": Okay Hun, ahm a comin'!");
                miner.getStateMachine().changeState(MinerStateManagerEnum.EAT_STEW.getState());
                return true;
        }
        return false;
    }
    
}
