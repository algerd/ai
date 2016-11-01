
package render;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import soccer.SoccerPitch;

public class Render {
    //SoccerPitch soccerPitch; // в тестовых целях
    private GraphicsContext gc;
    public int tact;
    private Field field;
    private Ball ball;
    private Team redTeam;
    private Team blueTeam;
           
    public Render(Canvas canvas, SoccerPitch soccerPitch) { 
        //this.soccerPitch = soccerPitch; // в тестовых целях
        gc = canvas.getGraphicsContext2D();  
        field = new Field(gc);
        ball = new Ball(gc, soccerPitch.getBall());
        redTeam = new Team(gc, soccerPitch.getRedTeam(), Color.RED);
        blueTeam = new Team(gc, soccerPitch.getBlueTeam(), Color.BLUE);
    }

    /**
     * This method is called every tact of animation timer.
     */
    public void update() {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
       
        field.render(tact);
        ball.render(tact);
        redTeam.render(tact);
        blueTeam.render(tact);     
        ++tact;
               
        //soccerPitch.render(gc); // в тестовых целях
        //soccerPitch.update();   // в тестовых целях  
    }
      
}
