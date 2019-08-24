package Search;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import static Search.DistanceService.minDistance;

public class RoomMapCountHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof RoomMapState) {
            RoomMapState s = (RoomMapState) problemState;
            RoomMap r = (RoomMap) s.getProblem();
            return computeH2(r, s);
        } else return Double.MAX_VALUE / 2;
    }

    private double computeH2(RoomMap r, RoomMapState s) {
        double h = 0;
        double threshold = 0.3;
        TreeMap<Position, HashSet<Position>> watchedDictionary = r.getWatchedDictionary();
        for (Map.Entry<Position, HashSet<Position>> entry : watchedDictionary.entrySet()) {
            Position nextRarePosition = entry.getKey();
            HashSet<Position> positions = entry.getValue();
            if (!s.getSeen().contains(nextRarePosition) && (1.0 / positions.size()) >= threshold) {
                double minDist = minDistance(positions, s.getPosition());
                h = Math.max(minDist, h);
            }
        }
        return h;
    }

}
