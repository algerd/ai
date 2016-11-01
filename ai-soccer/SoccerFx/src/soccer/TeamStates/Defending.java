
package soccer.TeamStates;

import soccer.SoccerTeam;
import common.FSM.State;
import common.Messaging.Telegram;

public class Defending extends State<SoccerTeam> {

    private static Defending instance = new Defending();

    private Defending() {}

    public static Defending getInstance() {
        return instance;
    }

    @Override
    public void enter(SoccerTeam team) {
        //these define the home regions for this state of each of the players
        final int BlueRegions[] = {1, 6, 8, 3, 5};
        final int RedRegions[] = {16, 9, 11, 12, 14};

        //set up the player's home regions
        if (team.getColor() == SoccerTeam.blue) {
            TeamStates.changePlayerHomeRegions(team, BlueRegions);
        } else {
            TeamStates.changePlayerHomeRegions(team, RedRegions);
        }
        //if a player is in either the Wait or ReturnToHomeRegion states, its
        //steering target must be updated to that of its new home region
        team.updateTargetsOfWaitingPlayers();
    }

    @Override
    public void execute(SoccerTeam team) {
        //if in control change states
        if (team.isControllingPlayer()) {
            team.getStateMachine().changeState(Attacking.getInstance());
            return;
        }
    }

    @Override
    public void exit(SoccerTeam team) {}

    @Override
    public boolean onMessage(SoccerTeam e, final Telegram t) {
        return false;
    }
}
