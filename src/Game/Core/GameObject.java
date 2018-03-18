package Game.Core;

import Game.Main;
import javafx.scene.canvas.GraphicsContext;

import java.util.LinkedList;
import java.util.ListIterator;

public abstract class GameObject{
    private GameObject parent;
    private LinkedList<GameObject> children;
    private int layer;
    private Main.GameSystem game_system;

    public GameObject(){
        this(0);
    }

    public GameObject(int layer){
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

    public final void attach(GameObject go){
        if(go.parent != null){
            go.parent.detach(go);
        }
        ListIterator<GameObject> it = children.listIterator();
        boolean child_added = false;
        while (it.hasNext()){
            if (it.next().layer > go.layer){
                it.previous();
                it.add(go);
                child_added = true;
                break;
            }
        }
        if(!child_added){
            children.add(children.size(), go);
        }
        go.onAttach(this);
    }

    public void onAttach(GameObject parent){
        this.parent = parent;
    }

    public final void detach(GameObject go){
        children.remove(go);
        go.onDetach();
    }

    public void onDetach(){
        this.parent = null;
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
