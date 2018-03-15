package Game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("FOOP");
        primaryStage.setScene(new Scene(root, 500, 500));
        Controller controller = loader.getController();
        GraphicsContext gc = controller.labyrinth.getGraphicsContext2D();
        MainLabyrinth labyrinth = new MainLabyrinth(20,20, 0, 10);
        Robot robot = new Robot(0, Color.GREEN);
        Robot robot2 = new Robot(3, Color.RED);
        Labyrinth lab = new Labyrinth(20,20);
        lab.setNodeAt(3,3, Labyrinth.NodeType.normal);
        robot.initialize(lab, 3,3);
        robot2.initialize(lab, 3,3);
        labyrinth.attach(robot);
        labyrinth.attach(robot2);
        
        labyrinth.draw(gc);
        primaryStage.show();
    }


    public void drawPath(ArrayList<Labyrinth.LabyrinthNode> path, GraphicsContext gc, Labyrinth labyrinth){
        double width = gc.getCanvas().getWidth()/labyrinth.getDim_x();
        double height = gc.getCanvas().getHeight()/labyrinth.getDim_y();
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        for(int i = 0; i < path.size() -1; i++){
            Labyrinth.LabyrinthNode from = path.get(i);
            Labyrinth.LabyrinthNode to = path.get(i+1);
            double centerXfrom = from.getX()*width+width/2;
            double centerYfrom = from.getY()*height+height/2;
            double centerXto = to.getX()*width+width/2;
            double centerYto = to.getY()*height+height/2;
            gc.strokeLine(centerXfrom, centerYfrom, centerXto, centerYto);
        }
    }
}
