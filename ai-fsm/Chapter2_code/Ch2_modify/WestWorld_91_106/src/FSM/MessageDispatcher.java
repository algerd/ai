
package FSM;

import FSM.Messages.Telegram;
import FSM.Utils.Timer;
import java.util.TreeSet;

public class MessageDispatcher {

    final public static double SEND_MSG_IMMEDIATELY = 0.0f;
    final public static Object NO_ADDITIONAL_INFO = null;  
    final private static MessageDispatcher instance = new MessageDispatcher();
    private final TreeSet<Telegram> priorityQ = new TreeSet<>();

    private MessageDispatcher() {}
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not allowed");
    }
    
    public static MessageDispatcher getInstance() {
        return instance;
    }
    
    /**
     * this method is utilized by DispatchMessage or DispatchDelayedMessages.
     * This method calls the message handling member function of the receiving entity, pReceiver, with the newly created telegram
     */
    private void discharge(BaseGameEntity receiver, Telegram msg) {
        if (!receiver.handleMessage(msg)) {
            System.out.println("Message not handled");
        }
    }

    /**
     *  given a message, a receiver, a sender and any time delay , this function
     *  routes the message to the correct agent (if no delay) or stores
     *  in the message queue to be dispatched at the correct time
     */
    public void dispatchMessage(double delay, EntityEnum sender, EntityEnum receiver, MessageEnum msg, Object ExtraInfo) {     
        if (receiver == null) {
            System.out.println("Warning! No Receiver");    
            return;
        }
        Telegram telegram = new Telegram(0, sender, receiver, msg, ExtraInfo);
        double currentTime = Timer.getInstance().getCurrentTime();
        
        //send the telegram to the recipient else calculate the time when the telegram should be dispatched
        if (delay <= 0.0f) {
            System.out.println(
                "Instant telegram dispatched at time: " + currentTime + " by " + sender + " for " + receiver + ". Msg is " + msg         
            );    
            discharge(EntityManager.getInstance().getEntity(receiver), telegram);
        }   
        else {
            telegram.setDispatchTime(currentTime + delay);
            this.priorityQ.add(telegram);
            System.out.println(
                "Delayed telegram from " + sender + " recorded at time " + currentTime + " for "+ receiver + ". Msg is " + msg    
            );
        }
    }

    /**
     * This function dispatches any telegrams with a timestamp that has expired. 
     * Any dispatched telegrams are removed from the queue send out any delayed messages. 
     * This method is called each time through the main game loop.
     */
    public void dispatchDelayedMessages() {
        double currentTime = Timer.getInstance().getCurrentTime();

        while (!this.priorityQ.isEmpty() && (this.priorityQ.last().getDispatchTime() < currentTime) && (this.priorityQ.last().getDispatchTime() > 0)) {
            Telegram telegram = priorityQ.last();
            BaseGameEntity pReceiver = EntityManager.getInstance().getEntity(telegram.receiver);
            System.out.println(
                "Queued telegram ready for dispatch: Sent to " + pReceiver.getEntity() + ". Msg is " + telegram.msg     
            );                   
            //send the telegram to the recipient
            discharge(pReceiver, telegram);
            this.priorityQ.remove(this.priorityQ.last());
        }
    }
}