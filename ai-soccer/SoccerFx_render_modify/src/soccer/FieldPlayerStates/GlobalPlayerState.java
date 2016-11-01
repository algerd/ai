
package soccer.FieldPlayerStates;

import soccer.MessageType;
import soccer.FieldPlayer;
import common.D2.Vector;
import common.FSM.State;
import common.Messaging.MessageDispatcher;
import common.Messaging.Telegram;
import soccer.Params;

public class GlobalPlayerState extends State<FieldPlayer> {

    static GlobalPlayerState instance = new GlobalPlayerState();

    private GlobalPlayerState() {}

    public static GlobalPlayerState getInstance() {
        return instance;
    }

    @Override
    public void enter(FieldPlayer player) {}

    @Override
    public void execute(FieldPlayer player) {
        //if a player is in possession and close to the ball reduce his max speed
        player.setMaxSpeed((player.isBallWithinReceivingRange() && player.isControllingPlayer()) ?
            Params.PLAYER_MAX_SPEED_WITH_BALL :
            Params.PLAYER_MAX_SPEED_WITHOUT_BALL);     
    }

    @Override
    public void exit(FieldPlayer player) {}

    @Override
    public boolean onMessage(FieldPlayer player, final Telegram telegram) {
        switch (telegram.msg) {
            case RECEIVE_BALL: 
                //set the target
                player.getSteering().setTarget((Vector)telegram.extraInfo);
                //change state 
                player.getStateMachine().changeState(ReceiveBall.getInstance());
                return true; 

            case SUPPORT_ATTACKER:
                //if already supporting just return
                if (player.getStateMachine().isInState(SupportAttacker.getInstance())) {
                    return true;
                }
                //set the target to be the best supporting position
                player.getSteering().setTarget(player.getTeam().getSupportSpot());
                //change the state
                player.getStateMachine().changeState(SupportAttacker.getInstance());
                return true;
           
            case WAIT:
                //change the state
                player.getStateMachine().changeState(Wait.getInstance());
                return true;            

            case GO_HOME:
                player.setDefaultHomeRegion();
                player.getStateMachine().changeState(ReturnToHomeRegion.getInstance());
                return true;

            case PASS_TO_ME:
                //get the position of the player requesting the pass 
                FieldPlayer receiver = (FieldPlayer)telegram.extraInfo;
                
                //if the ball is not within kicking range or their is already a 
                //receiving player, this player cannot pass the ball to the player making the request.
                if (player.getTeam().getReceivingPlayer() != null || !player.isBallWithinKickingRange()) {                 
                    return true;
                }
                //make the pass   
                player.getBall().kick(receiver.getPosition().subn(player.getBall().getPosition()), Params.PASS_FORCE_BALL);

                //let the receiver know a pass is coming 
                MessageDispatcher.dispatcher.dispatchMsg(
                        MessageDispatcher.SEND_MSG_IMMEDIATELY,
                        player.getId(),
                        receiver.getId(),
                        MessageType.RECEIVE_BALL,
                        receiver.getPosition());
                //change state   
                player.getStateMachine().changeState(Wait.getInstance());
                player.findSupport();
                return true;
        }
        return false;
    }
}
