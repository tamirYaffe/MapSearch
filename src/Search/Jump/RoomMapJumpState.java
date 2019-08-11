package Search.Jump;


import Search.*;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;

import java.util.*;

public class RoomMapJumpState implements IProblemState {
    private RoomMap roomMap; //the room map
    private Position position;  //current position
    private HashSet<Position> seen;       //seen positions
    private RoomMapJumpStep lastStep;  //last step
    private double cost = 0;
    protected double h = -1;
    protected TreeMap<Position, RoomMapJumpStep> nextPoints;       //neighbor positions in the MST


    public RoomMapJumpState(RoomMap roomMap, Position position, HashSet<Position> seen, RoomMapJumpStep lastStep) {
        this.roomMap = roomMap;
        this.position = position;
        this.seen = seen;
        this.lastStep = lastStep;
        RoomMapJumpGraphAdapter g = new RoomMapJumpGraphAdapter(roomMap.getWatchedDictionary(), roomMap.getVisualLineDictionary(), this);
//        RoomMapJumpGraphAdapter g = new RoomMapJumpGraphAdapter(roomMap.getWatchedDictionary(), roomMap.getVisualLineDictionary(), this, 0.0, 1000);
        updateNeighbors(g.getGraph());
    }

    public RoomMapJumpState(RoomMap roomMap, Position position, HashSet<Position> seen, RoomMapJumpStep lastStep, double cost) {
        this.roomMap = roomMap;
        this.position = position;
        this.seen = seen;
        this.lastStep = lastStep;
        this.cost = cost + getStateLastMoveCost();
        RoomMapJumpGraphAdapter g = new RoomMapJumpGraphAdapter(roomMap.getWatchedDictionary(), roomMap.getVisualLineDictionary(), this);
//        RoomMapJumpGraphAdapter g = new RoomMapJumpGraphAdapter(roomMap.getWatchedDictionary(), roomMap.getVisualLineDictionary(), this, 0.0, 1000);
        updateNeighbors(g.getGraph());
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

        if (nextPoints != null) {
            for (Position p : nextPoints.keySet()) {
                room[p.getY()][p.getX()] = "@@@"; //mark as seen on 'room'
            }
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
        return new ArrayList<>(nextPoints.values());
//        List<RoomMapJumpStep> moveList = new ArrayList<>();
//        HashSet<RoomMapJumpStep> removeMovesList = new HashSet<>();
//        HashSet<Position> removePositionSet = new HashSet<>();

//        for (Position neighbor : nextPoints.keySet()) {
//            RoomMapJumpStep step = new RoomMapJumpStep(position, neighbor);
//            removePositionSet.addAll(step.path.subList(1, step.path.size() - 1));
//            moveList.add(step);
//        }
//        for (RoomMapJumpStep move : moveList) {
//            if (removePositionSet.contains(move.target)) {
//                removeMovesList.add(move);
//                nextPoints.remove(move.target);
//            }
//        }
//        moveList.removeAll(removeMovesList);
//        return moveList;
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
        for (int i = 0; i < roomMapJumpStep.path.size(); i++) {
            newSeen.addAll(roomMap.getVisualNeighbors(roomMapJumpStep.path.get(i)));
        }
        // Create new state
        return new RoomMapJumpState(newProblem, roomMapJumpStep.target, newSeen, roomMapJumpStep, cost);
    }

    private boolean outOfBoundaries(int y, int x) {
        int[][] room = roomMap.getRoomMap();
        int height = room.length;
        int width = room[0].length;
        return x < 0 || y < 0 || y >= height || x >= width || room[y][x] != 0;
    }

    public Position getPosition() {
        return position;
    }

    public HashSet<Position> getSeen() {
        return seen;
    }

    private void updateNeighbors(Graph<PositionVertex, UndirectedWeightedEdge> g) {
        HashMap<Position, RoomMapJumpStep> tmpNext = new HashMap<>();
        HashSet<Position> positionsToRemove = new HashSet<>();
//        for (UndirectedWeightedEdge edge : g.outgoingEdgesOf(new PositionVertex(position, PositionVertex.TYPE.PRUNEABLE))) {
        PositionVertex positionVertex = new PositionVertex(position, PositionVertex.TYPE.UNPRUNNABLE);
        for (UndirectedWeightedEdge edge : g.outgoingEdgesOf(positionVertex)) {
//            Position s = edge.getSource().getPosition();
            Position t = Graphs.getOppositeVertex(g, edge, positionVertex).getPosition();
            RoomMapJumpStep step = new RoomMapJumpStep(position, t);
            tmpNext.putIfAbsent(t, step);
            positionsToRemove.addAll(step.path.subList(1, step.path.size() - 1));

//            tmpNext.putIfAbsent(t, new RoomMapJumpStep(position,t));
//            if (!s.equals(position)) {
//                tmpNext.put(s, new double[]{edge.getWeight()});
//            } else if (!t.equals(position)) {
//                tmpNext.put(t, new double[]{edge.getWeight()});
//            }
        }
        tmpNext.remove(position);
        for (Position positionToRemove : positionsToRemove) {
            tmpNext.remove(positionToRemove);
        }
        TreeMap<Position, HashSet<Position>> watchedDictionary = roomMap.getWatchedDictionary();
        nextPoints = new TreeMap<>(new Comparator<Position>() {
            @Override
            public int compare(Position o1, Position o2) {
                if (o1.equals(o2)) return 0;
                if (tmpNext.getOrDefault(o1, new RoomMapJumpStep(position, position)).getWeight() > tmpNext.getOrDefault(o2, new RoomMapJumpStep(position, position)).getWeight())
                    return 1;
                else return -1;
            }
        });
        nextPoints.putAll(tmpNext);
    }

    public double getH() {
        return h;
    }


}
