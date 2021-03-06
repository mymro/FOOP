package game.core;

import java.io.Serializable;
import java.util.Random;

public class MainDimension extends Vector_2 implements Serializable {
    private int layer;
    private long seed;

    public MainDimension(){
        super();
    }
    public MainDimension(int dim_x, int  dim_y, int layer, long seed) {
        super(dim_x,dim_y);
        this.layer = layer;
        this.seed = seed;
    }

    public int getLayer() {
        return layer;
    }

    public long getSeed() {
        if(seed == 0) {
            return new Random().nextInt();
        }
        return seed;
    }
}
