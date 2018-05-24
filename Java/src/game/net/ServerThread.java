package game.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class ServerThread extends Thread {

    private Server server;
    private Socket clientSocket;

    ObjectInputStream ois;
    ObjectOutputStream oos;

    public ServerThread(Socket clientSocket, Server server) {
        super("ServerThread");
        this.clientSocket = clientSocket;
        this.server = server;

        start();
    }

    @Override
    public void run() {

        try {
            oos = new ObjectOutputStream(getClientSocket().getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(getClientSocket().getInputStream());
        } catch (IOException e) {
            System.out.println("ServerThread streams: " + e);
        }

        sendMessage(new Message(Message.WELCOME));

        try {
            while (true) {
                Message message = (Message) ois.readObject(); // get a message
                // from Client
                processClientMessage(message); // handle this message
                if(message.getType() == Message.CLIENT_DISCONNECT)
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                oos.close();
                ois.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processClientMessage(Message message) { // handle message and answer message (you must answer!)

        switch (message.getType()) {

            case Message.CLIENT_CONNECT:
                server.addUser(message.getPlayer(), this.clientSocket, this.oos, message.getMessage());
                server.sendUserList();
                break;

            case Message.CLIENT_DISCONNECT:
                server.removeUser(message.getPlayer());
                sendMessage(new Message(Message.BYE));
                server.sendUserList();
                break;

            case Message.USERS_LIST:
                server.sendUserList();
                break;

            case Message.REQUEST_MATCH:
                sendMessageTo(message, server.getUserOutputStreamList().get(message.getPlayer().getName())); // to special user(request)
                break;

            case Message.START_MATCH:
                message.setMessage("Start_GAME");
                sendMessage(new Message(Message.START_MATCH));; // to special user(request)
                break;

            default:
                break;
        }
    }

    public void sendMessage(Message message) {
        try{
            oos.writeObject(message);
            oos.flush();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    public void sendMessageTo(Message message, ObjectOutputStream oos) {
        try{
            oos.writeObject(message);
            oos.flush();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public ObjectInputStream getOis() {
        return ois;
    }

    public void setOis(ObjectInputStream ois) {
        this.ois = ois;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    public void setOos(ObjectOutputStream oos) {
        this.oos = oos;
    }
}
