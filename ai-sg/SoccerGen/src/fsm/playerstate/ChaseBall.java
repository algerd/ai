
package fsm.playerstate;

import fsm.State;
import generator.Player;
import generator.PlayerMotion.MotionType;

public class ChaseBall extends State {
    
    private final Player player;
    
    public ChaseBall(Player player) {
        this.player = player;
    }
    
    @Override
    public void enter() {
        player.getPlayerMotion().setFlag(MotionType.RUN);
    }
    
    @Override
    public void execute() {
        player.getPlayerMotion().setTarget(player.getBall().getPos().getVectorXY());
        // если мяч в пределах приёма игроком - изменить состояние на TakeBall
        if (player.isBallWithinTakeRange()) {
            player.getStateMachine().changeState(PlayerStateEnum.TAKE_BALL);
        }
    }
    
    @Override
    public void exit() {
       player.getPlayerMotion().setFlag(MotionType.NONE); 
    }
    
}
