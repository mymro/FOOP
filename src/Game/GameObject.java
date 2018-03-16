package Game;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeSet;

public abstract class GameObject{
    private GameObject parent;
    private LinkedList<GameObject> children;
    private int layer;
    private Main.GameSystem game_system;

    GameObject(){
        this(0);
    }

    GameObject(int layer){
        children = new LinkedList<>();
        this.layer = layer;
        game_system = Main.GameSystem.getInstance();
    }

    public void update(){
        for(GameObject go : children){
            go.update();
        }
    }

    public void draw(GraphicsContext gc){
        for(GameObject go : children){
            go.draw(gc);
        }
    }

    public void attach(GameObject go){
        if(go.parent != null){
            go.parent.detach(go);
        }
        go.parent = this;
        ListIterator<GameObject> it = children.listIterator();
        while (it.hasNext()){
            if (it.next().layer > go.layer){
                it.previous();
                it.add(go);
                return;
            }
        }
        children.add(children.size(), go);
    }

    public void detach(GameObject go){
        children.remove(go);
        go.parent = null;
    }

    public void setLayer(int layer){
        this.layer = layer;
    }

    public int getLayer(){
        return layer;
    }

    public GameObject getParent() {
        return parent;
    }

    public Main.GameSystem getGame_system(){
        return game_system;
    }
}
