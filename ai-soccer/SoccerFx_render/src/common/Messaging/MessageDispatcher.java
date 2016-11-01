
package common.Messaging;

import soccer.MessageType;
import soccer.BaseGameEntity;
import java.util.TreeSet;
import common.Game.EntityManager;
import soccer.SoccerPitch;

/**
 * A message dispatcher. Manages messages of the type Telegram.
 */ 
public class MessageDispatcher {

    final public static MessageDispatcher dispatcher = new MessageDispatcher();
    public static final int SEND_MSG_IMMEDIATELY = 0;
    public static final int NO_ADDITIONAL_INFO = 0;
    public static final int SENDER_ID_IRRELEVANT = -1;
    //Messages are sorted by their dispatch time.
    private TreeSet<Telegram> priorityQ = new TreeSet<>();

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
        receiver.handleMessage(telegram);             
    }

    /**
     * given a message, a receiver, a sender and any time delay, this function
     * routes the message to the correct agent (if no delay) or stores
     * in the message queue to be dispatched at the correct time
     */
    public void dispatchMsg(int delay, int sender, int receiver, MessageType msg) {
        dispatchMsg(delay, sender, receiver, msg, null);
    }

    public void dispatchMsg(int delay, int sender, int receiver, MessageType msg, Object AdditionalInfo) {
        //get a pointer to the receiver and make sure the receiver is valid
        BaseGameEntity pReceiver = EntityManager.EntityMgr.getEntityFromID(receiver);
        if (pReceiver == null) {
            return;
        }
        //create the telegram
        Telegram telegram = new Telegram(0, sender, receiver, msg, AdditionalInfo);                    
        if (delay == 0) {
            //send the telegram to the recipient immediately
            discharge(pReceiver, telegram);
        } 
        else {
            //calculate tact when the telegram should be dispatched
            telegram.dispatchTact = SoccerPitch.tick + delay;
            priorityQ.add(telegram);       
        }
    }

    /**
     * This function dispatches any telegrams with a timestamp that has expired. 
     * Any dispatched telegrams are removed from the queue
     */
    public void dispatchDelayedMessages() {
        //now peek at the queue to see if any telegrams need dispatching.
        while (!priorityQ.isEmpty() && (priorityQ.first().dispatchTact < SoccerPitch.tick) && (priorityQ.first().dispatchTact > 0)) {
            //read the telegram from the front of the queue
            final Telegram telegram = priorityQ.first();
            //find the recipient
            BaseGameEntity pReceiver = EntityManager.EntityMgr.getEntityFromID(telegram.receiver);      
            //send the telegram to the recipient
            discharge(pReceiver, telegram);
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