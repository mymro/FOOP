package Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public class Robot extends GameObject{

    private Labyrinth labyrinth;
    private Labyrinth.LabyrinthNode current_pos;
    public Paint color;

    Robot(int layer, Paint color){
        super(layer);
        this.color = color;
    }

    public void initialize(Labyrinth labyrinth, int x, int y) throws IllegalArgumentException{
        this.labyrinth = labyrinth;
        this.current_pos = labyrinth.getNodeAt(x,y);
        if(current_pos == null){
            throw new IllegalArgumentException("no node at given position");
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        if(current_pos != null && labyrinth != null){
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

        super.update();
    }
}
