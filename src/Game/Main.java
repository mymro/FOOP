package Game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
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
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 500, 500));
        Controller controller = loader.getController();
        GraphicsContext gc = controller.labyrinth.getGraphicsContext2D();
        Labyrinth labyrinth = new Labyrinth(20,20);
        labyrinth.createLabyrinth();
        drawLabyrinth(gc, labyrinth);
        Random random = new Random();
        int x1 = random.nextInt(labyrinth.getDim_x());
        int x2 = random.nextInt(labyrinth.getDim_x());
        int y1 = random.nextInt(labyrinth.getDim_y());
        int y2 = random.nextInt(labyrinth.getDim_y());
        drawPath(labyrinth.findPath(labyrinth.getNodeAt(x1,y1), labyrinth.getNodeAt(x2, y2)), gc, labyrinth);
        primaryStage.show();
    }
    private void drawLabyrinth(GraphicsContext gc, Labyrinth labyrinth){
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(4);
        gc.strokeLine(0,0, gc.getCanvas().getWidth(),0);
        gc.strokeLine(0,0, 0,gc.getCanvas().getHeight());
        gc.strokeLine(gc.getCanvas().getWidth(),gc.getCanvas().getHeight(), gc.getCanvas().getWidth(),0);
        gc.strokeLine(gc.getCanvas().getWidth(),gc.getCanvas().getHeight(), 0,gc.getCanvas().getHeight());
        int x = labyrinth.getDim_x();
        int y = labyrinth.getDim_y();
        double width = gc.getCanvas().getWidth()/x;
        double height = gc.getCanvas().getHeight()/y;
        for (int i = 0; i < x; i++){
            for (int j = 0; j<y; j++){
                Labyrinth.Node current = labyrinth.getNodeAt(i,j);
                double centerX = current.getX()*width+width/2;
                double centerY = current.getY()*height+height/2;

                if(current.getNeighbourAt(Labyrinth.Direction.up) != null && current.getNeighbourAt(Labyrinth.Direction.up).getNeighbourAt(Labyrinth.Direction.down) == current){
                    gc.strokeLine(centerX, centerY, centerX, centerY-height/2);
                }
                if(current.getNeighbourAt(Labyrinth.Direction.down) != null && current.getNeighbourAt(Labyrinth.Direction.down).getNeighbourAt(Labyrinth.Direction.up) == current){
                    gc.strokeLine(centerX, centerY, centerX, centerY+height/2);
                }
                if(current.getNeighbourAt(Labyrinth.Direction.left) != null && current.getNeighbourAt(Labyrinth.Direction.left).getNeighbourAt(Labyrinth.Direction.right) == current){
                    gc.strokeLine(centerX, centerY, centerX-width+width/2, centerY);
                }
                if(current.getNeighbourAt(Labyrinth.Direction.right) != null && current.getNeighbourAt(Labyrinth.Direction.right).getNeighbourAt(Labyrinth.Direction.left) == current){
                    gc.strokeLine(centerX, centerY, centerX+width/2, centerY);
                }

                gc.strokeOval(centerX, centerY, 3,3);
            }
        }
    }

    public void drawPath(ArrayList<Labyrinth.Node> path, GraphicsContext gc, Labyrinth labyrinth){
        double width = gc.getCanvas().getWidth()/labyrinth.getDim_x();
        double height = gc.getCanvas().getHeight()/labyrinth.getDim_y();
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        for(int i = 0; i < path.size() -1; i++){
            Labyrinth.Node from = path.get(i);
            Labyrinth.Node to = path.get(i+1);
            double centerXfrom = from.getX()*width+width/2;
            double centerYfrom = from.getY()*height+height/2;
            double centerXto = to.getX()*width+width/2;
            double centerYto = to.getY()*height+height/2;
            gc.strokeLine(centerXfrom, centerYfrom, centerXto, centerYto);
        }
    }
}
