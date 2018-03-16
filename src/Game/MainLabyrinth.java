package Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;
import java.util.Set;

public class MainLabyrinth extends GameObject{

    private Labyrinth labyrinth;
    private long seed;

    MainLabyrinth(int dim_x, int dim_y, int layer){
        this(dim_x, dim_y, layer, new Random().nextInt());
    }

    MainLabyrinth(int dim_x, int dim_y, int layer, int seed){
        super(layer);
        Random random = new Random(seed);
        labyrinth = new Labyrinth(dim_x, dim_y);
        labyrinth.createLabyrinth(random);
        this.seed = seed;
    }

    public Robot addPlayer(Color color, int layer){
        Random random = new Random();
        int i,j;
        do{
            i = random.nextInt(labyrinth.getDim_x());
            j = random.nextInt(labyrinth.getDim_y());
        }while (labyrinth.getNodeAt(i,j).getType() == Labyrinth.NodeType.finish);
        Robot robot = new Robot(layer, color);
        Labyrinth lab = new Labyrinth(labyrinth.getDim_x(), labyrinth.getDim_y());
        lab.setNodeAt(i,j, Labyrinth.NodeType.normal, labyrinth.getNodeAt(i,j).getEdges());
        robot.initialize(lab , i, j);
        attach(robot);

        return robot;
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

                if(labyrinth.getNodeAt(i,j).getType() == Labyrinth.NodeType.finish){
                    gc.setStroke(Color.ORANGE);
                    gc.fillOval(centerX, centerY, 10,10);
                    gc.setStroke(Color.BLACK);
                }
            }
        }

        super.draw(gc);
    }

    @Override
    public void update() {
        super.update();
    }

    public Labyrinth.NodeType getTypeAt(int x, int y){
        return labyrinth.getNodeAt(x,y).getType();
    }

    public Set<Labyrinth.Direction> getEdgesAt(int x, int y){
        return  labyrinth.getNodeAt(x,y).getEdges();
    }

    public int getDimX(){
        return labyrinth.getDim_x();
    }

    public int getDimY(){
        return labyrinth.getDim_y();
    }
}
