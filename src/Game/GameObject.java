package Game;

import javafx.scene.canvas.GraphicsContext;

import java.util.TreeSet;

public abstract class GameObject implements Comparable<GameObject>{
    private GameObject parent;
    private TreeSet<GameObject> children;
    private int layer;
    private Main.GameSystem game_system;

    GameObject(){
        this(0);
    }

    GameObject(int layer){
        children = new TreeSet<>();
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

    public Main.GameSystem getGame_system(){
        return game_system;
    }

    @Override
    public int compareTo(GameObject go){
        if(layer <= go.getLayer()){
            return -1;
        }else if(go == this){
            return 0;
        }else {
            return 1;
        }
    }
}
