package Search;

import java.util.*;

public class RoomMapMSTHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof RoomMapState) {
            RoomMapState s = (RoomMapState) problemState;
            RoomMap r = (RoomMap) s.getProblem();
            TreeMap<Position, HashSet<Position>> watchedDictionary = r.getWatchedDictionary();
            RoomMapGraphAdapter g = new RoomMapGraphAdapter(watchedDictionary, s, 0.0, 1000);
            return g.getPrimMSTWeight();
        } else return Double.MAX_VALUE / 2;
    }
}
