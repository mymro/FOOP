package game.core;

public interface FModifier  {

    /**
     * it is influence to find the path.
     * it will be change the path with the position x and y
     * and will used a functional recursive call
     * @param x
     * @param y
     * @return double value
     */
    public double getFModifierAt(double x, double y);
}
