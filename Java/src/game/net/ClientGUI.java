package game.net;

import game.Controller;
import game.game.objects.MainLabyrinth;
import game.game.objects.Player;
import game.Main;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;

import static java.awt.image.ImageObserver.HEIGHT;
import static java.awt.image.ImageObserver.WIDTH;


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

    private Client client;
    private String userName;
    private String hostName;
    private Integer port;
    private static Main.GameSystem game_system = Main.GameSystem.getInstance();
    private static Controller controller = null;
    public static Text actiontarget = new Text();

    @Override
    public void start(Stage primaryStage) throws Exception {
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


                Player player = new Player(gui.getUserName(), null, gui.getHostName(), gui.getPort());
                Message wantMatch = new Message(Message.REQUEST_MATCH, player); // Firstly send a request

                client = new Client(gui.getHostName(), gui.getPort(), "RED", gui.getUserName());
                client.start();

                goToPlay(primaryStage, gui.getUserName(), gui.getHostName() + ":" + gui.getPort(), client);

                // client.makeRequest(wantMatch);
                // System.out.println(client.getCurrentMessage().getType());


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

    public void goToPlay(Stage primaryStage, String username, String hostName, Client client) {

        infoLabel = new Label("Waiting for Three Person");
        playerListLabel = new Label("NONE");
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                client.startClient();
                return null;
            }

            @Override
            protected void succeeded() {
                System.out.println("work done!");

                new AnimationTimer() {

                    long last_frame_time = System.nanoTime();

                    @Override
                    public void handle(long now) {
                        graphicsContext = controller.labyrinthCanvas.getGraphicsContext2D();
                        game_system.setLabyrinth(client.getMainLabyrinth());
                        graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
                        graphicsContext.setFill(Color.BLACK);
                        graphicsContext.fillRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
                        game_system.getLabyrinth().update();
                        game_system.getLabyrinth().draw(graphicsContext);
                        game_system.setDelta_time((now - last_frame_time) / 1000000000.0);
                        last_frame_time = now;
                        infoLabel.setText(client.getCurrentMessage().getMessage());

                    }
                }.start();
            }
        };

        new Thread(task).start();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller = loader.getController();
        primaryStage.setTitle(username + ", " + hostName);
        primaryStage.setResizable(false);

        infoLabel.setFont(Font.font("Amble CN", FontWeight.BOLD, 16));
        circle = new Circle(5, 5, 5);
        ((VBox) root).getChildren().add(infoLabel);
        ((VBox) root).getChildren().add(playerListLabel);
        ((VBox) root).getChildren().add(circle);

        Scene gameScene = new Scene(root);
        prepareActionHandlers(gameScene);
        graphicsContext = controller.labyrinthCanvas.getGraphicsContext2D();
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                tickAndRender(client);
                infoLabel.setText(client.getCurrentMessage().getMessage());
            }
        }.start();
        primaryStage.setScene(gameScene);


    }

    private static String getPlayerList(Client client) {
        String result = "";
        if (client.getCurrentMessage() != null) {
            for (Player player : client.getCurrentMessage().getUserList()) {
                result += "Name:: " + player.getName() + " Color: " + player.getColor() + " :: \n";
            }
        }
        if(client.getCurrentMessage().getUserList().size() == 3){
            result +="\n" + "Please click on LEFT tab to play game";
        }
        return result;
    }

    private static void tickAndRender(Client client) {
        // clear canvas

        circle.setFill(Color.valueOf(client.getColorInfo()));
        if (currentlyActiveKeys.contains("LEFT")) {

            new AnimationTimer() {

                long last_frame_time = System.nanoTime();

                @Override
                public void handle(long now) {
                    graphicsContext = controller.labyrinthCanvas.getGraphicsContext2D();
                    game_system.setLabyrinth(client.getMainLabyrinth());
                    graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
                    graphicsContext.setFill(Color.BLACK);
                    graphicsContext.fillRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
                    game_system.getLabyrinth().update();
                    game_system.getLabyrinth().draw(graphicsContext);
                    game_system.setDelta_time((now - last_frame_time) / 1000000000.0);

                    last_frame_time = now;
                    infoLabel.setText(client.getCurrentMessage().getMessage());
                    if (!game_system.isRunning) {
                        infoLabel.setText(game_system.getMessage());
                    }

                }
            }.start();
        } else {
            GraphicsContext graphicsContext = controller.labyrinthCanvas.getGraphicsContext2D();

            graphicsContext.setFill(Color.GREEN);
            //graphicsContext.fillText(getPlayerList(client) + "sddsfdsf ", 1,1,1000);
            graphicsContext.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            graphicsContext.getCanvas().getGraphicsContext2D().fillText(getPlayerList(client) + " ", 25,25,1500);
            //graphicsContext.fillRect(0, 0, graphicsContext.getCanvas().getWidth(), graphicsContext.getCanvas().getHeight());
        }
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

    public void updateGame() {
        game_system.getLabyrinth().update();
    }

    public static Main.GameSystem getGame_system() {
        return game_system;
    }
}
