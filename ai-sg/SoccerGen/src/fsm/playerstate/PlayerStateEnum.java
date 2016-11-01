
package fsm.playerstate;

import fsm.State;
import generator.Player;
import java.util.function.Function;

public enum PlayerStateEnum {
    
    WAIT(Wait::new),
    PASS(Pass::new ),
    TAKE_BALL(TakeBall::new),
    CHASE_BALL(ChaseBall::new);
    
    private final Function<Player,State> closure;
    
    private PlayerStateEnum(Function<Player,State> callback) {
        this.closure = callback;    
    }
    
    /**
     * Get new instance of State abstract class.
     */
    public State get(Player player) {
        return closure.apply(player);
    }
    
}
