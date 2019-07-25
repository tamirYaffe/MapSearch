package Search;


import rlforj.examples.ExampleBoard;
import rlforj.los.BresLos;
import rlforj.los.IFovAlgorithm;
import rlforj.los.PrecisePermissive;

import java.util.HashSet;

public class RoomMap implements IProblem {

    private int[][] room;         //map array
    private Position startPosition;
    private IHeuristic heuristic;  //Room problem heuristic
    private int numOfPositions;


    public RoomMap() {
        heuristic = new RoomMapHeuristic();
    }

    public RoomMap(int[][] room) {
        this.room = getRoomMapCopy(room);
        startPosition = findPositionOnMap(2);
        heuristic = new RoomMapHeuristic();
    }

    private Position findPositionOnMap(int posIndex) {
        for (int i = 0; i < room.length; i++) {
            for (int j = 0; j < room[0].length; j++) {
                if (room[i][j] == posIndex) {
                    numOfPositions++;
                    room[i][j] = 0;
                    return new Position(i, j);
                }
            }
        }
        return null;
    }

    public RoomMap(int[][] room, Position startPosition) {
        this.room = getRoomMapCopy(room);
        this.startPosition = new Position(startPosition);
        heuristic = new RoomMapHeuristic();
    }

    private int[][] getRoomMapCopy(int[][] room) {
        int[][] newRoomMap = new int[room.length][room[0].length];
        for (int i = 0; i < room.length; i++) {
            for (int j = 0; j < room[i].length; j++) {
                if (room[i][j] == 0) numOfPositions++;
                newRoomMap[i][j] = room[i][j];
            }
        }
        return newRoomMap;
    }

    int[][] getRoomMap() {
        return room;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public IHeuristic getHeuristic() {
        return heuristic;
    }

    @Override
    public IProblemState getProblemState() {
        return new RoomMapState(this, startPosition, getVisualNeighbors(startPosition), null);
    }

    /**
     * get all positions in the map that can be seen from the given position
     *
     * @param position - the current position
     * @return - HashSet of Seen Positions
     */
    HashSet<Position> getVisualNeighbors(Position position) {
        int x = position.getX(), y = position.getY();
        HashSet<Position> neighbors = new HashSet<>();
        neighbors.add(new Position(y, x));
        int w = room[0].length;
        int h = room.length;
        ExampleBoard b = new ExampleBoard(w, h);
        for (int i = 0; i < room.length; i++) {
            for (int j = 0; j < room[0].length; j++) {
                if (room[i][j] == 1)
                    b.setObstacle(j, i);
            }
        }
        b.resetVisitedAndMarks();
//        IFovAlgorithm a = new PrecisePermissive();
//        a.visitFieldOfView(b, x, y, w + h);
        BresLos a = new BresLos(false);
//        b.print(x, y);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
//                if (b.wasVisited(i, j) && room[i][j] != 1)
                if (a.existsLineOfSight(b,x,y,j,i,true) && room[i][j] != 1)
                    neighbors.add(new Position(i, j));
            }
        }

//
//        //RIGHT
//        while (x < room[0].length - 1 && room[y][++x] == 0) {
//            neighbors.add(new Position(y, x));
//        }
//        x = position.getX();
//
//        //LEFT
//        while (x > 0 && room[y][--x] == 0) {
//            neighbors.add(new Position(y, x));
//        }
//        x = position.getX();
//
//        //DOWN
//        while (y < room.length - 1 && room[++y][x] == 0) {
//            neighbors.add(new Position(y, x));
//        }
//        y = position.getY();
//
//        //UP
//        while (y > 0 && room[--y][x] == 0) {
//            neighbors.add(new Position(y, x));
//        }
//        y = position.getY();
//
//        //UP RIGHT
//        while (x < room[0].length - 1 && y > 0 && room[--y][++x] == 0) {
//            neighbors.add(new Position(y, x));
//        }
//        x = position.getX();
//        y = position.getY();
//
//        //DOWN RIGHT
//        while (x < room[0].length - 1 && y < room.length - 1 && room[++y][++x] == 0) {
//            neighbors.add(new Position(y, x));
//        }
//        x = position.getX();
//        y = position.getY();
//
//        //UP LEFT
//        while (x > 0 && y > 0 && room[--y][--x] == 0) {
//            neighbors.add(new Position(y, x));
//        }
//        x = position.getX();
//        y = position.getY();
//
//        //DOWN LEFT
//        while (x > 0 && y < room.length - 1 && room[++y][--x] == 0) {
//            neighbors.add(new Position(y, x));
//        }
        return neighbors;
    }

    @Override
    public IHeuristic getProblemHeuristic() {
        return heuristic;
    }

    @Override
    public boolean performMove(IProblemMove move) {
        return true;
    }

    int getNumberOfPositions() {
        return numOfPositions;
    }
}
