
package generator;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import loader.LoadDataTeam;

public class Team implements Updatable {
    
    private final int idTeam;
    private final String name;
    private final Goal ownGoal;
    private final Goal opponentGoal;
    private final Match match;
    private final Color color;
    private final List<Player> playerList = new ArrayList<>();
    
    public Team(int id, Goal ownGoal, Goal opponentGoal, Match match, Color color) {
        this.idTeam = id;
        this.match = match;
        this.ownGoal = ownGoal;
        this.opponentGoal = opponentGoal;
        this.color = color;
        
        // загружаем данные команды
        LoadDataTeam dataTeam = new LoadDataTeam(id);
        name = dataTeam.getName();
        for (int idPlayer : dataTeam.getIdPlayerList()) {
            playerList.add(0, new Player(idPlayer, this));
        }
         
    }
    
    @Override
    public void update() {
        
        for (Player player : playerList) {
            player.update();
        }
        
    }

    public Match getMatch() {
        return match;
    }

    public Color getColor() {
        return color;
    }      

    public List<Player> getPlayerList() {
        return playerList;
    }

    public Goal getOpponentGoal() {
        return opponentGoal;
    }
    
            
}
