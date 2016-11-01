
package soccer.FieldPlayerStates;

import common.Messaging.Telegram;
import soccer.FieldPlayer;
import common.D2.Vector;
import common.D2.Transformation;
import common.FSM.State;
import soccer.Params;

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
            double angle = -Math.PI / 4 * player.getTeam().getHomeGoal().getFacing().sign(player.getHeading());
            Transformation.rotateAroundOrigin(direction, angle);

            //задать мячу начальную скорость Params.BALL_VELOCITY_TURN_TACT в направлении direction
            player.getBall().setInitialVelocity(direction, Params.BALL_VELOCITY_TURN_TACT);
        } 
        else {
            player.getBall().setInitialVelocity(player.getTeam().getHomeGoal().getFacing(), Params.BALL_VELOCITY_DRIBBLE_TACT);
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
