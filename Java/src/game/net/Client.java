package game.net;

import game.core.*;
import game.game.objects.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class Client extends Thread {

    private String serverIP;
    private int port;
    private String userName;
    private Message currentMessage;
    private boolean run = true;
    private Player player;

    ObjectInputStream ois;
    ObjectOutputStream oos;

    private ServerSocket clsSocket;
    private Socket socket;

    public static ClientGUI clientGUI;
    private Vector_2 dimension = null;


    public Client(String serverIP, int port, String userName) {
        this.serverIP = serverIP;
        this.port = port;
        this.userName = userName;

    }

    @Override
    public void run() {

        connectToServer();
        Message message;
        try {
            while (run) {
                message = (Message) ois.readObject();
                if(message != null){
                    handleMessage(message);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                System.out.println(userName + " terminating");
                if (ois != null) {
                    ois.close();
                }
                if (oos != null) {
                    oos.close();
                }
                if (socket != null) {
                    socket.close();
                }
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(Message message) {

        Player new_player;
        switch (message.getType()) {
            case Message.ADD_PLAYER:
                new_player = new Player(message.getName(), message.getColor());
                this.player = new_player;
                clientGUI.addPlayer(new_player, (int)message.getPosX(), (int)message.getPosY(), message.getObjectKey());
                break;
            case Message.WELCOME:
                MainDimension dim = new MainDimension(message.getVector2().getDim_x(), message.getVector2().getDim_y(), 0, message.getSeed());
                clientGUI.createLabyrinth(dim);
                new_player = new Player(userName, message.getColor());
                this.player = new_player;
                clientGUI.addPlayer(new_player, (int)message.getPosX(), (int)message.getPosY(), message.getObjectKey());
                break;
            case Message.UPDATE:
                clientGUI.updateGame(message.getKeys(), message.getNew_positions_x(), message.getNew_positions_y());
                break;
            case Message.ADD_FLAG:
                clientGUI.addFlag((int)message.getPosX(), (int)message.getPosY(), message.getFlagType());
                break;
            default:
                break;
        }
    }


    private void connectToServer() {//waits until labyrinth is created

        try {
            this.socket = new Socket(InetAddress.getByName(serverIP), this.port);

            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());

            Message connectMessage = new Message(Message.CLIENT_CONNECT);
            connectMessage.setMessage(userName);
            sendToServer(connectMessage);

            Message message;
            while (true){// wait for welcome message
                message = (Message) ois.readObject();
                if(message != null && message.getType() == Message.WELCOME){
                    handleMessage(message);
                    break;
                }
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addFlag(int x, int y, Flag.flag_type type){
        Message msg = new Message(Message.ADD_FLAG);
        msg.setPosY(y);
        msg.setPosX(x);
        msg.setFlagType(type);
        sendToServer(msg);
    }

    public void sendToServer(Message message) {
        try {
            oos.writeObject(message);
            oos.flush();
        } catch (IOException e) {
            //clientGUI.showMessage("IO Error:\n" + e.getMessage());
        }
    }

    public Message getCurrentMessage() {
        return currentMessage;
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getPort() {
        return port;
    }

    public void setGUI(ClientGUI gui){
        clientGUI = gui;
    }
}
