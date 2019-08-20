package Search;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import static Search.DistanceService.minDistance;

public class RoomMapSingletonHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof RoomMapState) {
            RoomMapState s = (RoomMapState) problemState;
            RoomMap r = (RoomMap) s.getProblem();
            double h = computeH(r, s);
            return h;
        } else return Double.MAX_VALUE / 2;
    }

    private double computeH(RoomMap r, RoomMapState s) {
        double h = 0;
        TreeMap<Position, HashSet<Position>> watchedDictionary = r.getWatchedDictionary();
        for (Map.Entry<Position, HashSet<Position>> entry : watchedDictionary.entrySet()) {
            Position nextRarePosition = entry.getKey();
            HashSet<Position> positions = entry.getValue();
            if (!s.getSeen().contains(nextRarePosition)) {
                double minDist = minDistance(positions, s.getPosition());
                h = Math.max(minDist, h);
            }
        }
        return h;
    }
}
