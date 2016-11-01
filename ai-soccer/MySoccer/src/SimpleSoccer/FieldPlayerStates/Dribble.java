
package SimpleSoccer.FieldPlayerStates;

import common.Messaging.Telegram;
import SimpleSoccer.FieldPlayer;
import SimpleSoccer.ParamLoader;
import common.D2.Vector;
import common.D2.Transformation;
import common.FSM.State;
import common.misc.Utils;

public class Dribble extends State<FieldPlayer> {

    private static Dribble instance = new Dribble();

    private Dribble() {}

    public static Dribble getInstance() {
        return instance;
    }

    @Override
    public void enter(FieldPlayer player) {
        //let the team know this player is controlling
        player.getTeam().setControllingPlayer(player);
    }

    @Override
    public void execute(FieldPlayer player) {
        double dot = player.getTeam().getHomeGoal().getFacing().dot(player.getHeading());

        //if the ball is between the player and the home goal, it needs to swivel
        // the ball around by doing multiple small kicks and turns until the player 
        //is facing in the correct direction
        if (dot < 0) {
            //the player's heading is going to be rotated by a small amount (Pi/4) 
            //and then the ball will be kicked in that direction
            Vector direction = player.getHeading();

            //calculate the sign (+/-) of the angle between the player heading and the 
            //facing direction of the goal so that the player rotates around in the correct direction
            double angle = Utils.QuarterPi * -1 * player.getTeam().getHomeGoal().getFacing().sign(player.getHeading());

            Transformation.rotateAroundOrigin(direction, angle);

            //this value works well whjen the player is attempting to control the ball and turn at the same time
            final double KickingForce = 0.8;

            player.getBall().kick(direction, KickingForce);
        } 
        //kick the ball down the field
        else {
            player.getBall().kick(
                player.getTeam().getHomeGoal().getFacing(),
                ParamLoader.getInstance().MaxDribbleForce);
        }
        //the player has kicked the ball so he must now change state to follow it
        player.getStateMachine().changeState(ChaseBall.getInstance());
        return;
    }

    @Override
    public void exit(FieldPlayer player) {
    }

    @Override
    public boolean onMessage(FieldPlayer e, final Telegram t) {
        return false;
    }
}
