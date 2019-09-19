package Search;


import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.*;

import static Search.RoomMap.MOVEMENT_METHOD;
import static Search.RoomMap8WayStep.SQRT_OF_TWO;

public class RoomMapState implements IProblemState {
    private RoomMap roomMap; //the room map
    private Position position;  //current position
    private HashSet<Position> seen;       //seen positions
    private IProblemMove lastStep;  //last step
    private double cost = 0;
    private HashSet<Position> nextPoints;       //neighbor positions


    public RoomMapState(RoomMap roomMap, Position position, HashSet<Position> seen, IProblemMove lastStep) {
        this.roomMap = roomMap;
        this.position = position;
        this.seen = seen;
        this.lastStep = lastStep;
        if (MOVEMENT_METHOD.equals("Jump")) {
            RoomMapGraphAdapter g = new RoomMapGraphAdapter(roomMap.getWatchedDictionary(), roomMap.getVisualDictionary(), this, 1000);
            updateNeighbors(g.getGraph());
        } else updateNeighbors();
    }

    public RoomMapState(RoomMap roomMap, Position position, HashSet<Position> seen, IProblemMove lastStep, double cost) {
        this.roomMap = roomMap;
        this.position = position;
        this.seen = seen;
        this.lastStep = lastStep;
        this.cost = cost + getStateLastMoveCost();
        if (MOVEMENT_METHOD.equals("Jump")) {
            RoomMapGraphAdapter g = new RoomMapGraphAdapter(roomMap.getWatchedDictionary(), roomMap.getVisualDictionary(), this, 1000);
            updateNeighbors(g.getGraph());
        } else updateNeighbors();
    }
//
//    public RoomMapState(RoomMap roomMap, Position vertexPosition) {
//        this.roomMap = roomMap;
//        this.position = vertexPosition;
//    }

    @Override
    public List<IProblemState> getNeighborStates() {
//        if (MOVEMENT_METHOD.equals("Expanding Border")) {
//            return getExpandingBorderNeighborStates(seen,new HashSet<Position>(), new LinkedList<>() );
//        }
//        else {
//            return getSimpleNeighborStates();
//        }
//    }
//
//    private List<IProblemState> getSimpleNeighborStates() {
        List<IProblemState> neighborStates = new ArrayList<>();
        List<IProblemMove> legalMoves = getLegalMoves();
        for (IProblemMove move : legalMoves) {
            IProblemState newState = performMove(move);
            if (newState != null) {
                neighborStates.add(newState);
            }
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
            for (Position p : nextPoints) {
                room[p.getY()][p.getX()] = "@@@"; //mark as seen on 'room'
            }
        }

        room[position.getY()][position.getX()] = "$$$";

        //agent's current position
        StringBuilder string = new StringBuilder("position: " + position.getX() + "," + position.getY() + "\n");

        //add to 'string' the String array with the seen positions ('room')
        for (String[] strings : room) {
            string.append(Arrays.toString(strings)).append("\n");
        }

        return string.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoomMapState)) return false;
        RoomMapState that = (RoomMapState) o;
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
        return string.toString().hashCode();
    }

    private void addBinaryToString(StringBuilder binary, StringBuilder string) {
        string.append(Long.toString(Long.parseLong(binary.toString(), 2), 36));
    }

    private List<IProblemMove> getLegalMoves() {
        List<IProblemMove> moveList = new ArrayList<>();
        HashSet<Position> toRemove = new HashSet<>();
        for (Position newPosition : nextPoints) {
            if (outOfBoundaries(newPosition.getY(), newPosition.getX())) {
                toRemove.add(newPosition);
            }
        }
        for (Position pos : toRemove) {
            nextPoints.remove(pos);
        }
        switch (MOVEMENT_METHOD) {
            case "4-way":
                for (Position neighbor : nextPoints) {
                    RoomStep step = new RoomStep(position, neighbor);
                    moveList.add(step);
                }
                break;
            case "8-way":
                for (Position neighbor : nextPoints) {
                    RoomMap8WayStep step = new RoomMap8WayStep(position, neighbor);
                    moveList.add(step);
                }
                break;
            case "Jump":
            case "Expanding Border":
                for (Position neighbor : nextPoints) {
                    RoomMapJumpStep step = new RoomMapJumpStep(position, neighbor);
                    moveList.add(step);
                }
                break;
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
        if (lastStep != null)
            return lastStep.getCost();
        else return 0;
    }

    @Override
    public IProblemState performMove(IProblemMove move) {
        RoomMap newProblem = roomMap;
        Position newPosition = move.getNewPosition(position);
        HashSet<Position> newSeen = new HashSet<>(seen);
        if (MOVEMENT_METHOD.equals("Jump") /*|| MOVEMENT_METHOD.equals("Expanding Border")*/) {
            for (Position position : ((RoomMapJumpStep) move).getPath()) {
                newSeen.addAll(roomMap.getVisualNeighbors(position));
            }
        } else {
            // Validation
            newSeen.addAll(roomMap.getVisualNeighbors(newPosition));
        }

        // Create new state
        return new RoomMapState(newProblem, newPosition, newSeen, move, cost);
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

    private void updateNeighbors(Graph<PositionVertex, UndirectedWeightedEdge> g) {
        nextPoints = new HashSet<>();
        for (UndirectedWeightedEdge edge : g.outgoingEdgesOf(new PositionVertex(position, PositionVertex.TYPE.UNPRUNNABLE))) {
            Position s = edge.getSource().getPosition();
            Position t = edge.getTarget().getPosition();
            nextPoints.add(s);
            nextPoints.add(t);
        }
        nextPoints.remove(position);
    }

    private void updateNeighbors() {
        int height = roomMap.getRoomMap().length;
        int width = roomMap.getRoomMap()[0].length;
        nextPoints = new HashSet<>();
        int x = position.getX();
        int y = position.getY();
        if (MOVEMENT_METHOD.endsWith("way")) {
            if (y > 0)
                nextPoints.add(new Position(y - 1, x));
            if (y < height - 1)
                nextPoints.add(new Position(y + 1, x));
            if (x > 0)
                nextPoints.add(new Position(y, x - 1));
            if (x < width - 1)
                nextPoints.add(new Position(y, x + 1));
            if (MOVEMENT_METHOD.startsWith("8")) {
                if (y > 0 && x > 0)
                    nextPoints.add(new Position(y - 1, x - 1));
                if (y < height - 1 && x > 0)
                    nextPoints.add(new Position(y + 1, x - 1));
                if (y > 0 && x < width - 1)
                    nextPoints.add(new Position(y - 1, x + 1));
                if (y < height - 1 && x < width - 1)
                    nextPoints.add(new Position(y + 1, x + 1));
            }
        } else if (MOVEMENT_METHOD.equals("Expanding Border")) {
            HashSet <Position> visitedPositions = new HashSet<>();
            visitedPositions.add(position);
            LinkedList<Position> neighborQueue = new LinkedList<>();
            neighborQueue.add(new Position(y - 1, x));
            neighborQueue.add(new Position(y + 1, x));
            neighborQueue.add(new Position(y, x - 1));
            neighborQueue.add(new Position(y, x + 1));
            while (!neighborQueue.isEmpty()){
                Position nextPosition = neighborQueue.poll();
                if (visitedPositions.contains(nextPosition)) {
                    continue;
                }
                visitedPositions.add(nextPosition);
                if (outOfBoundaries(nextPosition.getY(), nextPosition.getX())) {
                    continue;
                }
                if (!seen.containsAll(roomMap.getVisualNeighbors(nextPosition))) {
                    nextPoints.add(nextPosition);
                } else {
                    neighborQueue.add(new Position(nextPosition.getY() - 1, nextPosition.getX()));
                    neighborQueue.add(new Position(nextPosition.getY() + 1, nextPosition.getX()));
                    neighborQueue.add(new Position(nextPosition.getY(), nextPosition.getX() - 1));
                    neighborQueue.add(new Position(nextPosition.getY(), nextPosition.getX() + 1));
                }
            }
        }
    }

    private List<IProblemState> getExpandingBorderNeighborStates(HashSet<Position> seen, HashSet<Position> visitedPositions, LinkedList<IProblemState> neighborQueue) {
        visitedPositions.add(position);
        List<IProblemState> neighborStates = new ArrayList<>();
        neighborQueue.addAll(getNeighborStates());
        while (!neighborQueue.isEmpty()){
            RoomMapState nextState = (RoomMapState) neighborQueue.poll();
            if (visitedPositions.contains(nextState.position)) continue;
            else visitedPositions.add(nextState.position);
            if (!seen.equals(nextState.seen)) {
                neighborStates.add(nextState);
            } else {
                neighborQueue.addAll(nextState.getNeighborStates());
            }
        }
        nextPoints = new HashSet<>();
        for (IProblemState neighborState : neighborStates) {
            RoomMapState nextState = (RoomMapState) neighborState;
            nextPoints.add(nextState.position);
        }
        return neighborStates;
    }

    public ArrayList<Position> asPartOfSolution() {
        if ((MOVEMENT_METHOD.equals("Jump") || MOVEMENT_METHOD.equals("Expanding Border")) && lastStep != null) {
            return new ArrayList<>(((RoomMapJumpStep) lastStep).getPath());
        }
        ArrayList<Position> pos = new ArrayList<>();
        pos.add(position);
        return pos;
    }
}
