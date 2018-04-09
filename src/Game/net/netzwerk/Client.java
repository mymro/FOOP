package Game.net.netzwerk;

import java.io.Serializable;
import java.util.function.Consumer;

public class Client extends  NetwerkConnection {

    private String ip;
    private int  port;

    public Client(String ip, int port, Consumer<Serializable> onRecevieCallBack){
        super(onRecevieCallBack);
        this.ip = ip;
        this.port = port;
    }

    @Override
    protected boolean isServer() {
        return false;
    }

    @Override
    protected String getIP() {
        return ip;
    }

    @Override
    protected int getPort() {
        return port;
    }
}
