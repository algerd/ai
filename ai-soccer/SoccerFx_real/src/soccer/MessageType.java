
package soccer;

public enum MessageType {

    RECEIVE_BALL,
    PASS_TO_ME,
    SUPPORT_ATTACKER,
    GO_HOME,
    WAIT;

    @Override
    public String toString() {
        switch (this) {
            case RECEIVE_BALL:
                return "Msg_ReceiveBall";

            case PASS_TO_ME:
                return "Msg_PassToMe";

            case SUPPORT_ATTACKER:
                return "Msg_SupportAttacker";

            case GO_HOME:
                return "Msg_GoHome";

            case WAIT:
                return "Msg_Wait";

            default:
                return "INVALID MESSAGE!!";
        }
        
    }

}