package game;

import game.game.objects.DontComeNearFlag;
import game.game.objects.MainLabyrinth;
import game.game.objects.SearchHereFlag;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;

public class Controller {

    private enum FlagType {
        ATTRACTOR_FLAG,
        REPEL_FLAG,
        NONE_FLAG
    }


    private static Main.GameSystem game_system = Main.GameSystem.getInstance();
    private FlagType choosenFlag = FlagType.NONE_FLAG;

    @FXML
    public Canvas labyrinthCanvas;

    @FXML
    public MenuBar menuBar;

    @FXML
    public void connectToServer() {
        //TODO
        System.out.println("TRY TO CONNECT TO SERVER.");
    }

    @FXML
    public void chooseAttractionFlag() {
        System.out.println("CHOOSE ATTRACTOR FLAG.");
        choosenFlag = FlagType.ATTRACTOR_FLAG;
    }

    @FXML
    public void chooseRepelFlag() {
        System.out.println("CHOOSE REPEL FLAG.");
        choosenFlag = FlagType.REPEL_FLAG;
    }

    @FXML
    public void handleAbout() {
        //TODO
        System.out.println("WANTS TO KNOW ABOUT SOMETHING");
    }

    @FXML
    public void handleClickEvent(final MouseEvent event) {
        // calculate labyrinth array coordinats
        int x = game_system.getLabyrinth().getLabyrinth().getDimension().getDim_x();
        int y = game_system.getLabyrinth().getLabyrinth().getDimension().getDim_y();
        double step_width = labyrinthCanvas.getWidth() / x;
        double step_height = labyrinthCanvas.getHeight() / y;
        int labyrinthCoorX = (int) Math.floor(event.getX() / step_width);
        int labyrinthCoorY = (int) Math.floor(event.getY() / step_height);
        System.out.println(labyrinthCoorX);
        System.out.println(labyrinthCoorY);

        switch (choosenFlag) {
            case ATTRACTOR_FLAG:
                game_system.getLabyrinth().addFlag(new SearchHereFlag(-30, labyrinthCoorX, labyrinthCoorY, 50, 50, null));
                game_system.getLabyrinth().update();
                break;
            case REPEL_FLAG:
                game_system.getLabyrinth().addFlag(new DontComeNearFlag(-30, labyrinthCoorX, labyrinthCoorY, 50, 50, null));
                game_system.getLabyrinth().update();
                break;
            default:
                System.out.println("NONE FLAG TYPE WAS CHOOSEN!!!");
                break;
        }

    }

}