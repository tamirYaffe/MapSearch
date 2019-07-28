package Search;

import java.util.HashMap;
import java.util.HashSet;

public class RoomMapCountHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof RoomMapState) {
            RoomMapState s = (RoomMapState) problemState;
            RoomMap r = (RoomMap) s.getProblem();
            HashMap<Position, int[]> counters = r.getWatchesCount();
            HashSet<Position> neighbors = r.getVisualNeighbors(s.getPosition());
            double positionWeight = 0;
            //for each position watched by the agent, add it's counter to the position weight
            for (Position neighbor : neighbors) {
                positionWeight += counters.getOrDefault(neighbor, new int[1])[0];
            }
            return (r.getTotalWatches() - positionWeight) / r.getTotalWatches();
        } else return Double.MAX_VALUE / 2;
    }
}
