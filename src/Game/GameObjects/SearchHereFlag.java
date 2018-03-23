package Game.GameObjects;

import Game.Core.Flag;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SearchHereFlag extends Flag {

    public SearchHereFlag(int layer, int x, int y, int dim_x, int dim_y, Robot player )throws IllegalArgumentException{
        super(layer, x, y, dim_x, dim_y, player);
    }

    @Override
    public void draw(GraphicsContext gc){
        double step_width = gc.getCanvas().getWidth()/dim_x;
        double step_height = gc.getCanvas().getHeight()/dim_y;
        for(int i = 0; i < 20; i++){
            gc.setStroke(Color.web("#4cf140", -0.9*getFModifierAt(getPos_x()-i,getPos_y())/20));
            gc.setLineWidth(step_width);
            gc.strokeOval((getPos_x()-i)*step_width, (getPos_y()-i)*step_height, ((i)*2+1)*step_width, ((i)*2+1)*step_height);
        }
    }

    @Override
    public double getFModifierAt(double x, double y) {
        return -20*Math.exp(-((Math.pow(x-getPos_x(),2)/(100) + Math.pow(y-getPos_y(), 2)/(100))));
    }
}
