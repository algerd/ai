
package common.Messaging;

import soccer.MessageType;

public class Telegram implements Comparable {
    
    public int sender;
    public int receiver;
    public MessageType msg;
    public int dispatchTact;
    public Object extraInfo;

    public Telegram() {
        dispatchTact = -1;
        sender = -1;
        receiver = -1;
        msg = null;
    }

    public Telegram(int delay, int sender, int receiver, MessageType msg) {
        this(delay, sender, receiver, msg, null);
    }

    public Telegram(int delay, int sender, int receiver, MessageType msg, Object info) {
        this.dispatchTact = delay;
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.extraInfo = info;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Telegram)) {
            return false;
        }
        Telegram t1 = this;
        Telegram t2 = (Telegram) o;
        return (t1.dispatchTact == t2.dispatchTact)
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
		hash = 53 * hash + sender;
		hash = 53 * hash + receiver;
		hash = 53 * hash + (msg != null ? msg.hashCode() : 0);
        hash = 53 * hash + (int)(Double.doubleToLongBits(dispatchTact) ^ (Double.doubleToLongBits(dispatchTact) >>> 32));
		hash = 97 * hash + (extraInfo == null ? 0 : extraInfo.hashCode());
		return hash;
	}

    @Override
    public int compareTo(Object o2) {
        Telegram t1 = this;
        Telegram t2 = (Telegram) o2;
        if (t1.dispatchTact == t2.dispatchTact) {
            return 0;
        } 
        else {
            return (t1.dispatchTact < t2.dispatchTact) ? -1 : 1;
        }
    }

    @Override
    public String toString() {
        return "time: " + dispatchTact + "  Sender: " + sender + "   Receiver: " + receiver + "   Msg: " + msg;
    }

}