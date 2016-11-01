
package soccer.FieldPlayerStates;

import soccer.FieldPlayer;
import common.D2.Vector;
import common.FSM.State;
import common.Messaging.Telegram;

public class Wait extends State<FieldPlayer> {

    private static Wait instance = new Wait();

    private Wait() {}

    public static Wait getInstance() {
        return instance;
    }

    @Override
    public void enter(FieldPlayer player) {
        //if the game is not on make sure the target is the center of the player's home region. 
        //This is ensure all the players are in the correct positions ready for kick off
        if (!player.getPitch().isGameOn()) {
            player.getSteering().setTarget(player.getHomeRegion().center());
        }
    }

    @Override
    public void execute(FieldPlayer player) {
        //if the player has been jostled out of position, get back in position  
        if (!player.isAtTarget()) {
            player.getSteering().arriveOn();
            return;
        } 
        else {
            player.getSteering().arriveOff();
            player.setVelocity(new Vector(0, 0));
            //the player should keep his eyes on the ball!
            player.trackBall();
        }

        //if this player's team is controlling AND this player is not the attacker
        //AND is further up the field than the attacker he should request a pass.
        if (player.getTeam().isControllingPlayer()
                && (!player.isControllingPlayer())
                && player.isAheadOfAttacker()) {
            player.getTeam().requestPass(player);
            return;
        }

        if (player.getPitch().isGameOn()) {
            //if the ball is nearer this player than any other team member  AND
            //there is not an assigned receiver AND neither goalkeeper has the ball, go chase it
            if (player.isClosestTeamMemberToBall()
                    && player.getTeam().getReceivingPlayer() == null
                    && !player.getPitch().getGoalKeeperHasBall()) {
                player.getStateMachine().changeState(ChaseBall.getInstance());
                return;
            }
        }
    }

    @Override
    public void exit(FieldPlayer player) {
    }

    @Override
    public boolean onMessage(FieldPlayer e, final Telegram t) {
        return false;
    }
}
