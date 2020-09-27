package Search;


import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;


public class RoomMap implements IProblem {
    private Position startPosition;
    private IHeuristic heuristic;  //Room problem heuristic
    private RoomMapService self;
    static String MOVEMENT_METHOD = "4-way";
    static String HEURISTIC_GRAPH_METHOD = "All";
    static String HEURISTIC_METHOD = "MST";


    public RoomMap() {
        heuristic = new ZeroHeuristic();
    }


    public RoomMap(int[][] room, Position startPosition) {
        self = new RoomMapService(room);
        this.startPosition = new Position(startPosition);
        heuristic = new ZeroHeuristic();
    }

    public RoomMap(int[][] room, Position startPosition, String movement, String heuristic, String los, String heuristicGraph, double distFactor, boolean isNoWhites, boolean isFarthest, boolean isBounded) {
        self = new RoomMapService(room, los);
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
            case "MSP":
                this.heuristic = new RoomMapMSPHeuristic();
                break;
            case "TSP":
                this.heuristic = new RoomMapTSPHeuristic();
                break;
        }
        HEURISTIC_METHOD = heuristic;
        MOVEMENT_METHOD = movement;
        HEURISTIC_GRAPH_METHOD = heuristicGraph;
        RoomMapGraphAdapter.includeWhiteCells = !isNoWhites;
        RoomMapGraphAdapter.withFarthest = isFarthest;
        RoomMapGraphAdapter.withDistanceFactor = isBounded;
        if (distFactor>=1){
            RoomMapGraphAdapter.distanceFactor = distFactor;
        }
    }

    public static String getHeuristicGraphMethod() {
        return HEURISTIC_GRAPH_METHOD;
    }

    int[][] getRoomMap() {
        return self.getRoomMap();
    }

    public Position getStartPosition() {
        return startPosition;
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

    @Override
    public IHeuristic getProblemHeuristic() {
        return heuristic;
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
