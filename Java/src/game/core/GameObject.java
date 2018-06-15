package game.core;

import game.Main;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * abstract Game-Object define the parent game-object, then I will be added children for this object
 *
 */
public abstract class GameObject implements Serializable {
    private GameObject parent;
    private LinkedList<GameObject> children;
    private int layer;
    private Main.GameSystem game_system;
    private double pos_x, pos_y;

    public GameObject() {
        this(0);
    }

    public GameObject(int layer) {
        this(layer, 0, 0);
    }

    public GameObject(int layer, double x, double y) {
        children = new LinkedList<>();
        this.layer = layer;
        game_system = Main.GameSystem.getInstance();
        pos_x = x;
        pos_y = y;
    }

    /**
     * update game object set the position X and Y
     */
    public void update() {
        for (GameObject go : children) {
            go.update();
        }
    }

    /**
     * draw the game objects to the graphicsContexts
     *
     * @param graphicsContext
     */
    public void draw(GraphicsContext graphicsContext) {
        for (GameObject go : children) {
            go.draw(graphicsContext);
        }
    }

    /**
     * attach game-object to parent object
     * game object should have parent object
     *
     * @param go
     */
    public final void attach(GameObject go) {
        if (go.parent != null) {
            go.parent.detach(go);
        }
        ListIterator<GameObject> it = children.listIterator();
        boolean child_added = false;
        while (it.hasNext()) {
            if (it.next().layer > go.layer) {
                it.previous();
                it.add(go);
                child_added = true;
                break;
            }
        }
        if (!child_added) {
            children.add(children.size(), go);
        }
        go.onAttach(this);
    }


    /**
     * define the parent for game-object
     *
     * @param parent
     */
    public void onAttach(GameObject parent) {
        this.parent = parent;
    }

    /**
     * remove game object from children
     *
     * @param go
     */
    public final void detach(GameObject go) {
        children.remove(go);
        go.onDetach();
    }

    /**
     * detach parent game-object
     */
    public void onDetach() {
        this.parent = null;
    }

    /**
     * get layet of game-object
     * @return
     */
    public int getLayer() {
        return layer;
    }

    /**
     * get parent of game-object
     * @return parent
     */
    public GameObject getParent() {
        return parent;
    }

    /**
     * get position x
     *
     * @return double position x
     */
    public double getPos_x() {
        return pos_x;
    }

    /**
     * set position x after update game-object
     *
     * @param x
     */
    public void setPos_x(double x) {
        pos_x = x;
    }

    /**
     * get position y after update game-object
     *
     * @return y
     */
    public double getPos_y() {
        return pos_y;
    }

    /**
     * set position x after update game-object
     *
     * @param y
     */
    public void setPos_y(double y) {
        pos_y = y;
    }


    /**
     * get game system singleton object
     * @return
     */
    public Main.GameSystem getGame_system() {
        return game_system;
    }

    /**
     * get children of parent game-object
     * @return
     */
    public LinkedList<GameObject> getChildren() {
        return children;
    }

}
