
package soccer.TeamStates;

import soccer.SoccerTeam;
import common.FSM.State;
import common.Messaging.Telegram;

public class PrepareForKickOff extends State<SoccerTeam> {

    private static PrepareForKickOff instance = new PrepareForKickOff();

    private PrepareForKickOff() {}

    public static PrepareForKickOff getInstance() {
        return instance;
    }

    @Override
    public void enter(SoccerTeam team) {
        //reset key player pointers
        team.setControllingPlayer(null);
        team.setSupportingPlayer(null);
        team.setReceivingPlayer(null);
        team.setPlayerClosestToBall(null);
        //send Msg_GoHome to each player.
        team.returnAllFieldPlayersToHome();
    }

    @Override
    public void execute(SoccerTeam team) {
        //if both teams in position, start the game
        if (team.isAllPlayersAtHome() && team.getOpponent().isAllPlayersAtHome()) {
            team.getStateMachine().changeState(Defending.getInstance());
        }
    }

    @Override
    public void exit(SoccerTeam team) {
        team.getPitch().gameOn();
    }

    @Override
    public boolean onMessage(SoccerTeam e, final Telegram t) {
        return false;
    }
}
