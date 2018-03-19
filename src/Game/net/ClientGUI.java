package Game.net;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class ClientGUI {
	
	private Client client;
	private String userName;
	private String rivalName;
	
	public JFrame clientFrame;
	private JButton btnStart, btnDisconnect;
	private JPanel usersPanel, controlPanel;
	private JScrollPane scrPanel;
	private JList userList;
	
	public ClientGUI(String userName) {
		CreateGUI();
	}
	
	public ClientGUI(Client client, String userName) {
		this.userName = userName;
		this.client = client;
		CreateGUI();
	}
	
	public void showMessage(String message) {
		JOptionPane.showMessageDialog(clientFrame, message);
	}
	
	public void updateUserList(Vector<String> userlist) {
		userList.removeAll();
		userlist.removeElement(userName);
		userList.setListData(userlist);
		btnStart.setEnabled(false);
	}


	private void CreateGUI() {
		
		clientFrame = new JFrame("Dama - " + userName);
		clientFrame.setSize(300, 400);
		clientFrame.setResizable(false);
		clientFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		clientFrame.setLocation(100, 100);
		clientFrame.setVisible(true);
		
		
		
		btnStart = new JButton("Start Match");
		btnStart.setEnabled(false);
		btnDisconnect = new JButton("Disconnect");
		
		userList = new JList();
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
		scrPanel = new JScrollPane(userList);
		scrPanel.setPreferredSize(new Dimension(150,300));
		
		/* Users Panel *********************/
		usersPanel = new JPanel();
		usersPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		usersPanel.setSize(200, 350);
		usersPanel.setBorder(new TitledBorder("Users:"));
		usersPanel.add(scrPanel);
		
		/* Control Panel *********************/
		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		controlPanel.setSize(100, 370);
		controlPanel.add(btnStart);
		controlPanel.add(btnDisconnect);
		
		clientFrame.getContentPane().setLayout(null);
		clientFrame.getContentPane().add(usersPanel);
		clientFrame.getContentPane().add(controlPanel);
		
		usersPanel.setBounds(10, 10, 170, 350);		
		controlPanel.setBounds(180, 20, 120, 300);
		
		/******************* Actions ********************/
		btnStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				Message wantMatch = new Message(Message.REQUEST_MATCH, userName); // Firstly send a request
				rivalName = userList.getSelectedValue().toString();
				wantMatch.setRivalName(rivalName);
				client.makeRequest(wantMatch);
				//Client.sendToServer(wantMatch);
			}				
		});
		
		btnDisconnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				client.disconnectFromServer();
			}
		});
		
		userList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				btnStart.setEnabled(true);
			}
		});
		
		clientFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				
			    //Client.disconnectFromServer();
			  }
		});
				
	}
	
	

}
