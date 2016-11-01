
package SimpleSoccer.FieldPlayerStates;

import SimpleSoccer.Define;
import SimpleSoccer.FieldPlayer;
import common.Debug.DbgConsole;
import common.FSM.State;
import common.Game.Region;
import common.Messaging.Telegram;

public class ReturnToHomeRegion extends State<FieldPlayer> {

    private static ReturnToHomeRegion instance = new ReturnToHomeRegion();

    private ReturnToHomeRegion() {}

    public static ReturnToHomeRegion getInstance() {
        return instance;
    }

    @Override
    public void enter(FieldPlayer player) {
        player.getSteering().arriveOn();

        if (!player.getHomeRegion().inside(player.getSteering().getTarget(), Region.halfsize)) {
            player.getSteering().setTarget(player.getHomeRegion().center());
        }
        if (Define.def(Define.PLAYER_STATE_INFO_ON)) {
            DbgConsole.debugConsole.print("Player ").print(player.getId()).print(" enters ReturnToHome state").print("");
        }
    }

    @Override
    public void execute(FieldPlayer player) {
        if (player.getPitch().isGameOn()) {
            //if the ball is nearer this player than any other team member  &&
            //there is not an assigned receiver && the goalkeeper does not gave
            //the ball, go chase it
            if (player.isClosestTeamMemberToBall()
                    && (player.getTeam().getReceivingPlayer() == null)
                    && !player.getPitch().getGoalKeeperHasBall()) {
                player.getStateMachine().changeState(ChaseBall.getInstance());

                return;
            }
        }

        //if game is on and close enough to home, change state to wait and set the 
        //player target to his current position.(so that if he gets jostled out of 
        //position he can move back to it)
        if (player.getPitch().isGameOn() && player.getHomeRegion().inside(player.getPosition(),
                Region.halfsize)) {
            player.getSteering().setTarget(player.getPosition());
            player.getStateMachine().changeState(Wait.getInstance());
        } 
        //if game is not on the player must return much closer to the center of his home region
        else if (!player.getPitch().isGameOn() && player.isAtTarget()) {
            player.getStateMachine().changeState(Wait.getInstance());
        }
    }

    @Override
    public void exit(FieldPlayer player) {
        player.getSteering().arriveOff();
    }

    @Override
    public boolean onMessage(FieldPlayer e, final Telegram t) {
        return false;
    }
}
