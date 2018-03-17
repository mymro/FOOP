package Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Robot extends GameObject{

    public static final double seconds_per_field = 0.2;

    private Labyrinth labyrinth;
    private double current_pos_x, current_pos_y;
    private Labyrinth.LabyrinthNode current_node, next_node;
    private Color color;
    private ArrayList<Labyrinth.LabyrinthNode> current_path;
    private int current_path_index;
    private double current_delta_time;

    Robot(int layer, Color color){
        super(layer);
        this.color = color;
    }

    public void initialize(Labyrinth labyrinth, int x, int y) throws ArrayIndexOutOfBoundsException{
        this.labyrinth = labyrinth;

        if(labyrinth != null){
            try{
                current_node = labyrinth.getNodeAt(x,y);
            }catch (ArrayIndexOutOfBoundsException e){
                throw e;
            }
            current_pos_x = current_node.getX();
            current_pos_y = current_node.getY();
        }else {
            current_node = null;
            current_pos_x = -1;
            current_pos_y = -1;
        }
        next_node = null;
        current_path = null;
        current_path_index = 0;
        current_delta_time = 0;
    }

    @Override
    public void draw(GraphicsContext gc) {
        if(current_pos_x > -1 && current_pos_y > -1 && labyrinth != null){
            drawLabyrinth(gc);
            if(current_path != null){
                drawPath(current_path, gc, labyrinth);
            }
            gc.setFill(color);
            int x = labyrinth.getDim_x();
            int y = labyrinth.getDim_y();
            double width = gc.getCanvas().getWidth()/x;
            double height = gc.getCanvas().getHeight()/y;
            double[] points_x = {current_pos_x*width, (current_pos_x+1) * width, (current_pos_x+0.5) * width};
            double[] points_y = {(current_pos_y+1)*height, (current_pos_y+1)*height, current_pos_y*height};
            gc.fillPolygon(points_x, points_y, 3);
        }

        super.draw(gc);
    }

    @Override
    public void update() {
        if(current_node != null && labyrinth != null){
            current_delta_time += getGame_system().deltaTime();

            if(current_path_index <= 0 && current_node.getType() != Labyrinth.NodeType.finish){
                current_path = labyrinth.findPath(current_node, null);
                current_path_index = current_path.size()-1;
                if(current_path_index > 0){
                    next_node = current_path.get(current_path_index-1);
                }else {
                    next_node = null;
                }
            }

            if(current_delta_time >= seconds_per_field && current_path_index > 0) {
                current_delta_time = 0;
                current_path_index = current_path_index - 1;
                current_node = current_path.get(current_path_index);

                if(current_path_index > 0){
                    next_node = current_path.get(current_path_index-1);
                }else {
                    next_node = null;
                }

                current_pos_x = current_node.getX();
                current_pos_y = current_node.getY();

                if(current_node.getType() == Labyrinth.NodeType.unknown){
                    if(getParent() instanceof MainLabyrinth){
                        MainLabyrinth main = (MainLabyrinth) getParent();
                        labyrinth.changeNode(current_node, main.getTypeAt(current_node.getX(), current_node.getY()), main.getEdgesAt(current_node.getX(), current_node.getY()));
                    }
                }

            }else if(next_node != null){
                current_pos_x = current_node.getX() + (next_node.getX() - current_node.getX()) * current_delta_time/seconds_per_field;
                current_pos_y = current_node.getY() + (next_node.getY() - current_node.getY()) * current_delta_time/seconds_per_field;
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
