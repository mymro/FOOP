package Game.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import javax.swing.JOptionPane;

public class Client extends Thread {
	
	private String serverIP;
	private int port;
	private String userName;
	private String rivalName;
	private Message rivalInfo;
	private boolean run = true;
	
	ObjectInputStream ois;
	ObjectOutputStream oos;
	
	private ServerSocket clsSocket;
	private Socket socket;
	
	public static ClientGUI clientGUI;
	
	public Client(String serverIP, int port, String userName) {
		this.serverIP = serverIP;
		this.port = port;
		this.userName = userName;
		
		clientGUI = new ClientGUI(Client.this, Client.this.userName);
	}
	
	public void startClient() {	
		connectToServer();
		
		try {
			while(run) {		
					Message message = (Message) ois.readObject();				
					handleMessage(message);
			}
		} catch (SocketException e) {
			clientGUI.showMessage("Server : " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				System.out.println(userName + " terminating");
				ois.close();
				oos.close();
				socket.close();
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
					System.out.println(userName + "accepted: " );
					new ClientThread(this, socket, userName, rivalName);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	
	private void handleMessage(Message message) {
		
		switch (message.getType()) {
		
		case Message.USERS_LIST:
			clientGUI.updateUserList(message.getUserList());
			break;
			
		case Message.BYE:
			run = false;
			clientGUI.showMessage("Server : BYE");
			break;
			
		case Message.USER_INFO:
			this.rivalInfo = message;
			break;
			
		case Message.REQUEST_MATCH: { // rival
			
			int response = JOptionPane.showConfirmDialog(null,
					message.getUserName()
							+ ": want to play match?", "Match Request",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if(response == JOptionPane.YES_OPTION) { // send accept to requester
				this.rivalName = message.getUserName();
				message.setType(Message.START_MATCH);				
				sendToServer(message);
			}
			else {
				
			}
		};break;
		
		case Message.START_MATCH:
			new ClientThread(this, this.rivalInfo);
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
			connectMessage.setUserName(userName);
			//System.out.println(InetAddress.getLocalHost().getHostAddress().toString() + ":" + clsSocket.getLocalPort());
			connectMessage.setMessage(InetAddress.getLocalHost().getHostAddress().toString() + ":" + clsSocket.getLocalPort());
			sendToServer(connectMessage); // send connect info to server
			
		} catch (UnknownHostException e) {
			clientGUI.showMessage("Unable to connect server\n" + e.getMessage());
			System.exit(0);		
		} catch (IOException e) {
			clientGUI.showMessage("Unable to connect server\n" + e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
	}
	
	public void makeRequest(Message message) {
		
		this.rivalName = message.getRivalName();
		// get rival info (addr and port)
		sendToServer(message);	
	}
	
	public void disconnectFromServer() { // disconnect procedure

		sendToServer(new Message(Message.CLIENT_DISCONNECT, userName));	
	}
		
	public void sendToServer(Message message) {		
		try {
			oos.writeObject(message);
			oos.flush();		
		} catch (IOException e) {		
			clientGUI.showMessage("IO Error:\n" + e.getMessage());
		}
	}
	
	public static void main(final String[] args) {
		
		Client client = null;
		try {
			client = new Client(args[0], Integer.parseInt(args[1]) , args[2]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("missing parameter!");
		}
		client.start();
		client.startClient();
	}

}
