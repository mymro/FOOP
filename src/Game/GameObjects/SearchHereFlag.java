package Game.GameObjects;

import Game.Core.Flag;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SearchHereFlag extends Flag {

    public SearchHereFlag(int layer, int x, int y, int dim_x, int dim_y )throws IllegalArgumentException{
        super(layer, x, y, dim_x, dim_y);
    }

    @Override
    public void draw(GraphicsContext gc){
        double step_width = gc.getCanvas().getWidth()/dim_x;
        double step_height = gc.getCanvas().getHeight()/dim_y;
        for(int i = 0; i < pos_x; i++){
            gc.setStroke(Color.web("#36e27e", -0.9*getFModifierAt(i,pos_y)/20));
            gc.setLineWidth(step_width);
            gc.strokeOval(i*step_width, i*step_height, (pos_x-i)*2*step_width, (pos_y-i)*2*step_height);
        }
    }

    @Override
    public double getFModifierAt(double x, double y) {
        return -20*Math.exp(-((Math.pow(x-pos_x,2)/(60) + Math.pow(y-pos_y, 2)/(60))));
    }
}
