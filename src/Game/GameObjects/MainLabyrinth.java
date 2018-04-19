package Game.GameObjects;

import Game.Core.FModifier;
import Game.Core.Flag;
import Game.Core.GameObject;
import Game.Core.Labyrinth;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class MainLabyrinth extends GameObject implements FModifier ,Serializable {

    private Labyrinth labyrinth;
    private long seed;
    private ArrayList<FModifier> modifiers;

    public Labyrinth getLabyrinth() {
        return labyrinth;
    }

    public MainLabyrinth(Dimension dimension, int layer) {
        this(new MainDimension(dimension.getDim_x(), dimension.getDim_y(), layer, new Random().nextInt()));

    }

    public MainLabyrinth(MainDimension mainDimension) {
        super(mainDimension.getLayer());
        Random random = new Random(mainDimension.getSeed());
        labyrinth = new Labyrinth(mainDimension);
        labyrinth.createLabyrinth(random);
        this.seed = mainDimension.getSeed();
        modifiers = new ArrayList<>();
    }

    public Robot addPlayer(Player player, int layer) {
        Random random = new Random();
        int i, j;
        do {
            i = random.nextInt(labyrinth.getDimension().getDim_x());
            j = random.nextInt(labyrinth.getDimension().getDim_y());
        } while (labyrinth.getNodeAt(i, j).getType() == Labyrinth.NodeType.finish);
        Robot robot = new Robot(layer, player);
        Labyrinth lab = new Labyrinth(labyrinth.getDimension());
        lab.setNodeAt(i, j, Labyrinth.NodeType.normal, labyrinth.getNodeAt(i, j).getEdges());
        robot.initialize(lab, i, j);
        attach(robot);

        return robot;
    }

    public Flag addFlag(Flag flag) {
        attach(flag);
        modifiers.add(flag);
        return flag;
    }

    @Override
    public void draw(GraphicsContext gc) {
        int x = labyrinth.getDimension().getDim_x();
        int y = labyrinth.getDimension().getDim_y();
        double step_width = gc.getCanvas().getWidth() / x;
        double step_height = gc.getCanvas().getHeight() / y;
        double lineWidth = step_width-2;
        double xFinish = 0d;
        double yFinish = 0d;

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(lineWidth);
        gc.strokeLine(0, 0, gc.getCanvas().getWidth(), 0);
        gc.strokeLine(0, 0, 0, gc.getCanvas().getHeight());
        gc.strokeLine(gc.getCanvas().getWidth(), gc.getCanvas().getHeight(), gc.getCanvas().getWidth(), 0);
        gc.strokeLine(gc.getCanvas().getWidth(), gc.getCanvas().getHeight(), 0, gc.getCanvas().getHeight());
        gc.setStroke(Color.WHITE);

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Labyrinth.LabyrinthNode current = labyrinth.getNodeAt(i, j);
                double centerX = current.getX() * step_width + step_width / 2;
                double centerY = current.getY() * step_height + step_height / 2;

                if (current.getNeighbourAt(Labyrinth.Direction.up) != null && current.getNeighbourAt(Labyrinth.Direction.up).getNeighbourAt(Labyrinth.Direction.down) == current) {
                    gc.strokeLine(centerX, centerY, centerX, centerY - step_height / 2);
                }
                if (current.getNeighbourAt(Labyrinth.Direction.down) != null && current.getNeighbourAt(Labyrinth.Direction.down).getNeighbourAt(Labyrinth.Direction.up) == current) {
                    gc.strokeLine(centerX, centerY, centerX, centerY + step_height / 2);
                }
                if (current.getNeighbourAt(Labyrinth.Direction.left) != null && current.getNeighbourAt(Labyrinth.Direction.left).getNeighbourAt(Labyrinth.Direction.right) == current) {
                    gc.strokeLine(centerX, centerY, centerX - step_width + step_width / 2, centerY);
                }
                if (current.getNeighbourAt(Labyrinth.Direction.right) != null && current.getNeighbourAt(Labyrinth.Direction.right).getNeighbourAt(Labyrinth.Direction.left) == current) {
                    gc.strokeLine(centerX, centerY, centerX + step_width / 2, centerY);
                }

                if (labyrinth.getNodeAt(i, j).getType() == Labyrinth.NodeType.finish) {
                    xFinish = centerX;
                    yFinish = centerY;
                }
            }
        }

        gc.setFill(Color.ORANGE);
        gc.fillOval(xFinish-lineWidth/2, yFinish-lineWidth/2, lineWidth, lineWidth);

        super.draw(gc);
    }

    @Override
    public void update() {
        super.update();
    }

    public Labyrinth.NodeType getTypeAt(int x, int y) {
        return labyrinth.getNodeAt(x, y).getType();
    }

    public Set<Labyrinth.Direction> getEdgesAt(int x, int y) {
        return labyrinth.getNodeAt(x, y).getEdges();
    }



    @Override
    public double getFModifierAt(double x, double y) {
        double result = 0;
        for (FModifier modifier : modifiers) {
            result += modifier.getFModifierAt(x, y);
        }
        return result;
    }

    @Override
    public String toString() {
        return "MainLabyrinth{" +
                "labyrinth=" + labyrinth +
                ", seed=" + seed +
                ", modifiers=" + modifiers +
                '}';
    }
}
