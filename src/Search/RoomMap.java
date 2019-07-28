package Search;


import rlforj.examples.ExampleBoard;
import rlforj.los.BresLos;

import java.util.HashMap;
import java.util.HashSet;

public class RoomMap implements IProblem {
    private int[][] room;         //map array

    private Position startPosition;
    private IHeuristic heuristic;  //Room problem heuristic
    private int numOfPositions;
    private HashMap<Position, HashSet<Position>> watchedDictionary;
    private HashMap<Position, HashSet<Position>> visualDictionary;
    private int totalWatches;


    public RoomMap() {
        heuristic = new RoomMapHeuristic();
    }

    public RoomMap(int[][] room) {
        this.room = getRoomMapCopy(room);
        startPosition = findPositionOnMap(2);
        heuristic = new RoomMapCountHeuristic();
        makeVisualDictionaries();
    }

    public RoomMap(int[][] room, Position startPosition) {
        this.room = getRoomMapCopy(room);
        this.startPosition = new Position(startPosition);
        heuristic = new RoomMapCountHeuristic();
        makeVisualDictionaries();
        int x = 0;
    }

    private void makeVisualDictionaries() {
        visualDictionary = new HashMap<>();
        watchedDictionary = new HashMap<>();
        int h = room.length;
        int w = room[0].length;
        ExampleBoard b = new ExampleBoard(w, h);
        //set the obstacles and the valid positions
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (room[i][j] == 1)
                    b.setObstacle(j, i); //add obstacle to the board
                else if (room[i][j] == 0) {
//                    numOfPositions++;
                    Position keyPosition = new Position(i, j);
                    watchedDictionary.put(keyPosition, new HashSet<>()); //add a Position with a counter
                    visualDictionary.put(keyPosition, new HashSet<>()); // make an HashSet of the positions that are visible
                }
            }
        }
        numOfPositions = visualDictionary.size();
        b.resetVisitedAndMarks();
        BresLos a = new BresLos(false);

        //for each position add to all other counters it's watch (+1)
        for (Position watchingPosition : watchedDictionary.keySet()) {
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    if (room[i][j] != 1) {
                        if ((a.existsLineOfSight(b, watchingPosition.getX(), watchingPosition.getY(), j, i, true))) { //if (x,y) sees (j,i)
                            Position watchedPosition = new Position(i, j);
                            watchedDictionary.get(watchedPosition).add(watchingPosition); // let (j,i) add to it's list (x,y)
                            visualDictionary.get(watchingPosition).add(watchedPosition);// let (x,y) add to it's list (j,i)
                            totalWatches++;
                        }
                    }
                }
            }

        }

    }


    public HashMap<Position, HashSet<Position>> getWatchedDictionary() {
        return watchedDictionary;
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

    private int[][] getRoomMapCopy(int[][] room) {
        int[][] newRoomMap = new int[room.length][room[0].length];
        for (int i = 0; i < room.length; i++) {
            System.arraycopy(room[i], 0, newRoomMap[i], 0, room[i].length);
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
        return visualDictionary.get(position);
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

    public HashMap<Position, HashSet<Position>> getVisualDictionary() {
        return visualDictionary;
    }

    public int getTotalWatches() {
        return totalWatches;
    }
}
