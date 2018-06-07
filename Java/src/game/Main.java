package game;

import game.game.objects.MainLabyrinth;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.ObjectStreamException;
import java.io.Serializable;

public class Main extends Application {



    public static void main(String[] args) {
        launch(args);
    }





    public static final class GameSystem implements Serializable {
        private static GameSystem instance;
        private double delta_time; // in nanoseconds
        public MainLabyrinth labyrinth;
        public boolean isRunning= true;
        private String message = null;
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

        public void setDelta_time(double delta_time) {
            this.delta_time = delta_time;
        }

        public MainLabyrinth getLabyrinth() {
            return labyrinth;
        }

        public void setLabyrinth(MainLabyrinth labyrinth) {
            this.labyrinth = labyrinth;
        }

        public boolean isRunning() {
            return isRunning;
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception , ObjectStreamException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        primaryStage.setTitle("FOOP");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        GraphicsContext gc = controller.labyrinthCanvas.getGraphicsContext2D();
        primaryStage.show();



        GameSystem game_system = GameSystem.getInstance();
        //controller.setLabyrinth(game_system.labyrinth);
        new AnimationTimer(){

            long last_frame_time = System.nanoTime();

            @Override
            public void handle(long now)  {
                gc.clearRect(0,0,gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
                gc.setFill(Color.BLACK);
                gc.fillRect(0,0,gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
                //game_system.labyrinth.update();
                //game_system.labyrinth.draw(gc);
                game_system.delta_time = (now- last_frame_time)/1000000000.0;
                last_frame_time = now;
            }
        }.start();
    }
}
