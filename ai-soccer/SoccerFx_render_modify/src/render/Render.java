
package render;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import soccer.SoccerPitch;

public class Render {
    SoccerPitch soccerPitch; // в тестовых целях
    private GraphicsContext gc;
    public int tact;
    private FieldRender field;
    private BallRender ball;
    private TeamRender redTeam;
    private TeamRender blueTeam;
           
    public Render(Canvas canvas, SoccerPitch soccerPitch) { 
        this.soccerPitch = soccerPitch; // в тестовых целях
        gc = canvas.getGraphicsContext2D();  
        field = new FieldRender(gc);
        //ball = new BallRender(gc, soccerPitch.getBall());
        //redTeam = new TeamRender(gc, soccerPitch.getRedTeam(), Color.RED);
        //blueTeam = new TeamRender(gc, soccerPitch.getBlueTeam(), Color.BLUE);
    }

    /**
     * This method is called every tact of animation timer.
     */
    public void update() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        /*
        field.render(tact);
        ball.render(tact);
        redTeam.render(tact);
        blueTeam.render(tact);     
        ++tact;
        */      
        soccerPitch.render(gc); // в тестовых целях
        soccerPitch.update();   // в тестовых целях  
    }
      
}
