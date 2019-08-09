package Search.Jump;


import Search.*;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.*;

public class RoomMapJumpState implements IProblemState {
    private RoomMap roomMap; //the room map
    private Position position;  //current position
    private HashSet<Position> seen;       //seen positions
    private RoomMapJumpStep lastStep;  //last step
    private double cost = 0;
    protected double h = 0;
    protected TreeMap<Position, double[]> nextPoints;       //neighbor positions in the MST


    public RoomMapJumpState(RoomMap roomMap, Position position, HashSet<Position> seen, RoomMapJumpStep lastStep) {
        this.roomMap = roomMap;
        this.position = position;
        this.seen = seen;
        this.lastStep = lastStep;
        new RoomMapJumpMSTHeuristic().getHeuristic(this);
    }

    public RoomMapJumpState(RoomMap roomMap, Position position, HashSet<Position> seen, RoomMapJumpStep lastStep, double cost) {
        this.roomMap = roomMap;
        this.position = position;
        this.seen = seen;
        this.lastStep = lastStep;
        this.cost = cost + getStateLastMoveCost();
        new RoomMapJumpMSTHeuristic().getHeuristic(this);
    }

    public RoomMapJumpState(RoomMap roomMap, Position vertexPosition) {
        this.roomMap = roomMap;
        this.position = vertexPosition;
    }

    @Override
    public List<IProblemState> getNeighborStates() {
        List<IProblemState> neighborStates = new ArrayList<>();
        List<RoomMapJumpStep> legalMoves = getLegalMoves();
        for (RoomMapJumpStep move : legalMoves) {
            IProblemState newState = performMove(move);
            if (newState != null)
                neighborStates.add(newState);
        }
        return neighborStates;
    }

    @Override
    public String toString() {
        // Make a string of the map
        String[][] room = new String[roomMap.getRoomMap().length][roomMap.getRoomMap()[0].length];

        //initialize the String map ('room')
        for (int i = 0; i < room.length; i++) {
            for (int j = 0; j < room[0].length; j++) {
                room[i][j] = "   ";     //blank slot
                if (roomMap.getRoomMap()[i][j] == 1) room[i][j] = "###";    //obstacle
            }
        }

        //for each position that was seen on the way to this state
        for (Position p : seen) {
            room[p.getY()][p.getX()] = "///"; //mark as seen on 'room'
        }
        room[position.getY()][position.getX()] = "$$$";

        //agent's current position
        String string = "position: " + position.getX() + "," + position.getY() + "\n";

        //add to 'string' the String array with the seen positions ('room')
        for (int i = 0; i < room.length; i++) {
            string += Arrays.toString(room[i]) + "\n";
        }

        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoomMapJumpState)) return false;
        RoomMapJumpState that = (RoomMapJumpState) o;
        return that.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        StringBuilder binary = new StringBuilder(40);
        StringBuilder string = new StringBuilder(100);
        string.append(Integer.toString(position.getX(), 36)).append(",").append(Integer.toString(position.getY(), 36)).append(",");
        int i = 0;
        for (Position p : roomMap.getWatchedDictionary().keySet()) {
            if (i++ > 61) {
                addBinaryToString(binary, string);
                i = 0;
                binary.setLength(0);
            }
            binary.append(seen.contains(p) ? "1" : "0");
        }
        if (i > 0) addBinaryToString(binary, string);
//        System.out.println(string);
        return string.toString().hashCode();
    }

    private void addBinaryToString(StringBuilder binary, StringBuilder string) {
        string.append(Long.toString(Long.parseLong(binary.toString(), 2), 36));
    }

    private List<RoomMapJumpStep> getLegalMoves() {
        List<RoomMapJumpStep> moveList = new ArrayList<>();
        for (Position MSTneighbors : nextPoints.keySet()) {
            moveList.add(new RoomMapJumpStep(position, MSTneighbors));
        }
        return moveList;
    }

    @Override
    public IProblem getProblem() {
        return roomMap;
    }

    @Override
    public boolean isGoalState() {
        return seen.size() == roomMap.getNumberOfPositions();
    }

    @Override
    public IProblemMove getStateLastMove() {
        return lastStep;
    }

    @Override
    public double getStateLastMoveCost() {
        return DistanceService.getWeight(lastStep.source, lastStep.target);
    }

    @Override
    public IProblemState performMove(IProblemMove move) {
        if (!(move instanceof RoomMapJumpStep))
            return null;
        RoomMap newProblem = roomMap;
        Position newPosition = new Position(position);
        HashSet<Position> newSeen = new HashSet<>(seen);
        int x = newPosition.getX();
        int y = newPosition.getY();
        RoomMapJumpStep roomMapJumpStep = (RoomMapJumpStep) move;
        GraphPath path = DistanceService.minPath(roomMapJumpStep.source, roomMapJumpStep.target);
        for (int i = 0; i < roomMapJumpStep.path.length; i++) {
            newSeen.addAll(roomMap.getVisualNeighbors(roomMapJumpStep.path[i]));
        }
        // Create new state
        return new RoomMapJumpState(newProblem, (Position) path.getEndVertex(), newSeen, roomMapJumpStep, cost);
    }

    private boolean outOfBoundaries(int y, int x) {
        int[][] room = roomMap.getRoomMap();
        int height = room.length;
        int width = room[0].length;
        return x < 0 || y < 0 || y >= height || x >= width || room[y][x] != 0;
    }

    public Position getPosition() {
        return new Position(position);
    }

    public HashSet<Position> getSeen() {
        return seen;
    }

    public void updateMST(Graph<PositionVertex, UndirectedWeightedEdge> g, double h) {
        this.h = h;
        HashMap<Position, double[]> tmpNext = new HashMap<>();
        for (UndirectedWeightedEdge edge : g.outgoingEdgesOf(new PositionVertex(position, PositionVertex.TYPE.UNPRUNNABLE))) {
            Position s = edge.getSource().getPosition();
            Position t = edge.getTarget().getPosition();
            if (!s.equals(position)) {
                tmpNext.put(s, new double[]{edge.getWeight()});
            } else if (!t.equals(position)) {
                tmpNext.put(t, new double[]{edge.getWeight()});
            }
        }
        nextPoints = new TreeMap<>(new Comparator<Position>() {
            @Override
            public int compare(Position o1, Position o2) {
                if (o1.equals(o2)) return 0;
                if (tmpNext.getOrDefault(o1, new double[1])[0] > tmpNext.getOrDefault(o2, new double[1])[0])
                    return 1;
                else return -1;
            }
        });
        nextPoints.putAll(tmpNext);
    }
}
