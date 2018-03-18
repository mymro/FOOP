package Game.GameObjects;

import Game.Core.Flag;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DontComeNearFlag extends Flag{

    public DontComeNearFlag(int layer, int x, int y, int dim_x, int dim_y )throws IllegalArgumentException{
        super(layer, x, y, dim_x, dim_y);
    }

    @Override
    public void draw(GraphicsContext gc){
        double step_width = gc.getCanvas().getWidth()/dim_x;
        double step_height = gc.getCanvas().getHeight()/dim_y;
        for(int i = 0; i < 20; i++){
            gc.setStroke(Color.web("#f24141", 0.9*getFModifierAt(pos_x-i,pos_y)/20));
            gc.setLineWidth(step_width);
            gc.strokeOval((pos_x-i)*step_width, (pos_y-i)*step_height, ((i)*2+1)*step_width, ((i)*2+1)*step_height);
        }
    }

    @Override
    public double getFModifierAt(double x, double y) {
        return 20*Math.exp(-((Math.pow(x-pos_x,2)/(200) + Math.pow(y-pos_y, 2)/(200))));
    }
}
