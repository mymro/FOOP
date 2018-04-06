package Game;

import Game.GameObjects.*;
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
    public Player player;

    public Main(Player player) {
       this.player = player;
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
        Controller controller = loader.getController();
        primaryStage.setTitle("FOOP");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        GraphicsContext gc = controller.labyrinthCanvas.getGraphicsContext2D();
        primaryStage.show();

        MainLabyrinth labyrinth = new MainLabyrinth(new Dimension(50,50), 0);
        controller.setLabyrinth(labyrinth);
        if(this.player ==null) {
            this.player = new Player("p1", "RED", "localhost", 2222);
        }

        //Player player2 = new Player("p2",Color.YELLOW, "localhost",3333);
        //Player player3 = new Player("p3",Color.BLUE, "localhost",4444);
        labyrinth.addPlayer(this.player, -1);
       // labyrinth.addPlayer(player2, -1);
        //labyrinth.addPlayer(player3, -1);
        SearchHereFlag flag = new SearchHereFlag(-30, 10, 10, 50, 50,null);
        DontComeNearFlag flag2 = new DontComeNearFlag(-30, 40, 40, 50, 50,null);
        labyrinth.addFlag(flag);
        labyrinth.addFlag(flag2);

        GameSystem game_system = GameSystem.getInstance();
        new AnimationTimer(){

            long last_frame_time = System.nanoTime();

            @Override
            public void handle(long now) {
                gc.clearRect(0,0,gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
                gc.setFill(Color.BLACK);
                gc.fillRect(0,0,gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
                labyrinth.update();
                labyrinth.draw(gc);
                game_system.delta_time = (now- last_frame_time)/1000000000.0;
                last_frame_time = now;
            }
        }.start();
    }
}
