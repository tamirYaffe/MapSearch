package Search;


import javafx.geometry.Pos;
import org.jgrapht.Graph;
import rlforj.examples.ExampleBoard;
import rlforj.los.BresLos;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;


public class RoomMap implements IProblem {
    protected Position startPosition;
    protected IHeuristic heuristic;  //Room problem heuristic
    private RoomMapService self;
    public static String MOVEMENT_METHOD = "4-way";


    public RoomMap() {
        heuristic = new ZeroHeuristic();
    }


    public RoomMap(int[][] room, Position startPosition) {
        self = new RoomMapService(this, room);
        this.startPosition = new Position(startPosition);
        heuristic = new ZeroHeuristic();
    }

    public RoomMap(int[][] room, Position startPosition, String movement, String heuristic, String los) {
        self = new RoomMapService(this, room, los);
        this.startPosition = new Position(startPosition);
        switch (heuristic) {
            case "Zero":
                this.heuristic = new ZeroHeuristic();
                break;
            case "Singleton":
                this.heuristic = new RoomMapSingletonHeuristic();
                break;
            case "MST":
                this.heuristic = new RoomMapMSTHeuristic();
                break;
            case "TSP":
                this.heuristic = new RoomMapTSPHeuristic();
                break;
        }
        MOVEMENT_METHOD = movement;
    }


    public int[][] getRoomMap() {
        return self.getRoomMap();
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
    public HashSet<Position> getVisualNeighbors(Position position) {
        return self.getVisualNeighbors(position);
    }

    public int getNumberOfPositions() {
        return self.getNumberOfPositions();
    }

    public HashMap<Position, HashSet<Position>> getVisualDictionary() {
        return self.getVisualDictionary();
    }

    public int getTotalWatches() {
        return self.getTotalWatches();
    }

    @Override
    public IHeuristic getProblemHeuristic() {
        return heuristic;
    }

    @Override
    public boolean performMove(IProblemMove move) {
        return true;
    }

    public TreeMap<Position, HashSet<Position>> getWatchedDictionary() {
        return self.getWatchedDictionary();
    }

    public HashMap<Position, HashSet<Double>> getVisualLineDictionary() {
        return self.getVisualLineDictionary();
    }

    public String getVisualAlgorithm() {
        return self.getVisualAlgorithm();
    }

    public String getHeuristicName() {
        return heuristic.getClass().getSimpleName();
    }

}
