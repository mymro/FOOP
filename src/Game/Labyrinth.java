package Game;

import java.util.*;

public class Labyrinth {
    public enum Direction {
        up,
        down,
        left,
        right
    }

    public enum NodeType{
        finish,
        normal
    }

    public class Node{
        private NodeType type;
        private int x,y;
        private HashMap<Direction, Node> neighbours = new HashMap<>();

        Node(int x, int y){
            this(x, y, NodeType.normal);
        }

        Node(int x, int y, NodeType type){
            this.type = type;
            this.x = x;
            this.y = y;
        }

        NodeType getType(){
            return type;
        }

        int getX(){
            return x;
        }

        int getY(){
            return y;
        }

        private HashMap<Direction, Node> getNeighbours(){
            return neighbours;
        }

        public Node getNeighbourAt(Direction dir){
            return neighbours.get(dir);
        }
    }

    private int dim_x, dim_y;
    private Node[][] labyrinth;

    Labyrinth(int dim_x, int dim_y) throws IllegalArgumentException{
        if(dim_x < 1 || dim_y <1){
            throw new IllegalArgumentException("Dimensions have to be greater 0");
        }
        this.dim_x = dim_x;
        this.dim_y = dim_y;
        labyrinth = new Node[dim_x][dim_y];
    }

    public void createLabyrinth(){

        Random random = new Random();
        int i = 0;
        int j = 0;

        switch (random.nextInt(4)){
            case 0:
                i = 0;
                j = random.nextInt(dim_y);
                break;
            case 1:
                i = dim_x-1;
                j = random.nextInt(dim_y);
                break;
            case 2:
                i = random.nextInt(dim_x);
                j = 0;
                break;
            case 3:
                i = random.nextInt(dim_x);
                j = dim_y-1;
        }

        ArrayList<Node> frontier = new ArrayList<Node>();
        boolean[][] frontierMatrix = new boolean[dim_x][dim_y];
        Node current = new Node(i, j, NodeType.finish);
        labyrinth[current.x][current.y] = current;
        int rand = 0;

        do{
            if(current.y-1 >= 0 && labyrinth[current.x][current.y-1] == null && frontierMatrix[current.x][current.y-1] != true){
                frontier.add(new Node(current.x, current.y-1));
                frontierMatrix[current.x][current.y-1] = true;
            }
            if(current.y+1 < dim_y && labyrinth[current.x][current.y+1] == null && frontierMatrix[current.x][current.y+1] != true){
                frontier.add(new Node(current.x, current.y+1));
                frontierMatrix[current.x][current.y+1] = true;
            }
            if(current.x-1 >= 0 && labyrinth[current.x-1][current.y] == null && frontierMatrix[current.x-1][current.y] != true){
                frontier.add(new Node(current.x-1, current.y));
                frontierMatrix[current.x-1][current.y] = true;
            }
            if(current.x+1 < dim_x && labyrinth[current.x+1][current.y] == null && frontierMatrix[current.x+1][current.y] != true){
                frontier.add(new Node(current.x+1, current.y));
                frontierMatrix[current.x+1][current.y] = true;
            }

            rand = random.nextInt(frontier.size());
            current = frontier.get(rand);
            labyrinth[current.x][current.y] = current;
            frontier.remove(rand);

            ArrayList<Node> possibleConnections = new ArrayList<>();

            if(current.y-1 >= 0 && labyrinth[current.x][current.y-1] != null){
                possibleConnections.add(labyrinth[current.x][current.y-1]);
            }
            if(current.y+1 < dim_y && labyrinth[current.x][current.y+1] != null){
                possibleConnections.add(labyrinth[current.x][current.y+1]);
            }
            if(current.x-1 >= 0 && labyrinth[current.x-1][current.y] != null){
                possibleConnections.add(labyrinth[current.x-1][current.y]);
            }
            if(current.x+1 < dim_x && labyrinth[current.x+1][current.y] != null){
                possibleConnections.add(labyrinth[current.x+1][current.y]);
            }

            rand = random.nextInt(possibleConnections.size());
            Node connect = possibleConnections.get(rand);

            if(connect.x == current.x){
                if(connect.y > current.y){
                    current.neighbours.put(Direction.down, connect);
                    connect.neighbours.put(Direction.up, current);
                }else {
                    current.neighbours.put(Direction.up, connect);
                    connect.neighbours.put(Direction.down, current);
                }

            }else if(connect.x > current.x){
                current.neighbours.put(Direction.right, connect);
                connect.neighbours.put(Direction.left, current);
            }else {
                current.neighbours.put(Direction.left, connect);
                connect.neighbours.put(Direction.right, current);
            }

        }while(frontier.size()>0);
    }

    public ArrayList<Node> findPath(Node from, Node to){
        LinkedList<Node> openSet = new LinkedList<>();
        ArrayList<Node> closedSet = new ArrayList<>();
        HashMap<Node, Integer> g = new HashMap<>();
        HashMap<Node, Integer> f = new HashMap<>();
        HashMap<Node, Node> cameFrom = new HashMap<>();
        g.put(from, 0);
        f.put(from, Math.abs(from.x-to.x)+Math.abs(from.y-to.y));
        openSet.addFirst(from);
        Node current = from;
        while (openSet.size()>0) {
            current = openSet.getFirst();
            if (current == to) {
                break;
            }
            openSet.remove(0);
            closedSet.add(current);
            for (Node neighbour : current.neighbours.values()) {
                if (closedSet.indexOf(neighbour) != -1) {
                    continue;
                }

                int tentativeg = g.get(current) + 1;
                int tentativef = tentativeg + Math.abs(neighbour.x - to.x) + Math.abs(neighbour.y - to.y);

                int index = openSet.indexOf(neighbour);
                if (index == -1) {
                    ListIterator<Node> it = openSet.listIterator();
                    boolean set = false;
                    while (it.hasNext()) {
                        int i = it.nextIndex();
                        Node node = it.next();
                        if (f.get(node) > tentativef) {
                            openSet.add(i, neighbour);
                            set = true;
                            break;
                        }
                    }
                    if(!set){
                        openSet.addLast(neighbour);
                    }
                } else if (g.get(neighbour) < tentativeg) {
                    continue;
                }
                g.put(neighbour, tentativeg);
                f.put(neighbour, tentativef);
                cameFrom.put(neighbour, current);
            }
        }
        ArrayList<Node> path = new ArrayList<>();
        int i = 0;
        if(cameFrom.get(current) != null || from == to){
            path.add(current);
            while (cameFrom.get(path.get(i)) != null){
                i = i+1;
                path.add(cameFrom.get(path.get(i-1)));
            }
        }
        return path;
    }

    public Node getNodeAt(int x, int y) throws ArrayIndexOutOfBoundsException{
        return labyrinth[x][y];
    }

    public int getDim_x(){
        return dim_x;
    }

    public int getDim_y(){
        return dim_y;
    }
}
