package soccer;

import render.renderjs.TimerJS;
import render.renderjs.RenderJS;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import render.renderfx.RenderFX;
import render.renderfx.TimerFX;

public class Main extends Application {
    
    @Override
    public void start(Stage stage) {
                             
        //seed random number generator
        common.Game.RandomUtil.setSeed(0);       
        SoccerPitch soccerPitch = new SoccerPitch();
        
        Canvas canvas = new Canvas();
        //start animation with JavaFx
        if (true) {
            RenderFX renderfx = new RenderFX(canvas, soccerPitch);
            TimerFX timerfx = new TimerFX(renderfx);
            timerfx.start();  
        }
        //start animation with Java-Script
        else {
            // run generation
            Generator generator = new Generator(soccerPitch);
            generator.run();
            RenderJS renderjs = new RenderJS(canvas, soccerPitch);
            TimerJS timerjs = new TimerJS(renderjs);
            timerjs.start();           
        }  
        
        Pane root = new Pane();
        Scene scene = new Scene(root);
        root.getChildren().add(canvas);
		stage.setScene(scene);
		stage.setTitle("Soccer");
		stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
