
package common.Messaging;

import SimpleSoccer.MessageType;
import SimpleSoccer.BaseGameEntity;
import SimpleSoccer.Define;
import java.util.TreeSet;
import common.Game.EntityManager;
import common.misc.FrameCounter;
import common.Debug.DbgConsole;

/**
 * A message dispatcher. Manages messages of the type Telegram.
 */ 
public class MessageDispatcher {

    static {
        //uncomment below to send message info to the debug window
        //Define.define(Define.SHOW_MESSAGING_INFO);
    }
    //to make life easier...
    final public static MessageDispatcher dispatcher = new MessageDispatcher();
    //to make code easier to read
    public static final double SEND_MSG_IMMEDIATELY = 0.0;
    public static final int NO_ADDITIONAL_INFO = 0;
    public static final int SENDER_ID_IRRELEVANT = -1;
    //a std::set is used as the container for the delayed messages
    //because of the benefit of automatic sorting and avoidance
    //of duplicates. Messages are sorted by their dispatch time.
    private TreeSet<Telegram> priorityQ = new TreeSet<Telegram>();

    private MessageDispatcher() {}

    public static MessageDispatcher getInstance() {
        return dispatcher;
    }
    
    /** 
     * this method is utilized by DispatchMsg or DispatchDelayedMessages.
     * This method calls the message handling member function of the receiving
     * entity, pReceiver, with the newly created telegram
     */
    private void discharge(BaseGameEntity receiver, Telegram telegram) {
        if (!receiver.handleMessage(telegram)) {
            //telegram could not be handled
            if (Define.def(Define.SHOW_MESSAGING_INFO)) {
                DbgConsole.debugConsole.print("Message not handled").print("");
            }
        }
    }

    /**
     * given a message, a receiver, a sender and any time delay, this function
     * routes the message to the correct agent (if no delay) or stores
     * in the message queue to be dispatched at the correct time
     */
    public void dispatchMsg(double delay, int sender, int receiver, MessageType msg) {
        dispatchMsg(delay, sender, receiver, msg, null);
    }

    public void dispatchMsg(double delay, int sender, int receiver, MessageType msg, Object AdditionalInfo) {

        //get a pointer to the receiver
        BaseGameEntity pReceiver = EntityManager.EntityMgr.getEntityFromID(receiver);
        //make sure the receiver is valid
        if (pReceiver == null) {
            if (Define.def(Define.SHOW_MESSAGING_INFO)) {
                DbgConsole.debugConsole.print("\nWarning! No Receiver with ID of ").print(receiver).print(" found").print("");
            }
            return;
        }

        //create the telegram
        Telegram telegram = new Telegram(0, sender, receiver, msg, AdditionalInfo);

        //if there is no delay, route telegram immediately                       
        if (delay <= 0.0) {
            if (Define.def(Define.SHOW_MESSAGING_INFO)) {
                DbgConsole.debugConsole.print("\nTelegram dispatched at time: ").print(FrameCounter.tickCounter.getCurrentFrame()).print(" by ").print(sender).print(" for ").print(receiver).print(". Msg is ").print(msg).print("");
            }
            //send the telegram to the recipient
            discharge(pReceiver, telegram);
        } 
        //else calculate the time when the telegram should be dispatched
        else {
            double CurrentTime = FrameCounter.tickCounter.getCurrentFrame();
            telegram.dispatchTime = CurrentTime + delay;
            //and put it in the queue
            priorityQ.add(telegram);
            
            if (Define.def(Define.SHOW_MESSAGING_INFO)) {
                DbgConsole.debugConsole.print("\nDelayed telegram from ").print(sender).print(" recorded at time ").print(FrameCounter.tickCounter.getCurrentFrame()).print(" for ").print(receiver).print(". Msg is ").print(msg).print("");
            }
        }
    }

    /**
     * This function dispatches any telegrams with a timestamp that has expired. 
     * Any dispatched telegrams are removed from the queue
     */
    public void dispatchDelayedMessages() {
        //first get current time
        double currentTime = FrameCounter.tickCounter.getCurrentFrame();

        //now peek at the queue to see if any telegrams need dispatching.
        //remove all telegrams from the front of the queue that have gone past their sell by date
        while (!priorityQ.isEmpty()
                && (priorityQ.first().dispatchTime < currentTime)
                && (priorityQ.first().dispatchTime > 0)) {
            //read the telegram from the front of the queue
            final Telegram telegram = priorityQ.first();

            //find the recipient
            BaseGameEntity pReceiver = EntityManager.EntityMgr.getEntityFromID(telegram.receiver);

            if (Define.def(Define.SHOW_MESSAGING_INFO)) {
                DbgConsole.debugConsole.print("\nQueued telegram ready for dispatch: Sent to ").print(pReceiver.getId()).print(". Msg is ").print(telegram.msg).print("");
            }
            //send the telegram to the recipient
            discharge(pReceiver, telegram);
            //remove it from the queue
            priorityQ.remove(priorityQ.first());
        }
    }
    /**
     * Count of messages in the queue.
     */
    public int size() {
        return priorityQ.size();
    }

    /**
     * Clear dispatcher messages.
     */
    public void clear() {
        priorityQ.clear();
    }
}