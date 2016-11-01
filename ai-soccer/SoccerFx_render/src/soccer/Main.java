package soccer;

import render.Render;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    
    final public static int WINDOW_WIDTH  = 700;
    final public static int WINDOW_HEIGHT = 400;
    
    @Override
    public void start(Stage stage) {
        
        Pane root = new Pane();
        Scene scene = new Scene(root);
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getChildren().add(canvas);
		stage.setScene(scene);
		stage.setTitle("Soccer");
		stage.show();
        
        //seed random number generator
        common.misc.Utils.setSeed(0);       
        SoccerPitch soccerPitch = new SoccerPitch(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // run generation
        // TODO: вывести на экран прогресс процесса генерации
        Generator generator = new Generator(soccerPitch);
        generator.run();
              
        //start animation
        Render render = new Render(canvas, soccerPitch);
        Timer timer = new Timer(render);
        timer.start();     
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
