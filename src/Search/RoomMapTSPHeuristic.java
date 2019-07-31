package Search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class RoomMapTSPHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof RoomMapState) {
            RoomMapState s = (RoomMapState) problemState;
            RoomMap r = (RoomMap) s.getProblem();
            double h = 0;
            TreeMap<Position, HashSet<Position>> watchedDictionary = r.getWatchedDictionary();
            RoomMapGraphAdapter g = new RoomMapGraphAdapter(watchedDictionary, s, 0.0, 5);
            g.pruneGraph();
            return g.getTSPWeight(s.getPosition());
        } else return Double.MAX_VALUE / 2;
    }
}
