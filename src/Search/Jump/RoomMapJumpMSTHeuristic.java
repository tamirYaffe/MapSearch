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
//            if (s.getH()!=-1) return s.getH();
            RoomMap r = (RoomMap) s.getProblem();
            TreeMap<Position, HashSet<Position>> watchedDictionary = r.getWatchedDictionary();
            HashMap<Position, HashSet<Double>> visualLineDictionary = r.getVisualLineDictionary();
            RoomMapJumpGraphAdapter g = new RoomMapJumpGraphAdapter(watchedDictionary, visualLineDictionary, s, 0.0, 1000);
            return g.getPrimMSTWeight();
        } else return Double.MAX_VALUE / 2;
    }
}
