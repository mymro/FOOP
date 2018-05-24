package game.net;

import game.game.objects.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientThread extends Thread {
	
	private Client client;
	private Socket socket;
	private int port;
	private String socketIP;
	private PrintWriter writer;
	String [] params;
	
	private Player player;

	
	ObjectInputStream ois;
	ObjectOutputStream oos;
	private ClientGUI clientGui;
	
	public ClientThread(Client client, Message message) { // first constructer calling by match requester
		this.client = client;
		this.player = message.getPlayer();
		socketIP = client.getServerIP();
		port = client.getPort();
		clientGui = new ClientGUI();
		
		try {
			this.socket = new Socket(InetAddress.getByName(socketIP), port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		start();

	}
	
	public ClientThread(Client client, Socket socket, Player player) {
		this.socket = socket;
		this.client = client;
		start();
	}

	@Override
	public void run() {
		try {			
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());
			
		} catch (IOException e) {
			System.out.println(this.player.getName() +" ClientThreadstreams: " + e.getMessage());
			e.printStackTrace();
		}

		try {
			while (true) {
				Message message = (Message) ois.readObject();
				if(message.getType() == Message.START_MATCH) {
					clientGui.updateGame();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
				ois.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}



	public PrintWriter getWriter() {
		return writer;
	}

	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}

	public void sendToRival(Message message) {	
		try {
			oos.writeObject(message);
			oos.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	



}
