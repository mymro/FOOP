package Game.net;

import Game.Core.Dimension;
import Game.GameObjects.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Vector;

public class Client extends Thread {

    private String serverIP;
    private int port;
    private String userName;
    private Message currentMessage;
    private boolean run = true;
    private Player player = null;

    ObjectInputStream ois;
    ObjectOutputStream oos;

    private ServerSocket clsSocket;
    private Socket socket;

    public static ClientGUI clientGUI;
    private MainLabyrinth mainLabyrinth = null;
    private Dimension dimension = null;

    public Client(String serverIP, int port, String color, String userName) {
        this.serverIP = serverIP;
        this.port = port;
        this.userName = userName;
        player = new Player(userName, color, serverIP.toString(), port);
        clientGUI = new ClientGUI();

    }

    public void startClient() {
        connectToServer();
        try {
            while (run) {
                Message message = (Message) ois.readObject();
                handleMessage(message);

            }
        } catch (SocketException e) {
            // clientGUI.showMessage("Server : " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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

    @Override
    public void run() {

        try {
            clsSocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                Socket socket = clsSocket.accept();
                if (socket != null) {
                    System.out.println(userName + "accepted: ");
                    try {
                        new ClientThread(this, socket, player).join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startGame(Vector<Player> userList) {

        System.out.println("The game can be started we are three persons");
        dimension = new Dimension(50, 50);
        mainLabyrinth = new MainLabyrinth(dimension, 0);

        int j = 5;
        for (Player player : userList) {

            mainLabyrinth.addPlayer(player, 0);
            System.out.println("Player: " + player);
            Robot robot = new Robot(0, player);
            SearchHereFlag flag = new SearchHereFlag(-30, 10, 10, dimension.getDim_x(), dimension.getDim_y(), robot);
            DontComeNearFlag flag2 = new DontComeNearFlag(-30 + j, 40 - j, 40 + j, dimension.getDim_x(), dimension.getDim_y(), robot);
            mainLabyrinth.addFlag(flag);
            mainLabyrinth.addFlag(flag2);
            mainLabyrinth.update();

        }

    }

    private void handleMessage(Message message) {

        switch (message.getType()) {

            case Message.USERS_LIST:
                this.currentMessage = message;
                System.out.println("Ich bin in CLIENT in Type" + "USERS_LIST");
                startGame(message.getUserList());

                break;

            case Message.BYE:
                run = false;
                this.currentMessage = message;
                System.out.println("Ich bin in CLIENT in Type" + "BYE");
                break;

            case Message.USER_INFO:
                this.currentMessage = message;
                System.out.println("Ich bin in CLIENT in Type" + "REQUEST_MATCH ");
                break;

            case Message.REQUEST_MATCH: { // rival
                this.currentMessage = message;
                System.out.println("Ich bin in CLIENT in Type" + "REQUEST_MATCH ");
                break;
            }

            case Message.START_MATCH:
                this.currentMessage = message;
                System.out.println("Ich bin in CLIENT in Type" + "START_MATCH ");

                try {
                    new ClientThread(this, this.currentMessage).join();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }


    private void connectToServer() { // connect to server

        try {
            this.socket = new Socket(InetAddress.getByName(serverIP), this.port);

            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());

            ois.readObject();
            Message connectMessage = new Message(Message.CLIENT_CONNECT);
            connectMessage.setPlayer(player);
            //System.out.println(InetAddress.getLocalHost().getHostAddress().toString() + ":" + clsSocket.getLocalPort());
            connectMessage.setMessage(InetAddress.getLocalHost().getHostAddress().toString() + ":" + clsSocket.getLocalPort());
            sendToServer(connectMessage); // send connect info to server

        } catch (UnknownHostException e) {
            //clientGUI.showMessage("Unable to connect server\n" + e.getMessage());
            System.exit(0);
        } catch (IOException e) {
            // clientGUI.showMessage("Unable to connect server\n" + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void makeRequest(Message message) {
        sendToServer(message);
    }

    public void disconnectFromServer() { // disconnect procedure
        sendToServer(new Message(Message.CLIENT_DISCONNECT, player));
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

    public void setCurrentMessage(Message currentMessage) {
        this.currentMessage = currentMessage;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public MainLabyrinth getMainLabyrinth() {
        return mainLabyrinth;
    }

    public void setMainLabyrinth(MainLabyrinth mainLabyrinth) {
        this.mainLabyrinth = mainLabyrinth;
    }
}
