package Game.net.netzwerk;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ChatApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private boolean isServer = true;

    private TextArea messages = new TextArea();
    private NetwerkConnection connection = isServer ? createServer() : createClient();

    @Override
    public void init() throws  Exception {
        connection.startConnection();
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Chat");
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }

    @Override
    public void stop() throws  Exception {
        connection.closeConnection();
    }

    private Server createServer(){
        return new Server(55555,data -> {
            Platform.runLater(()->{
                messages.appendText(data.toString() + "\n");
            });
        });
    }
    private Client createClient() {
        return new Client("localhost",55555,data -> {
            Platform.runLater(()->{
                messages.appendText(data.toString() + "\n");
            });
        });
    }
    private Parent createContent() {
        messages.setPrefHeight(550);
        TextField input = new TextField();
        input.setOnAction(event -> {
            String message = isServer ?"Server: " : "Client";
            message += input.getText();
            input.clear();

            messages.appendText(message + " \n");
            try {
                connection.send(message);
            } catch (Exception e) {
                messages.appendText("Failed to Send");
            }
        });
        VBox root = new VBox(20,messages,input);
        root.setPrefSize(600,600);
        return root;
    }
}
