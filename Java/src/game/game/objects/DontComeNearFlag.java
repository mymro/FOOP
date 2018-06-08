package game.game.objects;

import game.core.Flag;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;

public class DontComeNearFlag extends Flag implements Serializable{

    public DontComeNearFlag(int layer, int x, int y, int dim_x, int dim_y)throws IllegalArgumentException{
        super(layer, x, y, dim_x, dim_y);
    }

    @Override
    public void draw(GraphicsContext gc) {
        double step_width = gc.getCanvas().getWidth()/dim_x;
        double step_height = gc.getCanvas().getHeight()/dim_y;
        gc.setFill(Color.web("#f24141"));
        gc.fillOval((getPos_x())*step_width, (getPos_y())*step_height, step_width, step_height);
    }

    @Override
    public double getFModifierAt(double x, double y) {
        return 20*Math.exp(-((Math.pow(x-getPos_x(),2)/(50) + Math.pow(y-getPos_y(), 2)/(50))));
    }

    @Override
    public String toString() {
        super.toString();
        return "DontComeNearFlag{}";
    }
}
