
package SimpleSoccer.FieldPlayerStates;

import SimpleSoccer.Define;
import SimpleSoccer.FieldPlayer;
import common.Debug.DbgConsole;
import common.FSM.State;
import common.Messaging.Telegram;

public class ChaseBall extends State<FieldPlayer> {

    private static ChaseBall instance = new ChaseBall();

    private ChaseBall() {}

    public static ChaseBall getInstance() {
        return instance;
    }

    @Override
    public void enter(FieldPlayer player) {
        player.getSteering().seekOn();
        if (Define.def(Define.PLAYER_STATE_INFO_ON)) {
            DbgConsole.debugConsole.print("Player ").print(player.getId()).print(" enters chase state").print("");
        }
    }

    @Override
    public void execute(FieldPlayer player) {
        //if the ball is within kicking range the player changes state to KickBall.
        if (player.isBallWithinKickingRange()) {
            player.getStateMachine().changeState(KickBall.getInstance());
            return;
        }

        //if the player is the closest player to the ball then he should keep chasing it
        if (player.isClosestTeamMemberToBall()) {
            player.getSteering().setTarget(player.getBall().getPosition());
            return;
        }

        //if the player is not closest to the ball anymore, he should return back
        //to his home region and wait for another opportunity
        player.getStateMachine().changeState(ReturnToHomeRegion.getInstance());
    }

    @Override
    public void exit(FieldPlayer player) {
        player.getSteering().seekOff();
    }

    @Override
    public boolean onMessage(FieldPlayer e, final Telegram t) {
        return false;
    }
}
