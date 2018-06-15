package game.core;

public abstract class Flag extends GameObject implements FModifier {

    public enum flag_type {
        repell,
        attract,
        none
    }

    protected int dim_x;
    protected int dim_y;

    /**
     * create new Flag with layer, position and dimension
     * The flag help the robots to find the path
     * with the position x und y will be set to coordinate
     * the dimensions are for the area of flag
     * @param layer
     * @param x
     * @param y
     * @param dim_x
     * @param dim_y
     * @throws IllegalArgumentException
     */
    public Flag(int layer, int x, int y, int dim_x, int dim_y)throws IllegalArgumentException{
        super(layer, x, y);
        if(x<0 || x > dim_x || y < 0 || y > dim_y){
            throw new IllegalArgumentException("coordinates and dimensions do not match");
        }
        this.dim_x = dim_x;
        this.dim_y = dim_y;
    }

    @Override
    public String toString() {
        return "Flag{" +
                "dim_x=" + dim_x +
                ", dim_y=" + dim_y +
                '}';
    }
}
