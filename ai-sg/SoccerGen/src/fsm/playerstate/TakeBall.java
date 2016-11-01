
package fsm.playerstate;

import fsm.State;
import generator.Player;

public class TakeBall extends State  {
    private Player player;
    
    public TakeBall(Player player) {
        this.player = player;
    }
    
    @Override
    public void enter() {
        
    }
    
    @Override
    public void execute() {
        if (player.isReadyToKick()) {
            player.getStateMachine().changeState(PlayerStateEnum.PASS);
        }
    }
    
    @Override
    public void exit() {
        
    }
}
