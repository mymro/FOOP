package game.net;

import game.core.Flag;
import game.core.Vector_2;

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
    public static final int ADD_FLAG            = 8;
    public static final int ADD_PLAYER          = 9;
    public static final int UPDATE              = 10;



    private static final long serialVersionUID = 1L;

    private int type;
    private Flag.flag_type flag_type;
    private String message;
    private long seed;
    private String color;
    private double pos_x;
    private double pos_y;
    private int object_key;
    private Vector_2 vect;
    private String name;
    private int[] new_positions_keys;
    private double[] new_positions_x;
    private  double[] new_positions_y;

    public Message() {
    }

    public Message(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSeed(long seed){
        this.seed = seed;
    }

    public long getSeed(){
        return seed;
    }

    public void setColor(String color){
        this.color = color;
    }

    public String getColor(){
        return color;
    }

    public void setPosX(double x){
        pos_x = x;
    }

    public double getPosX(){
        return pos_x;
    }

    public void setPosY(double y){
        pos_y = y;
    }

    public double getPosY(){
        return pos_y;
    }

    public void setObjectKey(int key){
        object_key = key;
    }

    public int getObjectKey(){
        return object_key;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setNewPositions(int[] keys, double[] x, double[] y){
        new_positions_keys = keys;
        new_positions_x = x;
        new_positions_y = y;
    }

    public int[] getKeys(){
        return new_positions_keys;
    }

    public double[] getNew_positions_x(){
        return new_positions_x;
    }

    public double[] getNew_positions_y() {
        return new_positions_y;
    }

    public void setFlagType(Flag.flag_type type){
        flag_type = type;
    }

    public Flag.flag_type getFlagType(){
        return flag_type;
    }

    public void setVector2(Vector_2 vect){
        this.vect = vect;
    }

    public Vector_2 getVector2() {
        return vect;
    }
}
