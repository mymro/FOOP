package Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

public class MainLabyrinth extends GameObject{

    private Labyrinth labyrinth;

    MainLabyrinth(int dim_x, int dim_y, int layer){
        this(dim_x, dim_y, layer, new Random());
    }

    MainLabyrinth(int dim_x, int dim_y, int layer, int seed){
        this(dim_x, dim_y, layer, new Random(seed));
    }

    MainLabyrinth(int dim_x, int dim_y, int layer, Random random){
        super(layer);
        labyrinth = new Labyrinth(dim_x, dim_y);
        labyrinth.createLabyrinth(random);
    }

    @Override
    public void draw(GraphicsContext gc) {
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
            for (int j = 0; j < y; j++){
                Labyrinth.LabyrinthNode current = labyrinth.getNodeAt(i,j);
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

        super.draw(gc);
    }

    @Override
    public void update() {
        super.update();
    }

    public int getDimX(){
        return labyrinth.getDim_x();
    }

    public int getDimY(){
        return labyrinth.getDim_y();
    }
}
