
package render;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import soccer.PlayerBase;
import soccer.SoccerTeam;

public class Team implements Renderable {
    
    private List<Player> playerList = new ArrayList<>();
    private List<PlayerState> playerStateList = new ArrayList<>();
      
    public Team(GraphicsContext gc, SoccerTeam team, Color color) {
        for (PlayerBase player : team.getPlayerList()) {            
            playerList.add(new Player(gc, player, color));
            playerStateList.add(new PlayerState(gc, player));          
        }
    }
    
    @Override
    public void render(int tact) {
        for (Player player : playerList) {
            player.render(tact);           
        }
        for (PlayerState playerState : playerStateList) {
            playerState.render(tact);           
        }
    }
    
}
