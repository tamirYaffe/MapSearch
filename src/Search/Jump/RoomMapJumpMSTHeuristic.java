package Search.Jump;

import Search.IHeuristic;
import Search.IProblemState;
import Search.Position;
import Search.RoomMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class RoomMapJumpMSTHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof RoomMapJumpState) {
            RoomMapJumpState s = (RoomMapJumpState) problemState;
            if (s.isGoalState()) return 0;
            RoomMap r = (RoomMap) s.getProblem();
            TreeMap<Position, HashSet<Position>> watchedDictionary = r.getWatchedDictionary();
            HashMap<Position, HashSet<Double>> visualLineDictionary = r.getVisualLineDictionary();
            RoomMapJumpGraphAdapter g = new RoomMapJumpGraphAdapter(watchedDictionary, visualLineDictionary, s, 0.0, 1000);
            double h=g.getPrimMSTWeight(s);
            if (s.nextPoints == null)
                s.updateMST(g.getGraph(), h);
            return h;
        } else return Double.MAX_VALUE / 2;
    }
}
