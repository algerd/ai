
package SimpleSoccer.TeamStates;

import SimpleSoccer.SoccerTeam;
import SimpleSoccer.Define;
import common.Debug.DbgConsole;
import common.FSM.State;
import common.Messaging.Telegram;

public class Attacking extends State<SoccerTeam> {

    private static Attacking instance = new Attacking();

    private Attacking() {}

    public static Attacking getInstance() {
        return instance;
    }

    @Override
    public void enter(SoccerTeam team) {
        if (Define.def(Define.DEBUG_TEAM_STATES)) {
            DbgConsole.debugConsole.print(team.name()).print(" entering Attacking state").print("");
        }
        //these define the home regions for this state of each of the players
        final int BlueRegions[] = {1, 12, 14, 6, 4};
        final int RedRegions[] = {16, 3, 5, 9, 13};

        //set up the player's home regions
        if (team.getColor() == SoccerTeam.blue) {
            TeamStates.changePlayerHomeRegions(team, BlueRegions);
        } else {
            TeamStates.changePlayerHomeRegions(team, RedRegions);
        }
        //if a player is in either the Wait or ReturnToHomeRegion states, its
        //steering target must be updated to that of its new home region to enable
        //it to move into the correct position.
        team.updateTargetsOfWaitingPlayers();
    }

    @Override
    public void execute(SoccerTeam team) {
        //if this team is no longer in control change states
        if (!team.isControllingPlayer()) {
            team.getStateMachine().changeState(Defending.getInstance());
            return;
        }
        //calculate the best position for any supporting attacker to move to
        team.determineBestSupportingPosition();
    }

    @Override
    public void exit(SoccerTeam team) {
        //there is no supporting player for defense
        team.setSupportingPlayer(null);
    }

    @Override
    public boolean onMessage(SoccerTeam e, final Telegram t) {
        return false;
    }
}
