package Game.GameObjects;

import javafx.scene.paint.Color;

public class Player {
    private String name;
    private String url;
    private int port;
    private Color color;

    public Player(String name, Color color, String url, int port){
        this.name = name;
        this.color = color;
        this.url = url;
        this.port = port;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
