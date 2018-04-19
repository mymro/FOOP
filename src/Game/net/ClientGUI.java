package Game.net;

import Game.Controller;
import Game.GameObjects.MainLabyrinth;
import Game.GameObjects.Player;
import Game.Main;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.application.Platform;


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
    private Main.GameSystem game_system = Main.GameSystem.getInstance();
    private Controller controller=null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene firstScene = new Scene(grid, 300, 275);
        primaryStage.setScene(firstScene);
        Text gameTitle = new Text("Welcome to Game");
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

        final Text actiontarget = new Text();
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

                goToPlay(primaryStage,gui.getUserName(), gui.getHostName() +":" +gui.getPort(),client);

               // client.makeRequest(wantMatch);
               // System.out.println(client.getCurrentMessage().getType());




            }
        });

        primaryStage.show();
    }
   public void goToPlay(Stage primaryStage, String username, String hostName,Client client) {
       Label lbl = new Label("Waiting for Three Person");
       Task<Void> task = new Task<Void>() {
           @Override
           protected Void call() throws Exception {
               client.startClient();
               return null;
           }

           @Override
           protected void succeeded() {
               System.out.println("work done!");
               new AnimationTimer(){

                   long last_frame_time = System.nanoTime();

                   @Override
                   public void handle(long now) {
                       GraphicsContext gc = controller.labyrinthCanvas.getGraphicsContext2D();

                       controller.setLabyrinth(game_system.getLabyrinth());
                       gc.clearRect(0,0,gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
                       gc.setFill(Color.BLACK);
                       gc.fillRect(0,0,gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
                       game_system.getLabyrinth().update();
                       game_system.getLabyrinth().draw(gc);
                       game_system.setDelta_time((now- last_frame_time)/1000000000.0);
                       last_frame_time = now;
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
       Controller controller = loader.getController();
       primaryStage.setTitle(username + ", " + hostName);
       primaryStage.setResizable(false);


       lbl.setFont(Font.font("Amble CN", FontWeight.BOLD, 24));
       ((VBox) root).getChildren().add(lbl);

       primaryStage.setScene(new Scene(root));



   }
    public static void main(String[] args) {
        launch(args);
    }

    public static final class GameSystem {
        private static Main.GameSystem instance;
        private double delta_time; // in nanoseconds
        public MainLabyrinth labyrinth;



        private GameSystem(){
            delta_time = 0;
        }

        public static Main.GameSystem getInstance(){
            if (instance == null){
                instance = Main.GameSystem.getInstance();
            }
            return instance;
        }

        public double deltaTime(){
            return delta_time;
        }

        public MainLabyrinth getLabyrinth() {
            return labyrinth;
        }

        public void setLabyrinth(MainLabyrinth labyrinth) {
            this.labyrinth = labyrinth;
        }
    }


}
