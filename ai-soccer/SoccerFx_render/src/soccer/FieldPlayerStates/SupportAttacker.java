
package soccer.FieldPlayerStates;

import soccer.FieldPlayer;
import soccer.ParamLoader;
import common.D2.Vector;
import common.FSM.State;
import common.Messaging.Telegram;

public class SupportAttacker extends State<FieldPlayer> {

    private static SupportAttacker instance = new SupportAttacker();

    private SupportAttacker() {}

    public static SupportAttacker getInstance() {
        return instance;
    }

    @Override
    public void enter(FieldPlayer player) {
        player.getSteering().arriveOn();
        player.getSteering().setTarget(player.getTeam().getSupportSpot());
    }

    @Override
    public void execute(FieldPlayer player) {
        //if his team loses control go back home
        if (!player.getTeam().isControllingPlayer()) {
            player.getStateMachine().changeState(ReturnToHomeRegion.getInstance());
            return;
        }

        //if the best supporting spot changes, change the steering target
        if (player.getTeam().getSupportSpot() != player.getSteering().getTarget()) {
            player.getSteering().setTarget(player.getTeam().getSupportSpot());
            player.getSteering().arriveOn();
        }

        //if this player has a shot at the goal AND the attacker can pass
        //the ball to him the attacker should pass the ball to this player
        if (player.getTeam().canShoot(player.getPosition(), ParamLoader.getInstance().MaxShootingForce)) {
            player.getTeam().requestPass(player);
        }

        //if this player is located at the support spot and his team still have
        //possession, he should remain still and turn to face the ball
        if (player.isAtTarget()) {
            player.getSteering().arriveOff();

            //the player should keep his eyes on the ball!
            player.trackBall();
            player.setVelocity(new Vector(0, 0));

            //if not threatened by another player request a pass
            if (!player.isThreatened()) {
                player.getTeam().requestPass(player);
            }
        }
    }

    @Override
    public void exit(FieldPlayer player) {
        //set supporting player to null so that the team knows it has to determine a new one.
        player.getTeam().setSupportingPlayer(null);
        player.getSteering().arriveOff();
    }

    @Override
    public boolean onMessage(FieldPlayer e, final Telegram t) {
        return false;
    }
}
