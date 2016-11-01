package Tests;

import Steering.GameWorld;
import Steering.Timer;
import common.misc.Cgdi;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CohesionCellMain extends Application {
    
    final public static int WINDOW_WIDTH  = 600;
    final public static int WINDOW_HEIGHT = 500;
    
    @Override
    public void start(Stage stage) {
                            
        Pane root = new Pane();
        Scene scene = new Scene(root);
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getChildren().add(canvas);
		stage.setScene(scene);
		stage.setTitle("Steering Behaviors Test : cohesion cell + alignment cell + wander");
		stage.show();
        
        //start animation
        common.misc.Utils.setSeed(0);
        Cgdi cgdi = new Cgdi(canvas.getGraphicsContext2D());
        
        ///// Test:  
        GameWorld gameWorld = new CohesionCellTest(WINDOW_WIDTH, WINDOW_HEIGHT).getGameWorld();
        /////
        
        Timer timer = new Timer(cgdi, gameWorld);
        timer.start();
        
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
