
package render.renderjs;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import soccer.PlayerBase;
import soccer.SoccerTeam;

public class TeamRenderJS implements Renderable {
    
    private List<PlayerRenderJS> playerList = new ArrayList<>();
    private List<PlayerStateRenderJS> playerStateList = new ArrayList<>();
      
    public TeamRenderJS(GraphicsContext gc, SoccerTeam team, Color color) {
        for (PlayerBase player : team.getPlayerList()) {            
            playerList.add(new PlayerRenderJS(gc, player, color));
            playerStateList.add(new PlayerStateRenderJS(gc, player));          
        }
    }
    
    @Override
    public void render(int tact) {
        for (PlayerRenderJS player : playerList) {
            player.render(tact);           
        }
        for (PlayerStateRenderJS playerState : playerStateList) {
            playerState.render(tact);           
        }
    }
    
}
