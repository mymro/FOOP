package Game;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

public abstract class GameObject implements Comparable<GameObject>{
    private GameObject parent;
    private TreeSet<GameObject> children;
    private int layer;

    GameObject(){
        this(0);
    }

    GameObject(int layer){
        children = new TreeSet<>();
        this.layer = layer;
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
        children.add(go);
        go.parent = this;
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

    @Override
    public int compareTo(GameObject go){
        if(layer < go.getLayer()){
            return -1;
        }else if(layer == go.getLayer()){
            return 0;
        }else {
            return 1;
        }
    }
}
