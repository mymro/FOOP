package Game.Core;

import Game.Core.Dimension;

import java.io.Serializable;
import java.util.Random;

public class MainDimension extends Dimension implements Serializable {
    private int layer;
    private int seed;

    public MainDimension(){
        super();
    }
    public MainDimension(int dim_x, int  dim_y, int layer, int seed) {
        super(dim_x,dim_y);
        this.layer = layer;
        this.seed = seed;
    }

    public int getLayer() {
        return layer;
    }

    public int getSeed() {
        if(seed == 0) {
            return new Random().nextInt();
        }
        return seed;
    }
}
