package generator;

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
                                  
        Match match = new Match(1);
        
        Canvas canvas = new Canvas();
        //start animation step by step
        if (true) {
            RenderFX renderfx = new RenderFX(canvas, match);
            TimerFX timerfx = new TimerFX(renderfx);
            timerfx.start();  
        }
        //start animation overall
        else {
            // run generation
            Generator generator = new Generator(match);
            generator.run();
            RenderJS renderjs = new RenderJS(canvas, match);
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
