package Steering;

import common.misc.Cgdi;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    
    final public static int WINDOW_WIDTH  = 600;
    final public static int WINDOW_HEIGHT = 500;
    
    @Override
    public void start(Stage stage) {
                            
        Pane root = new Pane();
        Scene scene = new Scene(root);
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getChildren().add(canvas);
		stage.setScene(scene);
		stage.setTitle("Steering Behaviors - Another Big Shoal");
		stage.show();
        
        //start animation
        common.misc.Utils.setSeed(0);
        Cgdi cgdi = new Cgdi(canvas.getGraphicsContext2D());
        GameWorld gameWorld = new GameWorld(WINDOW_WIDTH, WINDOW_HEIGHT);
        Timer timer = new Timer(cgdi, gameWorld);
        timer.start();
        
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
