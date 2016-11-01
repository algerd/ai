
package FSM.Messages;

import FSM.EntityEnum;
import FSM.MessageEnum;

public class Telegram implements Comparable {
    private static int counter;
    /**
     * these telegrams will be stored in a priority queue. 
     * Therefore the > operator needs to be overloaded so that the PQ can sort the telegrams by time priority. 
     * Note how the times must be smaller than SmallestDelay apart before two Telegrams are considered unique.
     */
    public final static double SMALLEST_DELAY = 0.25;   
    /**
     * messages can be dispatched immediately or delayed for a specified amount of time. 
     * If a delay is necessary this field is stamped with the time the message should be dispatched.
     */
    public double dispatchTime = -1;
    /** the entity that sent this telegram. */
    public EntityEnum sender;
    /** the entity that is to receive this telegram. */
    public EntityEnum receiver;
    /** the message itself. */
    public MessageEnum msg;  
    /** any additional information that may accompany the message. */
    public Object extraInfo;

    public Telegram() {
        counter++;
    }

    public Telegram(double time, EntityEnum sender, EntityEnum receiver, MessageEnum msg) {
        this(time, sender, receiver, msg, null);
    }

    public Telegram(double time, EntityEnum sender, EntityEnum receiver, MessageEnum msg, Object info) {
        this();
        this.dispatchTime = time;
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.extraInfo = info;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Telegram)) {
            return false;
        }
        Telegram telegr = (Telegram)obj;
        return 
            (Math.abs(this.dispatchTime - telegr.dispatchTime) < SMALLEST_DELAY)
            && (this.sender == telegr.sender)
            && (this.receiver == telegr.receiver)
            && (this.msg == telegr.msg);
    }
    
    public void setDispatchTime(double time) {
        this.dispatchTime = time;
    }
    
    public double getDispatchTime() {
        return this.dispatchTime;
    }

    /**
     *  It is generally necessary to override the hashCode method whenever equals method is overridden, 
     *  so as to maintain the general contract for the hashCode method, which states that 
     *  equal objects must have equal hash codes.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + counter;
        return hash;
    }
    
    @Override
    public int compareTo(Object obj) {
        Telegram t2 = (Telegram)obj;
        if (this == t2) {
            return 0;
        } else {
            return (this.dispatchTime < t2.dispatchTime) ? -1 : 1;
        }
    }

    @Override
    public String toString() {
        return 
            "time: " + this.dispatchTime + 
            "  Sender: " + this.sender + 
            "   Receiver: " + this.receiver + 
            "   Msg: " + this.msg;
    }

    /**
     * handy helper function for dereferencing the ExtraInfo field of the Telegram to the required type.
     */
    public static <T> T DereferenceToType(Object p) {
        return (T) (p);
    }
}