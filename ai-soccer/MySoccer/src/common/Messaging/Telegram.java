
package common.Messaging;

import SimpleSoccer.MessageType;

public class Telegram implements Comparable {
    //the entity that sent this telegram
    public int sender;
    //the entity that is to receive this telegram
    public int receiver;
    //the message itself. These are all enumerated in the file
    //"MessageTypes.h"
    public MessageType msg;
    //messages can be dispatched immediately or delayed for a specified amount
    //of time. If a delay is necessary this field is stamped with the time 
    //the message should be dispatched.
    public double dispatchTime;
    //any additional information that may accompany the message
    public Object ExtraInfo;
    //these telegrams will be stored in a priority queue. Therefore the >
    //operator needs to be overloaded so that the PQ can sort the telegrams
    //by time priority. Note how the times must be smaller than
    //SmallestDelay apart before two Telegrams are considered unique.
    public final static double SmallestDelay = 0.25;

    public Telegram() {
        dispatchTime = -1;
        sender = -1;
        receiver = -1;
        msg = null;
    }

    public Telegram(double time, int sender, int receiver, MessageType msg) {
        this(time, sender, receiver, msg, null);
    }

    public Telegram(double time, int sender, int receiver, MessageType msg, Object info) {
        this.dispatchTime = time;
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.ExtraInfo = info;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Telegram)) {
            return false;
        }
        Telegram t1 = this;
        Telegram t2 = (Telegram) o;
        return (Math.abs(t1.dispatchTime - t2.dispatchTime) < SmallestDelay)
                && (t1.sender == t2.sender)
                && (t1.receiver == t2.receiver)
                && (t1.msg == t2.msg || (t1.msg == null && t2.msg == null));
    }

    /**
     *  It is generally necessary to override the hashCode method 
     *  whenever equals method is overridden, so as to maintain the 
     *  general contract for the hashCode method, which states that 
     *  equal objects must have equal hash codes.
     */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 53 * hash + this.sender;
		hash = 53 * hash + this.receiver;
		hash = 53 * hash + (this.msg != null ? this.msg.hashCode() : 0);
		double DispatchTime = this.dispatchTime - (this.dispatchTime % this.SmallestDelay);
        hash = 53 * hash + (int) (Double.doubleToLongBits(DispatchTime) ^ (Double.doubleToLongBits(DispatchTime) >>> 32));
		hash = 97 * hash + (this.ExtraInfo == null ? 0 : this.ExtraInfo.hashCode());
		return hash;
	}

    @Override
    public int compareTo(Object o2) {
        Telegram t1 = this;
        Telegram t2 = (Telegram) o2;
        if (Math.abs(t1.dispatchTime - t2.dispatchTime) < SmallestDelay) {
            return t1.hashCode() - t2.hashCode(); // equals objects return 0
        } 
        else {
            return (t1.dispatchTime < t2.dispatchTime) ? -1 : 1;
        }
    }

    @Override
    public String toString() {
        return "time: " + dispatchTime + "  Sender: " + sender + "   Receiver: " + receiver + "   Msg: " + msg;
    }

    /**
     * handy helper function for dereferencing the ExtraInfo field of the Telegram 
     * to the required type.
     */
    public static <T> T dereferenceToType(Object p) {
        return (T) (p);
    }
    
}