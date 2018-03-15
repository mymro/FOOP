package Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Robot extends GameObject{

    public static final double seconds_per_move = 0.1;

    private Labyrinth labyrinth;
    private Labyrinth.LabyrinthNode current_pos;
    private Color color;
    private ArrayList<Labyrinth.LabyrinthNode> current_path;
    private int current_path_index;
    private double current_delta_time;

    Robot(int layer, Color color){
        super(layer);
        this.color = color;
    }

    public void initialize(Labyrinth labyrinth, int x, int y) throws IllegalArgumentException{
        this.labyrinth = labyrinth;
        this.current_pos = labyrinth.getNodeAt(x,y);
        if(current_pos == null){
            throw new IllegalArgumentException("no node at given position");
        }
        current_delta_time = 0;
        current_path = null;
        current_path_index = 0;
    }

    @Override
    public void draw(GraphicsContext gc) {
        if(current_pos != null && labyrinth != null){
            drawLabyrinth(gc);
            if(current_path != null){
                drawPath(current_path, gc, labyrinth);
            }
            gc.setFill(color);
            int x = labyrinth.getDim_x();
            int y = labyrinth.getDim_y();
            double width = gc.getCanvas().getWidth()/x;
            double height = gc.getCanvas().getHeight()/y;
            double[] points_x = {current_pos.getX()*width, (current_pos.getX()+1) * width, (current_pos.getX()+0.5) * width};
            double[] points_y = {(current_pos.getY()+1)*height, (current_pos.getY()+1)*height, current_pos.getY()*height};
            gc.fillPolygon(points_x, points_y, 3);
        }

        super.draw(gc);
    }

    @Override
    public void update() {
        if(current_pos != null && labyrinth != null){
            current_delta_time += game_system.deltaTime();
            if(current_delta_time >= seconds_per_move){
                current_delta_time = 0;
                if(current_path_index <= 0 && current_pos.getType() != Labyrinth.NodeType.finish){
                    current_path = labyrinth.findPath(current_pos, null);
                    current_path_index = current_path.size()-1;
                }
                if(current_path_index > 0 ){
                    current_path_index = current_path_index - 1;
                    current_pos = current_path.get(current_path_index);
                    if(current_pos.getType() == Labyrinth.NodeType.unknwon){
                        if(getParent() instanceof MainLabyrinth){
                            MainLabyrinth main = (MainLabyrinth) getParent();
                            labyrinth.changeNode(current_pos, main.getTypeAt(current_pos.getX(), current_pos.getY()), main.getEdgesAt(current_pos.getX(), current_pos.getY()));
                        }
                    }
                }
            }
        }
        super.update();
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

    public void drawLabyrinth(GraphicsContext gc){
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(5);
        int x = labyrinth.getDim_x();
        int y = labyrinth.getDim_y();
        double width = gc.getCanvas().getWidth()/x;
        double height = gc.getCanvas().getHeight()/y;
        for (int i = 0; i < x; i++){
            for (int j = 0; j < y; j++){
                Labyrinth.LabyrinthNode current = labyrinth.getNodeAt(i,j);
                if(current != null){
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
                }
            }
        }
    }
}
