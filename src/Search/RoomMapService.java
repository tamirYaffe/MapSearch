package Search;

import rlforj.examples.ExampleBoard;
import rlforj.los.BresLos;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class RoomMapService {

    private RoomMap roomMap;
    private int[][] room;         //map array
    private int numOfPositions;
    private TreeMap<Position, HashSet<Position>> watchedDictionary;
    private HashMap<Position, HashSet<Position>> visualDictionary;
    private HashMap<Position, HashSet<Double>> visualLineDictionary;
    private int totalWatches;
    public static ExampleBoard b;

    public RoomMapService(RoomMap roomMap) {
        this.roomMap = roomMap;
        makeVisualDictionaries();
    }

    public RoomMapService(RoomMap roomMap, int[][] room) {
        this.room = getRoomMapCopy(room);
        this.roomMap = roomMap;
        makeVisualDictionaries();
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

    private void makeVisualDictionaries() {
        visualDictionary = new HashMap<>();
        visualLineDictionary = new HashMap<>();
        HashMap<Position, HashSet<Position>> tempWatchedDictinary = new HashMap<>();
        int h = room.length;
        int w = room[0].length;
        b = new ExampleBoard(w, h);
        //set the obstacles and the valid positions
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (room[i][j] == 1)
                    b.setObstacle(j, i); //add obstacle to the board
                else if (room[i][j] == 0) {
                    numOfPositions++;
                    Position keyPosition = new Position(i, j);
                    tempWatchedDictinary.put(keyPosition, new HashSet<>()); //add a Position with a counter
                    visualDictionary.put(keyPosition, new HashSet<>()); // make an HashSet of the positions that are visible
                    visualLineDictionary.put(keyPosition, new HashSet<>()); // make an HashSet of the positions that are visible
                }
            }
        }
        numOfPositions = visualDictionary.size();
        b.resetVisitedAndMarks();
        BresLos a = new BresLos(false);

        //for each position add to all other counters it's watch (+1)
        for (Position watchingPosition : tempWatchedDictinary.keySet()) {
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    if (room[i][j] != 1) {
                        if ((a.existsLineOfSight(b, watchingPosition.getX(), watchingPosition.getY(), j, i, true))) { //if (x,y) sees (j,i)
                            Position watchedPosition = new Position(i, j);
                            tempWatchedDictinary.get(watchedPosition).add(watchingPosition); // let (j,i) add to it's list (x,y)
                            visualDictionary.get(watchingPosition).add(watchedPosition);// let (x,y) add to it's list (j,i)
                            totalWatches++;

                            //update vector dictionary
                            double vector=(double) (watchingPosition.getX()-watchedPosition.getX())/(watchingPosition.getY()-watchedPosition.getY());
                            if(!visualLineDictionary.get(watchedPosition).contains(vector))
                                visualLineDictionary.get(watchedPosition).add(vector);
                        }
                    }
                }
            }

        }
        watchedDictionary = new TreeMap<>(new Comparator<Position>() {
            @Override
            public int compare(Position o1, Position o2) {
                if (tempWatchedDictinary.getOrDefault(o1, new HashSet<>()).size() > tempWatchedDictinary.getOrDefault(o2, new HashSet<>()).size())
                    return 1;
                return -1;
            }
        });
        watchedDictionary.putAll(tempWatchedDictinary);

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

    int getNumberOfPositions() {
        return numOfPositions;
    }

    HashMap<Position, HashSet<Position>> getVisualDictionary() {
        return visualDictionary;
    }

    public int getTotalWatches() {
        return totalWatches;
    }

    TreeMap<Position, HashSet<Position>> getWatchedDictionary() {
        return watchedDictionary;
    }

    public HashMap<Position, HashSet<Double>> getVisualLineDictionary() {
        return visualLineDictionary;
    }
}
