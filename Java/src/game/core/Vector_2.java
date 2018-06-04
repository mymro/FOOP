package game.core;

import java.io.Serializable;

public class Vector_2 implements Serializable {
    private int dim_x;
    private int dim_y;

    public Vector_2(){

    }
    public Vector_2(int dim_x, int dim_y) {
        this.dim_x = dim_x;
        this.dim_y = dim_y;
    }

    public int getDim_x() {
        return dim_x;
    }

    public int getDim_y() {
        return dim_y;
    }
}
