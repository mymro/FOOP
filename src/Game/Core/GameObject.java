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
    private double pos_x, pos_y;

    public GameObject(){
        this(0);
    }

    public GameObject(int layer){
        this(layer, 0, 0);
    }

    public GameObject(int layer, double x,  double y){
        children = new LinkedList<>();
        this.layer = layer;
        game_system = Main.GameSystem.getInstance();
        pos_x = x;
        pos_y = y;
    }

    public void update(){
        for(GameObject go : children){
            go.update();
        }
    }

    public void draw(GraphicsContext graphicsContext){
        for(GameObject go : children){
            go.draw(graphicsContext);
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

    public double getPos_x(){
        return pos_x;
    }

    public void setPos_x(double x){
        pos_x = x;
    }

    public double getPos_y(){
        return pos_y;
    }

    public void setPos_y(double y){
        pos_y = y;
    }

    public Main.GameSystem getGame_system(){
        return game_system;
    }
}
