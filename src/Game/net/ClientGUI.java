package Game.net;

import Game.GameObjects.Player;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
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

    private Client client;
    private String userName;
    private String hostName;
    private Integer port;





    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
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
                client.startClient();
                client.makeRequest(wantMatch);

            }
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
