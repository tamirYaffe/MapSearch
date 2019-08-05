package Search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class RoomMapUnseenSCCHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof RoomMapState) {
            RoomMapState s = (RoomMapState) problemState;
            RoomMap r = (RoomMap) s.getProblem();
            double h = 0;
            TreeMap<Position, HashSet<Position>> watchedDictionary = r.getWatchedDictionary();
            HashMap<Position, HashSet<Double>> visualLineDictionary=r.getVisualLineDictionary();
            RoomMapSCCGraphAdapter g = new RoomMapSCCGraphAdapter(watchedDictionary,visualLineDictionary, s, 0, 5);
            return g.getUnseenSCCWeight(s);
        } else return Double.MAX_VALUE / 2;
    }
}
