package Search;

import Search.Jump.RoomMapJumpGraphAdapter;
import Search.Jump.RoomMapJumpState;

import java.util.*;

public class RoomMapMSTHeuristic implements IHeuristic {
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
        } else if (problemState instanceof RoomMapState) {
            RoomMapState s = (RoomMapState) problemState;
            RoomMap r = (RoomMap) s.getProblem();
            TreeMap<Position, HashSet<Position>> watchedDictionary = r.getWatchedDictionary();
            HashMap<Position, HashSet<Double>> visualLineDictionary = r.getVisualLineDictionary();
            RoomMapGraphAdapter g = new RoomMapGraphAdapter(watchedDictionary, visualLineDictionary, s, 0.0, 1000);
            return g.getPrimMSTWeight();
        } else return Double.MAX_VALUE / 2;
    }
}
