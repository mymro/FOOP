package Game;

import javafx.scene.canvas.GraphicsContext;

public abstract class DrawableGameObject extends GameObject{

    DrawableGameObject(){
        super();
    }

    DrawableGameObject(int layer){
        super(layer);
    }

    abstract public void draw(GraphicsContext gc);
}
