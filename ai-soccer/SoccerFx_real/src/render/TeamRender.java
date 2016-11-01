
package render;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import soccer.PlayerBase;
import soccer.SoccerTeam;

public class TeamRender implements Renderable {
    
    private List<PlayerRender> playerList = new ArrayList<>();
    private List<PlayerStateRender> playerStateList = new ArrayList<>();
      
    public TeamRender(GraphicsContext gc, SoccerTeam team, Color color) {
        for (PlayerBase player : team.getPlayerList()) {            
            playerList.add(new PlayerRender(gc, player, color));
            playerStateList.add(new PlayerStateRender(gc, player));          
        }
    }
    
    @Override
    public void render(int tact) {
        for (PlayerRender player : playerList) {
            player.render(tact);           
        }
        for (PlayerStateRender playerState : playerStateList) {
            playerState.render(tact);           
        }
    }
    
}
