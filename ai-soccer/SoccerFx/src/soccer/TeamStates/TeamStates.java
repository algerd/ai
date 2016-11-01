/**
 * State prototypes for soccer team states.
 */
package soccer.TeamStates;

import soccer.SoccerTeam;

public class TeamStates {

    public static void changePlayerHomeRegions(SoccerTeam team, final int NewRegions[]) {
        for (int plyr = 0; plyr < NewRegions.length; ++plyr) {
            team.setPlayerHomeRegion(plyr, NewRegions[plyr]);
        }
    }
}
