package game.net;

import game.Controller;
import game.core.Flag;
import game.core.GameObject;
import game.core.MainDimension;
import game.game.objects.DontComeNearFlag;
import game.game.objects.MainLabyrinth;
import game.game.objects.Player;
import game.Main;
import game.game.objects.SearchHereFlag;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public class ClientGUI extends Application {

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public MainLabyrinth getLabyrinth() {
        return labyrinth;
    }

    private Client client;
    private String userName;
    private String hostName;
    private Integer port;
    private MainLabyrinth labyrinth;
    private static Main.GameSystem game_system = Main.GameSystem.getInstance();
    private static Controller controller = null;
    private Hashtable<Integer, GameObject> moving_objects;
    public static Text actiontarget = new Text();
    private Stage primaryStage;
    public static int counter = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        moving_objects = new Hashtable<>();
        this.primaryStage = primaryStage;

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene firstScene = new Scene(grid, 300, 275);
        primaryStage.setScene(firstScene);
        Text gameTitle = new Text("Welcome to game");
        gameTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(gameTitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        TextField hostTextField = new TextField();
        hostTextField.setText("localhost");
        grid.add(hostTextField, 1, 2);
        Label hostName = new Label("Host Name:");
        grid.add(hostName, 0, 2);

        TextField portTextField = new TextField();
        portTextField.setText("8000");
        Label port = new Label("Port:");
        grid.add(portTextField, 1, 3);
        grid.add(port, 0, 3);

        Button btn = new Button("Play");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 5);


        grid.add(actiontarget, 1, 6);
        ClientGUI main_gui = this;
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                actiontarget.setFill(Color.FIREBRICK);

                ClientGUI gui = new ClientGUI();
                gui.setUserName(userTextField.getText());
                System.out.println(gui.getUserName());

                gui.setHostName(hostTextField.getText());
                System.out.println(gui.getHostName());

                gui.setPort(Integer.valueOf(portTextField.getText()));
                System.out.println(gui.getPort());

                actiontarget.setText("Play button pressed" + gui.getUserName() + " " + gui.getPort() + " " + gui.getHostName());

                setHostName(gui.getHostName());
                setUserName(gui.getUserName());
                setPort(gui.getPort());
                client = new Client(getHostName(), getPort(), getUserName());
                client.setGUI(main_gui);
                client.start();
                goToPlay();
            }
        });

        primaryStage.show();
    }

    static HashSet<String> currentlyActiveKeys;
    private static Scene gameScene = null;
    static GraphicsContext graphicsContext = null;
    static Label infoLabel;
    static Label playerListLabel;
    private static Circle circle;
    Alert alert = null;

    private static Integer index = 0;

    public void goToFinish(String message) {

        Text gameTitle = new Text(message);

        circle = new Circle(20, 20, 20);
        circle.setFill(Color.web(message.substring(message.lastIndexOf(".") + 1)));
        gameTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));


        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Game Information");
                    alert.setHeaderText("Game Result");
                    Text text = new Text(message.substring(0, message.lastIndexOf(".")));
                    alert.setContentText(text.getText());
                    alert.setGraphic(circle);
                    alert.showAndWait();
                });

                return null;
            }
        };

        new Thread(task).start();
    }

    public void goToPlay() {

        infoLabel = new Label("Waiting for Three Person");
        playerListLabel = new Label("NONE");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller = loader.getController();
        controller.clientGUI = this;

        primaryStage.setTitle(userName + ", " + hostName);
        primaryStage.setResizable(false);
        BorderPane border = new BorderPane();

        HBox hbox = addHBox();
        border.setTop(hbox);
        border.setCenter(((VBox) root));

        Scene gameScene = new Scene(border);
        prepareActionHandlers(gameScene);
        graphicsContext = controller.labyrinthCanvas.getGraphicsContext2D();
        primaryStage.setScene(gameScene);


        new AnimationTimer() {

            long last_frame_time = System.nanoTime();

            @Override
            public void handle(long now) {
                if (labyrinth != null) {
                    graphicsContext.setFill(Color.BLACK);
                    graphicsContext.fillRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
                    game_system.setDelta_time((now - last_frame_time) / 1000000000.0);
                    last_frame_time = now;
                    labyrinth.draw(graphicsContext);

                }
            }
        }.start();


    }

      /*
     * Creates an HBox with two buttons for the top region
     */

    public HBox addHBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(5);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #4dd6c6;");

        circle = new Circle(20, 20, 20);
        circle.setFill(Color.web(client.getColor()));
        hbox.getChildren().addAll(circle);

        return hbox;
    }

    private static void prepareActionHandlers(Scene gameScene) {
        // use a set so duplicates are not possible
        currentlyActiveKeys = new HashSet<String>();
        gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                currentlyActiveKeys.add(event.getCode().toString());
            }
        });
        gameScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                currentlyActiveKeys.remove(event.getCode().toString());
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void finishGame(String message, boolean finish) {
        if (finish && message != null) {
            goToFinish(message);

        }
    }

    public void updateGame(int[] keys, double[] x, double[] y) {

        for (int i = 0; i < keys.length; i++) {
            moving_objects.get(keys[i]).setPos_x(x[i]);
            moving_objects.get(keys[i]).setPos_y(y[i]);
        }
    }

    public void setColorCircle(String x){
        if(x !=null) {
            this.circle.setFill(Color.web(x));
        }
    }
    public void createLabyrinth(MainDimension dim) {
        labyrinth = new MainLabyrinth(dim);
    }

    public synchronized void addPlayer(Player player, int x, int y, int object_key) {
        synchronized (moving_objects) {
            moving_objects.put(object_key, labyrinth.addPlayer(player, 0, x, y));
        }
    }

    public void createFlag(double mouse_x, double mouse_y, Flag.flag_type type) {
        int x = labyrinth.getLabyrinth().getDimension().getDim_x();
        int y = labyrinth.getLabyrinth().getDimension().getDim_y();
        Canvas canvas = graphicsContext.getCanvas();
        double step_width = canvas.getWidth() / x;
        double step_height = canvas.getHeight() / y;
        int labyrinthCoorX = (int) Math.floor(mouse_x / step_width);
        int labyrinthCoorY = (int) Math.floor(mouse_y / step_height);
        System.out.println(labyrinthCoorX);
        System.out.println(labyrinthCoorY);

        switch (type) {
            case attract:
                client.addFlag(labyrinthCoorX, labyrinthCoorY, Flag.flag_type.attract);
                break;
            case repell:
                client.addFlag(labyrinthCoorX, labyrinthCoorY, Flag.flag_type.repell);
            default:
                System.out.println("NO FLAG TYPE WAS CHOOSEN!!!");
                break;
        }
    }

    public void addFlag(int x, int y, Flag.flag_type type) {
        Flag flag;
        switch (type) {
            case attract:
                flag = new SearchHereFlag(-1, x, y, labyrinth.getLabyrinth().getDimension().getDim_x(), labyrinth.getLabyrinth().getDimension().getDim_y());
                labyrinth.addFlag(flag);
                break;
            case repell:
                flag = new DontComeNearFlag(-1, x, y, labyrinth.getLabyrinth().getDimension().getDim_x(), labyrinth.getLabyrinth().getDimension().getDim_y());
                labyrinth.addFlag(flag);
                break;
        }
    }
}
