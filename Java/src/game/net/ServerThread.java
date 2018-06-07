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

        try {
            Message incoming_message;
            while (true) {
                incoming_message = (Message) ois.readObject();
                if(incoming_message != null){
                    processClientMessage(incoming_message); // handle this message
                }
            }

        } catch (IOException e) {
            System.out.println("Socket is closed");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                oos.close();
                ois.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Socket is closed");
            }
        }
    }

    private void processClientMessage(Message message) { // handle message and answer message (you must answer!)

        switch (message.getType()) {

            case Message.CLIENT_CONNECT:
                server.addUser(oos, clientSocket, message.getMessage());
                break;
            case Message.ADD_FLAG:
                server.addFlag((int)message.getPosX(), (int)message.getPosY(), message.getFlagType());
                break;
            case Message.CLIENT_DISCONNECT:
                server.removeUser(oos,clientSocket, message);
                sendMessageTo(new Message(Message.BYE), oos);
                break;
            case Message.BYE:
                server.removeUser(oos,clientSocket, message);
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
            System.out.println("Socked closed and disconnected");
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
