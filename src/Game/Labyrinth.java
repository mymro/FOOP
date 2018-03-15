package Game;

import java.util.*;

public class Labyrinth{
    public enum Direction {
        up,
        down,
        left,
        right
    }

    public enum NodeType{
        finish,
        normal,
        unknwon,
    }

    public static class LabyrinthNode {
        private NodeType type;
        private int x,y;
        private HashMap<Direction, LabyrinthNode> neighbours = new HashMap<>();

        LabyrinthNode(int x, int y){
            this(x, y, NodeType.normal);
        }

        LabyrinthNode(int x, int y, NodeType type){
            this.type = type;
            this.x = x;
            this.y = y;
        }

        NodeType getType(){
            return type;
        }

        public void setType(NodeType type){
            this.type = type;
        }

        int getX(){
            return x;
        }

        int getY(){
            return y;
        }

        private HashMap<Direction, LabyrinthNode> getNeighbours(){
            return neighbours;
        }

        public LabyrinthNode getNeighbourAt(Direction dir){
            return neighbours.get(dir);
        }

        public Set<Direction> getEdges(){
            return new HashSet<>(neighbours.keySet());
        }
    }

    private int dim_x, dim_y;
    private LabyrinthNode[][] labyrinth;

    Labyrinth(int dim_x, int dim_y){
        if(dim_x < 1 || dim_y <1){
            throw new IllegalArgumentException("Dimensions have to be greater 0");
        }
        this.dim_x = dim_x;
        this.dim_y = dim_y;
        labyrinth = new LabyrinthNode[dim_x][dim_y];
    }

    public void createLabyrinth(){
        createLabyrinth(new Random());
    }

    public void createLabyrinth(long seed){
        Random random = new Random(seed);
        createLabyrinth(random);
    }

    public void createLabyrinth(Random random){
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

        ArrayList<LabyrinthNode> frontier = new ArrayList<LabyrinthNode>();
        boolean[][] frontierMatrix = new boolean[dim_x][dim_y];
        LabyrinthNode current = new LabyrinthNode(i, j, NodeType.finish);
        labyrinth[current.x][current.y] = current;
        int rand = 0;

        do{
            if(current.y-1 >= 0 && labyrinth[current.x][current.y-1] == null && frontierMatrix[current.x][current.y-1] != true){
                frontier.add(new LabyrinthNode(current.x, current.y-1));
                frontierMatrix[current.x][current.y-1] = true;
            }
            if(current.y+1 < dim_y && labyrinth[current.x][current.y+1] == null && frontierMatrix[current.x][current.y+1] != true){
                frontier.add(new LabyrinthNode(current.x, current.y+1));
                frontierMatrix[current.x][current.y+1] = true;
            }
            if(current.x-1 >= 0 && labyrinth[current.x-1][current.y] == null && frontierMatrix[current.x-1][current.y] != true){
                frontier.add(new LabyrinthNode(current.x-1, current.y));
                frontierMatrix[current.x-1][current.y] = true;
            }
            if(current.x+1 < dim_x && labyrinth[current.x+1][current.y] == null && frontierMatrix[current.x+1][current.y] != true){
                frontier.add(new LabyrinthNode(current.x+1, current.y));
                frontierMatrix[current.x+1][current.y] = true;
            }

            rand = random.nextInt(frontier.size());
            current = frontier.get(rand);
            labyrinth[current.x][current.y] = current;
            frontier.remove(rand);

            ArrayList<LabyrinthNode> possibleConnections = new ArrayList<>();

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
            LabyrinthNode connect = possibleConnections.get(rand);

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

    public ArrayList<LabyrinthNode> findPath(LabyrinthNode from, LabyrinthNode to){
        LinkedList<LabyrinthNode> openSet = new LinkedList<>();
        ArrayList<LabyrinthNode> closedSet = new ArrayList<>();
        HashMap<LabyrinthNode, Integer> g = new HashMap<>();
        HashMap<LabyrinthNode, Integer> f = new HashMap<>();
        HashMap<LabyrinthNode, LabyrinthNode> cameFrom = new HashMap<>();
        g.put(from, 0);
        if(to == null){
            f.put(from, 0);
        }else{
            f.put(from, Math.abs(from.x-to.x)+Math.abs(from.y-to.y));
        }
        openSet.addFirst(from);
        LabyrinthNode current = from;
        while (openSet.size()>0) {
            current = openSet.getFirst();
            if (current == to) {
                break;
            }else if(current.getType() == NodeType.unknwon){
                break;
            }
            openSet.remove(0);
            closedSet.add(current);
            for (LabyrinthNode neighbour : current.neighbours.values()) {
                if (closedSet.indexOf(neighbour) != -1) {
                    continue;
                }

                int tentative_g = g.get(current) + 1;
                int tentative_f = 0;
                if(to != null){
                    tentative_f = tentative_g + Math.abs(neighbour.x - to.x) + Math.abs(neighbour.y - to.y);
                }

                int index = openSet.indexOf(neighbour);
                if (index == -1) {
                    ListIterator<LabyrinthNode> it = openSet.listIterator();
                    boolean set = false;
                    while (it.hasNext()) {
                        int i = it.nextIndex();
                        LabyrinthNode node = it.next();
                        if (f.get(node) > tentative_f) {
                            openSet.add(i, neighbour);
                            set = true;
                            break;
                        }
                    }
                    if(!set){
                        openSet.addLast(neighbour);
                    }
                } else if (g.get(neighbour) < tentative_g) {
                    continue;
                }
                g.put(neighbour, tentative_g);
                f.put(neighbour, tentative_f);
                cameFrom.put(neighbour, current);
            }
        }
        ArrayList<LabyrinthNode> path = new ArrayList<>();
        int i = 0;
        if((cameFrom.get(current) != null || from == to) && openSet.size()>0){
            path.add(current);
            while (cameFrom.get(path.get(i)) != null){
                i = i+1;
                path.add(cameFrom.get(path.get(i-1)));
            }
        }
        return path;
    }

    public LabyrinthNode getNodeAt(int x, int y) throws ArrayIndexOutOfBoundsException{
        return labyrinth[x][y];
    }

    public LabyrinthNode setNodeAt(int x, int y, NodeType type)throws  ArrayIndexOutOfBoundsException{
        try {
            return setNodeAt(x,y,type, new HashSet<>());
        }catch (ArrayIndexOutOfBoundsException e){
            throw e;
        }
    }

    public LabyrinthNode changeNode(LabyrinthNode node, NodeType type, Set<Direction> new_connections ) throws ArrayIndexOutOfBoundsException{
        try{
            return setNodeAt(node.getX(), node.getY(), type, new_connections);
        }catch (ArrayIndexOutOfBoundsException e){
            throw e;
        }
    }

    public LabyrinthNode setNodeAt(int x, int y, NodeType type, Set<Direction> new_connections) throws  ArrayIndexOutOfBoundsException{

        LabyrinthNode node = labyrinth[x][y];
        new_connections = new HashSet<>(new_connections);

        if(node != null && new_connections.size()>0) {
            Set<Direction> dirs = node.neighbours.keySet();
            for (Direction dir : dirs) {
                if(!new_connections.contains(dir)){
                    LabyrinthNode neighbour = node.neighbours.get(dir);
                    switch (dir) {
                        case up:
                            neighbour.neighbours.remove(Direction.down);
                            node.neighbours.remove(Direction.up);
                            break;
                        case down:
                            neighbour.neighbours.remove(Direction.up);
                            node.neighbours.remove(Direction.down);
                            break;
                        case left:
                            neighbour.neighbours.remove(Direction.right);
                            node.neighbours.remove(Direction.left);
                            break;
                        case right:
                            neighbour.neighbours.remove(Direction.left);
                            node.neighbours.remove(Direction.right);
                            break;
                    }
                } else {
                    new_connections.remove(dir);
                }
            }
        }else{
                node = new LabyrinthNode(x,y);
                labyrinth[x][y] = node;
        }

        node.setType(type);

        for (Direction dir : new_connections){
            int neighbour_x = x;
            int neighbour_y = y;
            Direction neighbour_dir = null;

            switch (dir){
                case up:
                    neighbour_x = x;
                    neighbour_y = y-1;
                    neighbour_dir = Direction.down;
                    break;
                case down:
                    neighbour_x = x;
                    neighbour_y = y+1;
                    neighbour_dir = Direction.up;
                    break;
                case left:
                    neighbour_x = x-1;
                    neighbour_y = y;
                    neighbour_dir = Direction.right;
                    break;
                case right:
                    neighbour_x = x+1;
                    neighbour_y = y;
                    neighbour_dir = Direction.left;
                    break;
            }

            if (neighbour_x < dim_x && neighbour_y < dim_y && neighbour_x >= 0 && neighbour_y >= 0 ){
                LabyrinthNode neighbour = labyrinth[neighbour_x][neighbour_y];
                if(neighbour == null){
                    neighbour = new LabyrinthNode(neighbour_x, neighbour_y, NodeType.unknwon);
                    labyrinth[neighbour_x][neighbour_y] = neighbour;
                }

                neighbour.neighbours.put(neighbour_dir, node);
                node.neighbours.put(dir, neighbour);
            }
        }

        return node;
    }

    public int getDim_x(){
        return dim_x;
    }

    public int getDim_y(){
        return dim_y;
    }
}
