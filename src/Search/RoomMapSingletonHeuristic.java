package Search;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class RoomMapSingletonHeuristic implements IHeuristic{
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof RoomMapState) {
            RoomMapState s = (RoomMapState) problemState;
            RoomMap r = (RoomMap) s.getProblem();
            double h=0;
            TreeMap<Position, HashSet<Position>> watchedDictionary=r.getWatchedDictionary();
            return h;
        }
        else return Double.MAX_VALUE;
    }
}
