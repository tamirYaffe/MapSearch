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
    private Position startPosition;
    private IHeuristic heuristic;  //Room problem heuristic
    private RoomMapService self;


    public RoomMap() {
        heuristic = new RoomMapHeuristic();
    }


    public RoomMap(int[][] room, Position startPosition) {
        self = new RoomMapService(this,room);
        this.startPosition = new Position(startPosition);
//        heuristic = new RoomMapCountHeuristic();
//        heuristic = new RoomMapSingletonHeuristic();
//        heuristic = new RoomMapMSTHeuristic();
        heuristic = new RoomMapTSPHeuristic();
//        heuristic = new RoomMapUnseenSCCHeuristic();
    }



    int[][] getRoomMap() {
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
    HashSet<Position> getVisualNeighbors(Position position) {
        return self.getVisualNeighbors(position);
    }

    public int getNumberOfPositions() {
        return self.getNumberOfPositions();
    }

    HashMap<Position, HashSet<Position>> getVisualDictionary() {
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

    TreeMap<Position, HashSet<Position>> getWatchedDictionary() {
        return self.getWatchedDictionary();
    }

    public HashMap<Position, HashSet<Double>> getVisualLineDictionary() {
        return self.getVisualLineDictionary();
    }

    public String getVisualAlgorithm(){
        return self.getVisualAlgorithm();
    }

    public String getHeuristicName() {
        return heuristic.getClass().getSimpleName();
    }
}
