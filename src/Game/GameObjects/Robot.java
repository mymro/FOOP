package Game.GameObjects;

import Game.Core.GameObject;
import Game.Core.Labyrinth;
import Game.GameObjects.MainLabyrinth;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Robot extends GameObject {

    public static final double seconds_per_field = 0.02;

    private Labyrinth labyrinth;
    private Labyrinth.LabyrinthNode current_node, next_node;
    private Color color;
    private ArrayList<Labyrinth.LabyrinthNode> current_path;
    private int current_path_index;
    private double current_delta_time;
    private MainLabyrinth mainLabyrinth;
    private String player;

    public Robot(int layer, Player player) {
        super(layer);
        if (player.getColor().equalsIgnoreCase("RED")) {
            this.color = Color.RED;
        }
        if (player.getColor().equalsIgnoreCase("GREEN")) {
            this.color = Color.GREEN;
        }
        if (player.getColor().equalsIgnoreCase("YELLOW")) {
            this.color = Color.YELLOW;
        }
        this.player = player.getName();
        mainLabyrinth = null;
        initialize(null, 0, 0);
    }

    public void initialize(Labyrinth labyrinth, int x, int y) throws ArrayIndexOutOfBoundsException {
        this.labyrinth = labyrinth;

        if (labyrinth != null) {
            try {
                current_node = labyrinth.getNodeAt(x, y);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw e;
            }
            setPos_x(current_node.getX());
            setPos_y(current_node.getY());
        } else {
            current_node = null;
            setPos_x(-1);
            setPos_y(-1);
        }
        next_node = null;
        current_path = null;
        current_path_index = 0;
        current_delta_time = 0;
    }

    @Override
    public void onAttach(GameObject parent) {
        super.onAttach(parent);
        mainLabyrinth = (MainLabyrinth) parent;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainLabyrinth = null;
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (getPos_x() >= 0 && getPos_y() >= 0 && labyrinth != null) {
            //drawLabyrinth(gc);
            //if(current_path != null){
            // drawPath(current_path, gc, labyrinth);
            //a}
            gc.setFill(color);
            int x = labyrinth.getDimension().getDim_x();
            int y = labyrinth.getDimension().getDim_y();
            double step_width = gc.getCanvas().getWidth() / x;
            double step_height = gc.getCanvas().getHeight() / y;
            double[] points_x = {getPos_x() * step_width, (getPos_x() + 1) * step_width, (getPos_x() + 0.5) * step_width};
            double[] points_y = {(getPos_y() + 1) * step_height, (getPos_y() + 1) * step_height, getPos_y() * step_height};
            gc.fillPolygon(points_x, points_y, 3);
        }

        super.draw(gc);
    }

    @Override
    public void update() {
        if (current_node != null && labyrinth != null) {
            current_delta_time += getGame_system().deltaTime();

            if (current_path_index <= 0 && current_node.getType() != Labyrinth.NodeType.finish) {
                current_path = labyrinth.findPath(current_node, null, mainLabyrinth);
                current_path_index = current_path.size() - 1;
                if (current_path_index > 0) {
                    next_node = current_path.get(current_path_index - 1);
                } else {
                    next_node = null;
                }
            }

            if (current_delta_time >= seconds_per_field && current_path_index > 0) {
                current_delta_time = 0;
                current_path_index = current_path_index - 1;
                current_node = current_path.get(current_path_index);

                if (current_path_index > 0) {
                    next_node = current_path.get(current_path_index - 1);
                } else {
                    next_node = null;
                }
                setPos_x(current_node.getX());
                setPos_y(current_node.getY());

                if (current_node.getType() == Labyrinth.NodeType.unknown) {
                    if (mainLabyrinth != null) {
                        labyrinth.changeNode(current_node, mainLabyrinth.getTypeAt(current_node.getX(), current_node.getY()), mainLabyrinth.getEdgesAt(current_node.getX(), current_node.getY()));
                    }
                }

            } else if (next_node != null) {
                setPos_x(current_node.getX() + (next_node.getX() - current_node.getX()) * current_delta_time / seconds_per_field);
                setPos_y(current_node.getY() + (next_node.getY() - current_node.getY()) * current_delta_time / seconds_per_field);
            }
        }
        super.update();
    }

    public void drawPath(ArrayList<Labyrinth.LabyrinthNode> path, GraphicsContext gc, Labyrinth labyrinth) {
        double width = gc.getCanvas().getWidth() / labyrinth.getDimension().getDim_x();
        double height = gc.getCanvas().getHeight() / labyrinth.getDimension().getDim_y();
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        for (int i = 0; i < path.size() - 1; i++) {
            Labyrinth.LabyrinthNode from = path.get(i);
            Labyrinth.LabyrinthNode to = path.get(i + 1);
            double centerXfrom = from.getX() * width + width / 2;
            double centerYfrom = from.getY() * height + height / 2;
            double centerXto = to.getX() * width + width / 2;
            double centerYto = to.getY() * height + height / 2;
            gc.strokeLine(centerXfrom, centerYfrom, centerXto, centerYto);
        }
    }

    public void drawLabyrinth(GraphicsContext gc) {
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(5);
        int x = labyrinth.getDimension().getDim_x();
        int y = labyrinth.getDimension().getDim_y();
        double width = gc.getCanvas().getWidth() / x;
        double height = gc.getCanvas().getHeight() / y;
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Labyrinth.LabyrinthNode current = labyrinth.getNodeAt(i, j);
                if (current != null) {
                    double centerX = current.getX() * width + width / 2;
                    double centerY = current.getY() * height + height / 2;

                    if (current.getNeighbourAt(Labyrinth.Direction.up) != null && current.getNeighbourAt(Labyrinth.Direction.up).getNeighbourAt(Labyrinth.Direction.down) == current) {
                        gc.strokeLine(centerX, centerY, centerX, centerY - height / 2);
                    }
                    if (current.getNeighbourAt(Labyrinth.Direction.down) != null && current.getNeighbourAt(Labyrinth.Direction.down).getNeighbourAt(Labyrinth.Direction.up) == current) {
                        gc.strokeLine(centerX, centerY, centerX, centerY + height / 2);
                    }
                    if (current.getNeighbourAt(Labyrinth.Direction.left) != null && current.getNeighbourAt(Labyrinth.Direction.left).getNeighbourAt(Labyrinth.Direction.right) == current) {
                        gc.strokeLine(centerX, centerY, centerX - width + width / 2, centerY);
                    }
                    if (current.getNeighbourAt(Labyrinth.Direction.right) != null && current.getNeighbourAt(Labyrinth.Direction.right).getNeighbourAt(Labyrinth.Direction.left) == current) {
                        gc.strokeLine(centerX, centerY, centerX + width / 2, centerY);
                    }
                }
            }
        }
    }

}
