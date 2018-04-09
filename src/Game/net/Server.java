package Game.net;

import Game.GameObjects.*;
import Game.Main;
import javafx.animation.AnimationTimer;
import javafx.application.Application;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class Server {

    private int port;
    private ServerSocket ssocket = null;
    private int numConnections = 0;


    private Hashtable<Player, Socket> userList = new Hashtable<Player, Socket>(); // Player-Socket pair list
    private Hashtable<Player, String> userInfoList = new Hashtable<Player, String>();
    private Hashtable<Player, ObjectOutputStream> userOutputStreamList = new Hashtable<Player, ObjectOutputStream>();

    private MainLabyrinth labyrinth = null;
    private List<String> colorList = new ArrayList<>();

    public Server(int port) {
        colorList.add("RED");
        colorList.add("YELLOW");
        colorList.add("GREEN");
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


        int countOfPlayer = 0;
        while (countOfPlayer < 3) {
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

        startGame();


    }

    public  void startGame() {
        Dimension dimension = new Dimension(50, 50);
        labyrinth = new MainLabyrinth(dimension, 0);
        System.out.println("The game can be started we are three persons");

        int i = 0;
        int j = 5;
        for (Player player : userList.keySet()) {
            i++;
            labyrinth.addPlayer(player, -i);
            System.out.println("Player: " + i + player);
            Robot robot = new Robot(i, player);
            SearchHereFlag flag = new SearchHereFlag(-30, 10, 10, dimension.getDim_x(),dimension.getDim_y(), robot);
            DontComeNearFlag flag2 = new DontComeNearFlag(-30 +j, 40 - j, 40 + j, dimension.getDim_x(),dimension.getDim_y(),robot);
            labyrinth.addFlag(flag);
            labyrinth.addFlag(flag2);
            labyrinth.update();
        }
        Main.GameSystem game_system = Main.GameSystem.getInstance();
        game_system.setLabyrinth(labyrinth);
        new AnimationTimer() {
            long last_frame_time = System.nanoTime();

            @Override
            public void handle(long now) {
                game_system.labyrinth.update();
                last_frame_time = now;
            }
        }.start();
        Application.launch(Main.class);
    }

    public synchronized void addUser(Player player, Socket socket,
                                     ObjectOutputStream oos, String info) {
        if(!userList.isEmpty()) {
            player.setColor(colorList.get(userList.size()));
        }

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
