
package fsm.playerstate;

import fsm.State;
import generator.Player;
import utils.Vector;
import utils.Vector3D;

public class Pass extends State {
    
    private Player player;
    
    public Pass(Player player) {
        this.player = player;
    }
    
    @Override
    public void enter() {
        
    }
    
    @Override
    public void execute() {
        player.resetKickTimer();
        player.getBall().pass(new Vector3D(20, 20, 1), 0.75);
        
        player.getStateMachine().changeState(PlayerStateEnum.WAIT);
    }
    
    @Override
    public void exit() {
        
    }
    
}
