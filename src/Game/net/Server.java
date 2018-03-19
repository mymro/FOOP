package Game.net;

import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.Vector;

public class Server {

    private int port;
    private ServerSocket ssocket = null;
    private int numConnections = 0;

    private Hashtable<String, Socket> userList = new Hashtable<String, Socket>(); // userName-Socket pair list
    private Hashtable<String, String> userInfoList = new Hashtable<String, String>();
    private Hashtable<String, ObjectOutputStream> userOutputStreamList = new Hashtable<String, ObjectOutputStream>();

    private File inputFile = null;
    private FileWriter fwriter;
    private PrintWriter writer;

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
        createLogFile();

        while (true) {
            try {
                socket = ssocket.accept();
                if (socket != null) {
                    writeToLog(socket, "connected");
                    new ServerThread(socket, this); // create new thread
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void addUser(String userName, Socket socket,
                                     ObjectOutputStream oos, String info) {

        synchronized (userList) {
            userList.put(userName, socket);
        }
        synchronized (userOutputStreamList) {
            userOutputStreamList.put(userName, oos);
        }
        synchronized (userInfoList) {
            userInfoList.put(userName, info);
        }
        System.out.println(userName + " connected ");
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

    public synchronized void removeUser(String userName) {
        synchronized (userList) {
            writeToLog(userList.get(userName), "disconnected");
            userList.remove(userName);
            System.out.println(userName + " disconnected");
        }
        synchronized (userOutputStreamList) {
            userOutputStreamList.remove(userName);
        }
        synchronized (userInfoList) {
            userInfoList.remove(userName);
        }
    }

    public void createLogFile() {
        this.inputFile = new File("server.log");
        try {
            this.fwriter = new FileWriter(inputFile, true);
        } catch (IOException e) {
            System.out.println("Error creating log file : "
                    + inputFile.getName());
        }
        this.setWriter(new PrintWriter(fwriter));
        writer.close();
    }

    private void writeToLog(Socket socket, String cond) {
        try {
            writer = new PrintWriter(new FileWriter(this.inputFile, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.println(socket.getLocalAddress().getHostAddress() + " "
                + socket.getLocalPort() + " " + cond);
        writer.close();
    }

        /* GET - SET METHODLARI... */

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

    public Hashtable<String, Socket> getUserList() {
        return userList;
    }

    public void setUserList(Hashtable<String, Socket> userList) {
        this.userList = userList;
    }

    public Hashtable<String, String> getUserInfoList() {
        return userInfoList;
    }

    public void setUserInfoList(Hashtable<String, String> userInfoList) {
        this.userInfoList = userInfoList;
    }

    public Hashtable<String, ObjectOutputStream> getUserOutputStreamList() {
        return userOutputStreamList;
    }

    public void setUserOutputStreamList(Hashtable<String, ObjectOutputStream> userOutputStreamList) {
        this.userOutputStreamList = userOutputStreamList;
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public FileWriter getFwriter() {
        return fwriter;
    }

    public void setFwriter(FileWriter fwriter) {
        this.fwriter = fwriter;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }
}
