package Game.GameObjects;

public class Dimension {
    private int dim_x;
    private int dim_y;

    public Dimension(int dim_x, int dim_y) {
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
