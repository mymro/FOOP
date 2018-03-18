package Game.Core;

import Game.Core.FModifier;
import Game.Core.GameObject;

public abstract class Flag extends GameObject implements FModifier {

    protected int pos_x;
    protected int pos_y;
    protected int dim_x;
    protected int dim_y;

    public Flag(){
        this(0, 0, 0, 0, 0);
    }

    public Flag(int layer, int x, int y, int dim_x, int dim_y )throws IllegalArgumentException{
        super(layer);
        if(x<0 || x > dim_x || y < 0 || y > dim_y){
            throw new IllegalArgumentException("coordinates and dimensions do not match");
        }
        pos_x = x;
        pos_y = y;
        this.dim_x = dim_x;
        this.dim_y = dim_y;
    }
}
