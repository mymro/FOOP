package game.net;

import game.Main;
import game.core.Flag;
import game.core.Vector_2;
import game.core.GameObject;
import game.game.objects.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server implements Serializable {

    private static final int PLAYERS_TO_START = 3;
    private int connected_players = 0;

    private ServerSocket ssocket = null;

    private Hashtable<Robot, Socket> userList = new Hashtable<>(); // Player-Socket pair list
    private Hashtable<Robot, ObjectOutputStream> userOutputStreamList = new Hashtable<>();

    private MainLabyrinth labyrinth = null;
    private List<String> colorList = new ArrayList<>();
    private Vector_2 dimension = null;
    private boolean game_started = false;
    private static Main.GameSystem game_system = Main.GameSystem.getInstance();

    private Hashtable<Integer, GameObject> moving_objects;
    private Integer current_key = 1;
    private long last_frame_time;

    public Server(int port) {
        colorList.add("#f44242");
        colorList.add("#42f480");
        colorList.add("#4341f4");
        moving_objects = new Hashtable<>();

        try {
            this.ssocket = new ServerSocket(port);
            dimension = new Vector_2(20, 20);
            labyrinth = new MainLabyrinth(dimension, 0);
        } catch (IOException e) {
            System.out.println("Server: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void startServer() throws UnknownHostException {

        Socket socket;
        System.out.println("Server started at "
                + InetAddress.getLocalHost().toString() + " port "
                + ssocket.getLocalPort());


        int countOfPlayer = 0;
        while (countOfPlayer < PLAYERS_TO_START) {
            try {
                socket = ssocket.accept();
                if (socket != null) {
                    System.out.println(socket + " : connected");
                    new ServerThread(socket, this); // create new thread
                    countOfPlayer++;
                    System.out.println("Waiting for three Person to play ......");

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public synchronized void addUser(ObjectOutputStream oos, Socket socket, String name){
        Message msg = new Message(Message.WELCOME);
        Robot robot;
        String color = colorList.get(connected_players);
        int new_key;

        synchronized (current_key){
            robot = labyrinth.addPlayer(color ,name);
            while (moving_objects.containsKey(current_key)){
                current_key++;
            }
            moving_objects.put(current_key, robot);
            msg.setObjectKey(current_key);
            new_key = current_key;
        }

        msg.setSeed(labyrinth.getSeed());
        msg.setVector2(labyrinth.getLabyrinth().getDimension());
        msg.setColor(color);
        msg.setPosX(robot.getPos_x());
        msg.setPosY(robot.getPos_y());

        try {
            oos.writeObject(msg);
            oos.flush();
        } catch (IOException e) {
            System.out.println("sendToClients : Ex:" + e.getMessage());
            e.printStackTrace();
        }

        msg.setType(Message.ADD_PLAYER);
        sendToClients(msg);//notify other clients of new player

        synchronized (moving_objects){//tell new client about other players
            for (Integer key: moving_objects.keySet()) {
                if(moving_objects.get(key) instanceof Robot && new_key != key){
                    Robot current_robot = (Robot) moving_objects.get(key);
                    Message message = new Message(Message.ADD_PLAYER);
                    message.setObjectKey(key);
                    message.setColor(current_robot.getPlayer().getColor());
                    message.setPosX(current_robot.getPos_x());
                    message.setPosY(current_robot.getPos_y());
                    try {
                        oos.writeObject(message);
                        oos.flush();
                    } catch (IOException e) {
                        System.out.println("sendToClients : Ex:" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }

        synchronized (userList) {
            userList.put(robot, socket);
        }
        synchronized (userOutputStreamList) {
            userOutputStreamList.put(robot, oos);
        }
        System.out.println(robot.getPlayer() + " connected ");

        connected_players++;

        if (connected_players >= PLAYERS_TO_START){
            Runnable gameRunnable = new Runnable() {
                public void run() {
                    update();
                }
            };

            last_frame_time = System.nanoTime();
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(gameRunnable, 1000, 16, TimeUnit.MILLISECONDS);//60 FPS
            // has to be shutdown on close
        }
    }

    public void addFlag(int x, int y, Flag.flag_type type){
        Flag flag;
        switch (type) {
            case attract:
                flag = new SearchHereFlag(-1, x, y, labyrinth.getLabyrinth().getDimension().getDim_x(), labyrinth.getLabyrinth().getDimension().getDim_y());
                labyrinth.addFlag(flag);
                break;
            case repell:
                flag = new DontComeNearFlag(-1, x, y, labyrinth.getLabyrinth().getDimension().getDim_x(), labyrinth.getLabyrinth().getDimension().getDim_y());
                labyrinth.addFlag(flag);
                break;
        }
        Message msg = new Message(Message.ADD_FLAG);
        msg.setPosX(x);
        msg.setPosY(y);
        msg.setFlagType(type);
        sendToClients(msg);
    }

    private void update(){
        long now = System.nanoTime();
        game_system.setDelta_time((now-last_frame_time)/1000000000.0);
        last_frame_time = now;
        labyrinth.update();
        Message msg = new Message(Message.UPDATE);

        int[] keys = new int[moving_objects.size()];
        double[] x = new double[moving_objects.size()];
        double[] y = new double[moving_objects.size()];
        int i = 0;

        for (Integer key: moving_objects.keySet()){
            keys[i] = key;
            x[i] = moving_objects.get(key).getPos_x();
            y[i] = moving_objects.get(key).getPos_y();
            i++;
        }

        msg.setNewPositions(keys, x,y);

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

    long getSeed(){
        return labyrinth.getSeed();
    }

    Vector_2 getLabyrinthDimension(){
        return labyrinth.getLabyrinth().getDimension();
    }
}
