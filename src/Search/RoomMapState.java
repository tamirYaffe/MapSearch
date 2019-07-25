package Search;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class RoomMapState implements IProblemState {
    private RoomMap roomMap; //the room map
    private Position position;  //current position
    private HashSet<Position> seen;       //seen positions
    private RoomStep lastStep;  //last step

    public RoomMapState(RoomMap roomMap, Position position, HashSet<Position> seen, RoomStep lastStep) {
        this.roomMap = roomMap;
        this.position = position;
        this.seen = seen;
        this.lastStep = lastStep;
    }

    @Override
    public List<IProblemState> getNeighborStates() {
        List<IProblemState> neighborStates = new ArrayList<>();
        List<RoomStep> legalMoves = getLegalMoves();
        for (RoomStep move : legalMoves) {
            IProblemState newState = performMove(move);
            if (newState != null)
                neighborStates.add(newState);
        }
        return neighborStates;
    }

    @Override
    public String toString() {
        String[][] room = new String[roomMap.getRoomMap().length][roomMap.getRoomMap()[0].length];
        String s = "";
        for (int i = 0; i < room.length; i++) {
            for (int j = 0; j < room[0].length; j++) {
                room[i][j] = "   ";
                if (roomMap.getRoomMap()[i][j] == 1) room[i][j] = "###";
            }
        }
        for (Position p : seen) {
            room[p.getY()][p.getX()] = "///";
        }
        s += "position: " + position.getX() + "," + position.getY() + "\n";
        s += lastStep + "\n";
//        System.out.println(lastStep);
        for (int i = 0; i < room.length; i++) {
            s += Arrays.toString(room[i]) + "\n";
//            System.out.println(Arrays.toString(room[i]));
        }
        return s + "\n";
    }

    private List<RoomStep> getLegalMoves() {
        int height = roomMap.getRoomMap().length;
        int width = roomMap.getRoomMap()[0].length;

        List<RoomStep> moveList = new ArrayList<>();
        if (position.getY() > 0)
            moveList.add(new RoomStep(RoomStep.MOVE.UP));
        if (position.getY() < height - 1)
            moveList.add(new RoomStep(RoomStep.MOVE.DOWN));
        if (position.getX() > 0)
            moveList.add(new RoomStep(RoomStep.MOVE.LEFT));
        if (position.getX() < width - 1)
            moveList.add(new RoomStep(RoomStep.MOVE.RIGHT));
        if (position.getY() > 0 && position.getX() < width - 1)
            moveList.add(new RoomStep(RoomStep.MOVE.UP_RIGHT));
        if (position.getY() > 0 && position.getX() > 0)
            moveList.add(new RoomStep(RoomStep.MOVE.UP_LEFT));
        if (position.getY() < height - 1 && position.getX() < width - 1)
            moveList.add(new RoomStep(RoomStep.MOVE.DOWN_RIGHT));
        if (position.getY() < height - 1 && position.getX() > 0)
            moveList.add(new RoomStep(RoomStep.MOVE.DOWN_LEFT));
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
        toString();
        return lastStep;
    }

    @Override
    public double getStateLastMoveCost() {
        if (lastStep!=null && (lastStep._move == RoomStep.MOVE.DOWN || lastStep._move == RoomStep.MOVE.UP
                || lastStep._move == RoomStep.MOVE.RIGHT || lastStep._move == RoomStep.MOVE.LEFT))
            return 1;
        else return Math.sqrt(2);
    }

    @Override
    public IProblemState performMove(IProblemMove move) {
        if (!(move instanceof RoomStep))
            return null;
        RoomMap newProblem = roomMap;
        Position newPosition = new Position(position);
        HashSet<Position> newSeen = new HashSet<>(seen);
        int x = newPosition.getX();
        int y = newPosition.getY();
        RoomStep roomStep = (RoomStep) move;

        // Find the moving cell
        if (roomStep._move == RoomStep.MOVE.DOWN)
            newPosition.setY(y + 1);
        else if (roomStep._move == RoomStep.MOVE.UP)
            newPosition.setY(y - 1);
        else if (roomStep._move == RoomStep.MOVE.RIGHT)
            newPosition.setX(x + 1);
        else if (roomStep._move == RoomStep.MOVE.LEFT)
            newPosition.setX(x - 1);
        else if (roomStep._move == RoomStep.MOVE.UP_RIGHT) {
            newPosition.setY(y - 1);
            newPosition.setX(x + 1);
        } else if (roomStep._move == RoomStep.MOVE.UP_LEFT) {
            newPosition.setY(y - 1);
            newPosition.setX(x - 1);
        } else if (roomStep._move == RoomStep.MOVE.DOWN_RIGHT) {
            newPosition.setY(y + 1);
            newPosition.setX(x + 1);
        } else if (roomStep._move == RoomStep.MOVE.DOWN_LEFT) {
            newPosition.setY(y + 1);
            newPosition.setX(x - 1);
        }

        // Validation
        if (outOfBoundaries(newPosition.getY(), newPosition.getX()))
            return null;

        newSeen.addAll(roomMap.getVisualNeighbors(newPosition));
//        System.out.println(this);
        // Create new state
        return new RoomMapState(newProblem, newPosition, newSeen, roomStep);
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
}
