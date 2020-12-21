package Search;

import java.util.*;

public class AStar {
    public static int expanded;
    public static int generated;
    public static int duplicates;
    public static double rootH = 0.0;
    private int[][] map;
    private PriorityQueue<Node> openQueue;
    private HashSet<Node> closedList;
    private HashMap<Node, Double> openList;
    private static class Node implements Comparable<Node>{
        double g;
        double h;
        double f;
        Position position;
        Node parent;

        public Node(Position position, Node parent, Position goal) {
            this.position = position;
            this.parent = parent;
            int dx = Math.abs(parent.position.getX() - position.getX());
            int dy = Math.abs(parent.position.getY() - position.getY());
            if(dx + dy == 2)
                this.g = parent.g + Math.sqrt(2);
            else
                this.g = parent.g + 1;
            this.h = Math.abs(goal.getY() - this.position.getY()) +
                    Math.abs(goal.getX() - this.position.getX());
            this.f = g + h;
        }

        public Node(Position position, Position goal) {
            this.position = position;
            this.g = 0;
            // manhattan distance for 4-way movement
            this.h = Math.abs(goal.getY() - this.position.getY()) + Math.abs(goal.getX() - this.position.getX());
            // euclidean distance for 8-way movement
//            this.h = Math.sqrt(Math.pow(goal.getY() - this.position.getY(),2) + Math.pow(goal.getX() - this.position.getX(), 2));
            this.f = g + h;
        }

        public Node(Position position){
            this.position = position;
        }

        public List<Node> getNeighbors(int[][]map, Position goal){
            List<Node> neighbors = new ArrayList<>();
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if(i == 0 && j == 0)
                        continue;
                    // 4-way movement constraint.
//                    if(i != 0 && j != 0)
//                        continue;
                    Position neighborPosition = new Position(this.position.getY()+j, this.position.getX()+i);
                    // check legal move
                    if(neighborPosition.getX() < 0 || neighborPosition.getX() >= map[0].length ||
                        neighborPosition.getY() < 0 || neighborPosition.getY() >= map.length ||
                            map[neighborPosition.getY()][neighborPosition.getX()] != 0)
                        continue;
                    Node neighbor = new Node(neighborPosition, this, goal);
                    neighbors.add(neighbor);
                }
            }
            return neighbors;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (obj instanceof Node) {
                Node other = (Node) obj;
                return this.position.equals(other.position);
            }
            return false;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.position.getX();
            result = prime * result + this.position.getY();
            return result;
        }

        @Override
        public int compareTo(Node other) {
            if(this.f > other.f) {
                return 1;
            } else if (this.f < other.f) {
                return -1;
            }else return Double.compare(other.g, this.g);
        }
    }

    public AStar(int[][] map) {
        closedList=new HashSet<>();
        openList =new HashMap<>();
        openQueue = new PriorityQueue<>();
        this.map = map;
    }

    public PathFindingPath solve(Position start, Position goal){
        double startTime = System.currentTimeMillis();
        expanded = 0;
        generated = 0;
        duplicates = 0;
        PathFindingPath solution = null;
        Node current;

        // create start and goal nodes
        Node startNode = new Node(start, goal);
        Node goalNode = new Node(goal);

        // add start node to open.
        openQueue.add(startNode);
        openList.put(startNode, startNode.g);

        rootH = startNode.h;
//        System.out.println("Root.H: " + rootH);

        while (!openQueue.isEmpty()) {
            current = openQueue.poll();
            //check current is in open list
            if(!openList.containsKey(current))
                continue;
            openList.remove(current);

            // goal check
            if (goalNode.equals(current)) {
//                System.out.println("\rexpanded: " + expanded + "\tgenerated: " + generated + "\tduplicates: " + duplicates + "\t\tg: " + current.g + "\t\th: " + current.h + "\t\tf: " + (current.f) + "\t\tTime: " + (System.currentTimeMillis() - startTime) + "ms");
//                System.out.println("calculated Average PerformMove Time: " + TestTime.calculateAveragePerformMoveTime() + " calculated Average Graph Creation Time: " + TestTime.calculateAverageGraphCreationTime() + " calculated Average H Time: " + TestTime.calculateAverageHTime());
                solution = getSolution(current);
                break;
            }
            List<Node> neighbors = current.getNeighbors(map, goal);
            for (Node  neighbor: neighbors) {
                if (closedList.contains(neighbor))
                    continue;
                if (!openList.containsKey(neighbor)) {
                    openQueue.add(neighbor);
                    openList.put(neighbor, neighbor.g);
                    generated++;
                } else {
                    duplicates++;
                    if (openList.get(neighbor) > neighbor.g) {
                        openList.replace(neighbor, neighbor.g);
                        openQueue.add(neighbor);
                        generated++;
                    }
                }
            }
            closedList.add(current);
            expanded++;
//            System.out.print("\rexpanded: " + expanded + "\tgenerated: " + generated + "\tduplicates: " + duplicates + "\t\tg: " + current.g + "\t\th: " + current.h + "\t\tf: " + (current.f) + "\t\tTime: " + (System.currentTimeMillis() - startTime) + "ms");
        }
        // empty lists
        closedList.clear();
        openList.clear();
        openQueue.clear();
        return solution;
    }

    private PathFindingPath getSolution(Node endNode) {
        if (endNode == null)
            return null;
        double solutionCost = endNode.g;
        Node current = endNode;
        List<Position> solutionPath = new ArrayList<>();
        while (current.parent != null) {
            solutionPath.add(current.position);
            current = current.parent;
        }
        solutionPath.add(current.position);
        Collections.reverse(solutionPath);
        return new PathFindingPath(solutionPath, solutionCost);
    }
}

