package Search;

import java.util.*;

public class RoomMapMSTHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof RoomMapState) {
            RoomMapState s = (RoomMapState) problemState;
            RoomMap r = (RoomMap) s.getProblem();
            TreeMap<Position, HashSet<Position>> watchedDictionary = r.getWatchedDictionary();
            HashMap<Position, HashSet<Double>> visualLineDictionary=r.getVisualLineDictionary();
            RoomMapGraphAdapter g = new RoomMapGraphAdapter(watchedDictionary,visualLineDictionary, s, 0.0, 5);
            return g.getPrimMSTWeight()+g.getMinDistance(s.getPosition());
        } else return Double.MAX_VALUE / 2;
    }
}
