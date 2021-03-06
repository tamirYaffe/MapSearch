package Search;

import java.util.*;

public class RoomMapMSTHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof RoomMapState) {
            RoomMapState s = (RoomMapState) problemState;
            RoomMap r = (RoomMap) s.getProblem();
            TreeMap<Position, HashSet<Position>> watchedDictionary = r.getWatchedDictionary();
            HashMap<Position, HashSet<Double>> visualLineDictionary = r.getVisualLineDictionary();
//            RoomMapGraphAdapter g = new RoomMapGraphAdapter(watchedDictionary,  s,true);
            RoomMapGraphAdapter g = s.getGraphAdapter();
            return g.getPrimMSTWeight();
        } else return Double.MAX_VALUE / 2;
    }
}
