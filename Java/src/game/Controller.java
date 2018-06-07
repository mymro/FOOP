package game;

import game.core.Flag;
import game.game.objects.DontComeNearFlag;
import game.game.objects.MainLabyrinth;
import game.game.objects.SearchHereFlag;
import game.net.Client;
import game.net.ClientGUI;
import game.net.Message;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

public class Controller {


    private Flag.flag_type choosenFlag = Flag.flag_type.none;

    @FXML
    public Canvas labyrinthCanvas;

    @FXML
    public MenuBar menuBar;

    @FXML
    public Circle playerColorCircle;

    public ClientGUI clientGUI;

    public MainLabyrinth mainLabyrinth =null;

    @FXML
    public void connectToServer() {
        //TODO
        System.out.println("TRY TO CONNECT TO SERVER.");
    }

    @FXML
    public void chooseAttractionFlag() {
        System.out.println("CHOOSE ATTRACTOR FLAG.");
        choosenFlag = Flag.flag_type.attract;

    }

    @FXML
    public void chooseRepelFlag() {
        System.out.println("CHOOSE REPEL FLAG.");
        choosenFlag = Flag.flag_type.repell;
    }

    @FXML
    public void handleAbout() {
        //TODO
        System.out.println("WANTS TO KNOW ABOUT SOMETHING");
    }

    @FXML
    public void handleClickEvent(final MouseEvent event) {
        // calculate labyrinth array coordinats
        clientGUI.createFlag(event.getX(), event.getY(), choosenFlag);
    }
}
