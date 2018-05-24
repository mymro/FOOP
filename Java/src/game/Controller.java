package game;

import game.game.objects.DontComeNearFlag;
import game.game.objects.MainLabyrinth;
import game.game.objects.SearchHereFlag;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;

public class Controller {

    private enum FlagType{
        ATTRACTOR_FLAG,
        REPEL_FLAG,
        NONE_FLAG
    }

    private MainLabyrinth labyrinth;
    private FlagType choosenFlag = FlagType.NONE_FLAG;

    public void setLabyrinth(MainLabyrinth labyrinth) {
        this.labyrinth = labyrinth;
    }

    @FXML
    public Canvas labyrinthCanvas;

    @FXML
    public MenuBar menuBar;

    @FXML
    public void connectToServer()
    {
       //TODO
       System.out.println("TRY TO CONNECT TO SERVER.");
    }

    @FXML
    public void chooseAttractionFlag()
    {
        System.out.println("CHOOSE ATTRACTOR FLAG.");
        choosenFlag = FlagType.ATTRACTOR_FLAG;
    }

    @FXML
    public void chooseRepelFlag()
    {
        System.out.println("CHOOSE REPEL FLAG.");
        choosenFlag = FlagType.REPEL_FLAG;
    }

    @FXML
    public void handleAbout()
    {
        //TODO
        System.out.println("WANTS TO KNOW ABOUT SOMETHING");
    }

    @FXML
    public void handleClickEvent(final MouseEvent event)
    {
        // calculate labyrinth array coordinats
        int x = labyrinth.getLabyrinth().getDimension().getDim_x();
        int y = labyrinth.getLabyrinth().getDimension().getDim_y();
        double step_width = labyrinthCanvas.getWidth() / x;
        double step_height = labyrinthCanvas.getHeight() / y;
        int labyrinthCoorX = (int) Math.floor(event.getX()/step_width);
        int labyrinthCoorY = (int) Math.floor(event.getY()/step_height);
        System.out.println(labyrinthCoorX);
        System.out.println(labyrinthCoorY);

        switch (choosenFlag) {
            case ATTRACTOR_FLAG: labyrinth.addFlag(new SearchHereFlag(-30, labyrinthCoorX, labyrinthCoorY, 50, 50,null));
                break;
            case REPEL_FLAG: labyrinth.addFlag(new DontComeNearFlag(-30, labyrinthCoorX, labyrinthCoorY, 50, 50,null));
                break;
            default: System.out.println("NONE FLAG TYPE WAS CHOOSEN!!!");
                break;
        }

    }

}
