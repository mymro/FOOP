package Game.net;

import Game.Core.Flag;
import Game.GameObjects.*;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.Vector;

public class Server {

    private int port;
    private ServerSocket ssocket = null;
    private int numConnections = 0;

    private Hashtable<Player, Socket> userList = new Hashtable<Player, Socket>(); // Player-Socket pair list
    private Hashtable<Player, String> userInfoList = new Hashtable<Player, String>();
    private Hashtable<String, Player> users = new Hashtable<String, Player>();
    private Hashtable<Player, ObjectOutputStream> userOutputStreamList = new Hashtable<Player, ObjectOutputStream>();

    private MainLabyrinth labyrinth = null;

    public Server(int port) {

        this.port = port;
        try {
            this.ssocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Server: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void startServer() throws UnknownHostException {

        Socket socket = null;
        System.out.println("Server started at "
                + InetAddress.getLocalHost().toString() + " port "
                + ssocket.getLocalPort());
        Dimension dimension= new Dimension(32, 32);
         labyrinth = new MainLabyrinth(dimension,20);

        int countOfPlayer = 0;
        while (countOfPlayer < 3) {
            try {
                socket = ssocket.accept();
                if (socket != null) {
                    System.out.println(socket + " : connected");
                    new ServerThread(socket, this); // create new thread
                    String username = InetAddress.getLocalHost().toString() + ":" + ssocket.getLocalPort();

                    Player player = new Player(username, Color.RED, InetAddress.getLocalHost().toString(), ssocket.getLocalPort());
                    labyrinth.addPlayer(player, 0);

                    Robot robot = new Robot(0, player);
                    Flag fl = new DontComeNearFlag(0, 1, 1, 32, 32,robot);
                    labyrinth.addFlag(fl);
                    countOfPlayer ++;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        for(Player p : users.values()){
            labyrinth.update();
        }

    }

    public synchronized void addUser(Player player, Socket socket,
                                     ObjectOutputStream oos, String info) {

        synchronized (userList) {
            userList.put(player, socket);
        }
        synchronized (userOutputStreamList) {
            userOutputStreamList.put(player, oos);
        }
        synchronized (userInfoList) {
            userInfoList.put(player, info);
        }
        System.out.println(player + " connected ");
    }

    public void sendUserList() {
        Message msg = new Message(Message.USERS_LIST);
        msg.setUserList(new Vector(userInfoList.keySet()));
        sendToClients(msg);
    }

    public void sendToClients(Message message) {
        System.out.println(message.getUserList());
        for (ObjectOutputStream oos : userOutputStreamList.values()) {
            try {
                oos.writeObject(message);
                oos.flush();
            } catch (IOException e) {
                System.out.println("sendToClients : Ex:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public synchronized void removeUser(Player player) {
        synchronized (userList) {
            System.out.println(userList.get(player.getName()) + " : disconnected");
            userList.remove(player);
            System.out.println(player.getName() + " disconnected");
        }
        synchronized (userOutputStreamList) {
            userOutputStreamList.remove(player);
        }
        synchronized (userInfoList) {
            userInfoList.remove(player);
        }
    }

    public static void main(String[] args) { // Main program
        try {
            Server labyrinthServer = new Server(Integer.parseInt(args[0]));
            labyrinthServer.startServer();
        } catch (UnknownHostException e) {
            System.out.println("Server error :" + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("missing parameter!");
            System.exit(0);
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ServerSocket getSsocket() {
        return ssocket;
    }

    public void setSsocket(ServerSocket ssocket) {
        this.ssocket = ssocket;
    }

    public int getNumConnections() {
        return numConnections;
    }

    public void setNumConnections(int numConnections) {
        this.numConnections = numConnections;
    }

    public Hashtable<Player, Socket> getUserList() {
        return userList;
    }

    public void setUserList(Hashtable<Player, Socket> userList) {
        this.userList = userList;
    }

    public Hashtable<Player, String> getUserInfoList() {
        return userInfoList;
    }

    public void setUserInfoList(Hashtable<Player, String> userInfoList) {
        this.userInfoList = userInfoList;
    }

    public Hashtable<Player, ObjectOutputStream> getUserOutputStreamList() {
        return userOutputStreamList;
    }

    public void setUserOutputStreamList(Hashtable<Player, ObjectOutputStream> userOutputStreamList) {
        this.userOutputStreamList = userOutputStreamList;
    }
}
