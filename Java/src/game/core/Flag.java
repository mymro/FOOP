package game.core;

import game.game.objects.Robot;

public abstract class Flag extends GameObject implements FModifier {

    protected int dim_x;
    protected int dim_y;
    private Robot player;

    public Flag(int layer, int x, int y, int dim_x, int dim_y, Robot player )throws IllegalArgumentException{
        super(layer, x, y);
        if(x<0 || x > dim_x || y < 0 || y > dim_y){
            throw new IllegalArgumentException("coordinates and dimensions do not match");
        }
        this.dim_x = dim_x;
        this.dim_y = dim_y;
        this.player = player;
    }

    public Robot getPlayer(){
        return player;
    }

    @Override
    public String toString() {
        return "Flag{" +
                "dim_x=" + dim_x +
                ", dim_y=" + dim_y +
                ", player=" + player +
                '}';
    }
}
