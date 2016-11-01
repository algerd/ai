
package generator;

import javafx.scene.paint.Color;
import loader.LoadDataMatch;

public class Match implements Updatable {
    
    private final int idMatch;   
    private final Ball ball;
    private final Goal leftGoal;
    private final Goal rightGoal;
    private final Team homeTeam;
    private final Team guestTeam;
    
    public Match(int idMatch) {  
        this.idMatch = idMatch;
        
        leftGoal = new Goal(Goal.GoalSide.LEFT);
        rightGoal = new Goal(Goal.GoalSide.RIGHT);                       
        ball = new Ball(); 
        
        // загрузить данные матча
        LoadDataMatch dataMatch = new LoadDataMatch(idMatch);       
        homeTeam = new Team(dataMatch.getIdHomeTeam(), leftGoal, rightGoal, this, Color.RED); 
        guestTeam = new Team(dataMatch.getIdGuestTeam(), rightGoal, leftGoal, this, Color.BLUE);
    }
    
    @Override
    public void update() {
        ball.update();
        homeTeam.update();
        guestTeam.update();
            
    }

    public Ball getBall() {
        return ball;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getGuestTeam() {
        return guestTeam;
    }
                   
}
