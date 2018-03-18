package Game;

import Game.GameObjects.DontComeNearFlag;
import Game.GameObjects.MainLabyrinth;
import Game.GameObjects.SearchHereFlag;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.concurrent.TimeUnit;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static final class GameSystem {
        private static GameSystem instance;
        private double delta_time; // in nanoseconds

        private GameSystem(){
            delta_time = 0;
        }

        public static GameSystem getInstance(){
            if (instance == null){
                instance = new GameSystem();
            }
            return instance;
        }

        public double deltaTime(){
            return delta_time;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("FOOP");
        primaryStage.setScene(new Scene(root, 500, 500));
        Controller controller = loader.getController();
        GraphicsContext gc = controller.labyrinth.getGraphicsContext2D();
        primaryStage.show();

        MainLabyrinth labyrinth = new MainLabyrinth(50,50, 0);
        labyrinth.addPlayer(Color.RED, -1);
        labyrinth.addPlayer(Color.BLUE, -1);
        labyrinth.addPlayer(Color.YELLOW, -1);
        SearchHereFlag flag = new SearchHereFlag(-30, 10, 10, 50, 50);
        DontComeNearFlag flag2 = new DontComeNearFlag(-30, 40, 40, 50, 50);
        labyrinth.addFlag(flag);
        labyrinth.addFlag(flag2);

        GameSystem game_system = GameSystem.getInstance();
        TimeUnit.SECONDS.sleep(1);

        new AnimationTimer(){

            long last_frame_time = System.nanoTime();

            @Override
            public void handle(long now) {
                gc.clearRect(0,0,gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
                labyrinth.update();
                labyrinth.draw(gc);
                game_system.delta_time = (now- last_frame_time)/1000000000.0;
                last_frame_time = now;
            }
        }.start();
    }
}
