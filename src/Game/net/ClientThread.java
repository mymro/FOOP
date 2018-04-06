package Game.net;

import Game.Core.Labyrinth;
import Game.GameObjects.Player;
import Game.Main;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.SwingUtilities;

public class ClientThread extends Thread {
	
	private Client client;
	private Socket socket;
	private int port;
	private String socketIP;
	private PrintWriter writer;
	String [] params;
	
	private Player player;

	/**
	 *
	 * TODO
	 *
	 */
	private Main game;
	
	ObjectInputStream ois;
	ObjectOutputStream oos;
	
	public ClientThread(Client client, Message message) { // first constructer calling by match requester
		this.client = client;
		this.player = message.getPlayer();
		params = message.getMessage().split(":");
		
		socketIP = params[0];
		port = Integer.parseInt(params[1]);
		
		try {
			this.socket = new Socket(InetAddress.getByName(socketIP), port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/**TODO
		SwingUtilities.invokeLater(new Runnable() {	
			@Override
			public void run() {
				game = new GameGUI(ClientThread.this, userName, rivalName, GameGUI.BLUE_PIECE);			
			}
		}); **/
		start();

	}
	
	public ClientThread(Client client, Socket socket, Player player) {
		this.socket = socket;
		this.client = client;


		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				game = new Main(player) ;
				try {
					game.start(new Stage());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

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
				if(message.getType() == Message.MOVE) {
					// TODO game.move(message.getFrom(), message.getTo());
				}
				else if(message.getType() == Message.TAKE) {
					//TODO	game.takePiece(message.getTaken());
					/** TODO
					writer.println(message.getUserName() + " - " + "move " + game.indexToPos(message.getFrom()) + " to "
							+ game.indexToPos(message.getTo()) + ", piece at " + game.indexToPos(message.getTaken()) + 
									" is taken");
					game.move(message.getFrom(), message.getTo());
					 **/
				}
				else if(message.getType() == Message.KING) {
					/** TODO
					if(message.getTo() == 1)

						game.makeKingAt(message.getFrom(), GameGUI.BLUE_KING);
					else
						game.makeKingAt(message.getFrom(), GameGUI.RED_KING);
					 **/
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
