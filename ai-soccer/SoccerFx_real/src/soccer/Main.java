package soccer;

import render.Render;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import render.FieldRender;

public class Main extends Application {
    
    @Override
    public void start(Stage stage) {
       
        Pane root = new Pane();
        Scene scene = new Scene(root);
        Canvas canvas = new Canvas(FieldRender.LENGTH_FIELD, FieldRender.WIDTH_FIELD);
        root.getChildren().add(canvas);
		stage.setScene(scene);
		stage.setTitle("Soccer");
		stage.show();
        
        //seed random number generator
        common.Game.RandomUtil.setSeed(0);       
        SoccerPitch soccerPitch = new SoccerPitch();
        
        // run generation
        // TODO: вывести на экран прогресс процесса генерации
        Generator generator = new Generator(soccerPitch);
        //generator.run();
              
        //start animation
        Render render = new Render(canvas, soccerPitch);
        Timer timer = new Timer(render);
        timer.start();     
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
