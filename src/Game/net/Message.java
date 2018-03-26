package Game.net;

import Game.GameObjects.Player;

import java.util.Vector;

public class Message implements java.io.Serializable {

    //Message constants
    public static final int WELCOME				= 0;
    public static final int CLIENT_CONNECT  	= 1;
    public static final int CLIENT_DISCONNECT	= 2;
    public static final int USERS_LIST			= 3;
    public static final int BYE					= 4;
    public static final int REQUEST_MATCH		= 5;
    public static final int START_MATCH 		= 6;
    public static final int USER_INFO 			= 7;
    public static final int MOVE	 			= 8;
    public static final int TAKE 				= 9;
    public static final int KING 				= 10;

    private static final long serialVersionUID = 1L;

    private int type;
    private Player player;
    private String message;
    private int from;
    private int to;
    private int taken;

    private Vector<String> userList;

    public Message() {
    }

    public Message(int type) {
        this.type = type;
    }

    public Message(int type,Player player) {
        this.type = type;
        this.player = player;
    }

    public Message(int type, Player player, String message) {
        this.type = type;
        this.player  = player;
        this.message = message;
    }

    public Message(int type, Player player, int from, int to) {
        this.type = type;
        this.from = from;
        this.to = to;
    }

    public Message(int type, Player player, int from, int to, int take) {
        this.type = type;
        this.player = player;
        this.from = from;
        this.to = to;
        this.setTaken(take);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

	/* GET - SET Methodları */

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getTaken() {
        return taken;
    }

    public void setTaken(int taken) {
        this.taken = taken;
    }

    public Vector<String> getUserList() {
        return userList;
    }

    public void setUserList(Vector<String> userList) {
        this.userList = userList;
    }
}
