package Search.Jump;


import Search.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;


public class RoomMapJump extends RoomMap implements IProblem {


    public RoomMapJump() {
        super();
    }


    public RoomMapJump(int[][] room, Position startPosition) {
        super(room,startPosition);
//        heuristic = new RoomMapCountHeuristic();
//        heuristic = new RoomMapSingletonHeuristic();
        super.heuristic = new RoomMapMSTHeuristic();
//        super.heuristic = new RoomMapTSPHeuristic();
//        heuristic = new RoomMapUnseenSCCHeuristic();
    }



    public int[][] getRoomMap() {
        return super.getRoomMap();
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public IHeuristic getHeuristic() {
        return heuristic;
    }

    @Override
    public IProblemState getProblemState() {
        return new RoomMapJumpState(this, startPosition, getVisualNeighbors(startPosition), null);
    }

    /**
     * get all positions in the map that can be seen from the given position
     *
     * @param position - the current position
     * @return - HashSet of Seen Positions
     */
    public HashSet<Position> getVisualNeighbors(Position position) {
        return super.getVisualNeighbors(position);
    }

    public int getNumberOfPositions() {
        return super.getNumberOfPositions();
    }

    public HashMap<Position, HashSet<Position>> getVisualDictionary() {
        return super.getVisualDictionary();
    }

    public int getTotalWatches() {
        return super.getTotalWatches();
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
        return super.getWatchedDictionary();
    }

    public HashMap<Position, HashSet<Double>> getVisualLineDictionary() {
        return super.getVisualLineDictionary();
    }

    public String getVisualAlgorithm(){
        return super.getVisualAlgorithm();
    }

    public String getHeuristicName() {
        return heuristic.getClass().getSimpleName();
    }
}
