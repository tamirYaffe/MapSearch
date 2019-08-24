package Search;

import rlforj.examples.ExampleBoard;
import rlforj.los.BresLos;

import java.util.*;

public class RoomMapService {

    private int[][] room;         //map array
    private int numOfPositions;
    private TreeMap<Position, HashSet<Position>> watchedDictionary;
    private HashMap<Position, HashSet<Position>> visualDictionary;
    private HashMap<Position, HashSet<Double>> visualLineDictionary;
    private VisualLineOfSightAdapter visualLineOfSightAdapter;

    public RoomMapService(int[][] room) {
        this.room = getRoomMapCopy(room);
        makeVisualDictionaries();
    }

    public RoomMapService(int[][] room, String los) {
        this.room = getRoomMapCopy(room);
        switch (los) {
            case "4-way":
                visualLineOfSightAdapter = new VisualLineOfSightAdapter(new FourWayLos());
                break;
            case "8-way":
                visualLineOfSightAdapter = new VisualLineOfSightAdapter(new EightWayLos());
                break;
            case "Symmetric BresLos":
                visualLineOfSightAdapter = new VisualLineOfSightAdapter(new BresLos(true));
                break;
            case "Asymmetric BresLos":
                visualLineOfSightAdapter = new VisualLineOfSightAdapter(new BresLos(false));
                break;
        }
        makeVisualDictionaries();
    }

    private int[][] getRoomMapCopy(int[][] room) {
        int[][] newRoomMap = new int[room.length][room[0].length];
        for (int i = 0; i < room.length; i++) {
            System.arraycopy(room[i], 0, newRoomMap[i], 0, room[i].length);
        }
        return newRoomMap;
    }

    public int[][] getRoomMap() {
        return room;
    }

    private void makeVisualDictionaries() {
        visualDictionary = new HashMap<>();
        visualLineDictionary = new HashMap<>();
        HashMap<Position, HashSet<Position>> tempWatchedDictinary = new HashMap<>();
        int h = room.length;
        int w = room[0].length;
        ExampleBoard exampleBoard = new ExampleBoard(w, h);
        //set the obstacles and the valid positions
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (room[i][j] == 1)
                    exampleBoard.setObstacle(j, i); //add obstacle to the board
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
        exampleBoard.resetVisitedAndMarks();

        //for each position add to all other counters it's watch (+1)
        for (Position watchingPosition : tempWatchedDictinary.keySet()) {
            for (Position position : tempWatchedDictinary.keySet()) {
                int i = position.getY();
                int j = position.getX();
                if (room[i][j] != 1) {
                    if ((visualLineOfSightAdapter.existsLineOfSight(exampleBoard, watchingPosition.getX(), watchingPosition.getY(), j, i))) { //if (x,y) sees (j,i)
                        Position watchedPosition = new Position(i, j);
                        tempWatchedDictinary.get(watchedPosition).add(watchingPosition); // let (j,i) add to it's list (x,y)
                        visualDictionary.get(watchingPosition).add(watchedPosition);// let (x,y) add to it's list (j,i)

                        //update vector dictionary
                        double vector = (double) (watchingPosition.getX() - watchedPosition.getX()) / (watchingPosition.getY() - watchedPosition.getY());
                        visualLineDictionary.get(watchedPosition).add(vector);
                    }
                }
//                }
//                }
            }

        }
        watchedDictionary = new TreeMap<>((o1, o2) -> {
            int o1Size = tempWatchedDictinary.getOrDefault(o1, new HashSet<>()).size();
            int o2Size = tempWatchedDictinary.getOrDefault(o2, new HashSet<>()).size();
            if (o1.equals(o2)) return 0;
            if (o1Size > o2Size) return 1;
            return -1;
        });
        watchedDictionary.putAll(tempWatchedDictinary);
    }


    /**
     * get all positions in the map that can be seen from the given position
     *
     * @param position - the current position
     * @return - HashSet of Seen Positions
     */
    public HashSet<Position> getVisualNeighbors(Position position) {
        return visualDictionary.get(position);
    }

    public int getNumberOfPositions() {
        return numOfPositions;
    }

    public HashMap<Position, HashSet<Position>> getVisualDictionary() {
        return visualDictionary;
    }

    public TreeMap<Position, HashSet<Position>> getWatchedDictionary() {
        return watchedDictionary;
    }

    public HashMap<Position, HashSet<Double>> getVisualLineDictionary() {
        return visualLineDictionary;
    }

    public String getVisualAlgorithm() {
        return visualLineOfSightAdapter.getLosName();
    }
}
