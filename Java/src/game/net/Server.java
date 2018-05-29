package game.net;

import game.Main;
import game.core.Dimension;
import game.core.Serialization;
import game.game.objects.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class Server implements Serializable {

    private int port;
    private ServerSocket ssocket = null;
    private int numConnections = 0;


    private Hashtable<Player, Socket> userList = new Hashtable<Player, Socket>(); // Player-Socket pair list
    private Hashtable<Player, String> userInfoList = new Hashtable<Player, String>();
    private Hashtable<Player, ObjectOutputStream> userOutputStreamList = new Hashtable<Player, ObjectOutputStream>();

    private MainLabyrinth labyrinth = null;
    private List<String> colorList = new ArrayList<>();
    private static Integer startIndex = 0;
    private Dimension dimension = null;
    private static Main.GameSystem game_system = Main.GameSystem.getInstance();
    public Server(int port) {
        colorList.add("RED");
        colorList.add("BLUE");
        colorList.add("GREEN");
        this.port = port;
        try {
            this.ssocket = new ServerSocket(port);
            dimension = new Dimension(50, 50);
            labyrinth = new MainLabyrinth(dimension, 0);
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


        int countOfPlayer = 0;
        while (countOfPlayer < 2) {
            try {
                socket = ssocket.accept();
                if (socket != null) {
                    System.out.println(socket + " : connected");
                    new ServerThread(socket, this); // create new thread
                    countOfPlayer++;
                    System.out.println("Waiting three Person to play ......");

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public synchronized void addUser(Player player, Socket socket,
                                     ObjectOutputStream oos, String info) {
        if (!userList.isEmpty()) {
            player.setColor(colorList.get(userList.size()));
        }

        synchronized (userList) {

            userList.put(player, socket);

             if(userList.size()> 1) {
                 startGame();
            }
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
        msg.setUserList(new Vector(userList.keySet()));
        msg.setMainLabyrinth(labyrinth);
        //Serialization.serialize(labyrinth,"game.dat");
        sendToClients(msg);
    }

    public void addFlag(Message message) {
        Message msg = new Message(Message.SEND_FLAG);
        msg.setUserList(new Vector<>(userList.keySet()));

        //labyrinth.addFlag(message.getFlag());
        msg.setMainLabyrinth(labyrinth);
        msg.setFlag(message.getFlag());
        //Serialization.serialize(labyrinth,"game.dat");
        sendToClients(msg);
    }

    public void startGame() {
        Message msg = new Message(Message.START_MATCH);
        msg.setUserList(new Vector<>(userList.keySet()));
        msg.setMainLabyrinth(labyrinth);
        //Serialization.serialize(labyrinth,"game.dat");
        sendToClients(msg);
    }


    public void sendToClients(Object message) {
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
