package Search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class RoomMapMSPHeuristic implements IHeuristic {
//    MSP = Minimal Simple Path
    @Override
    public double getHeuristic(IProblemState problemState) {
//        if (problemState instanceof RoomMapState) {
//            RoomMapState s = (RoomMapState) problemState;
//            RoomMap r = (RoomMap) s.getProblem();
//            TreeMap<Position, HashSet<Position>> watchedDictionary = r.getWatchedDictionary();
//            HashMap<Position, HashSet<Double>> visualLineDictionary = r.getVisualLineDictionary();
//            RoomMapGraphAdapter g = new RoomMapGraphAdapter(watchedDictionary,  s,true);
////            System.out.println(s.toString());
//            return g.getSimplePathWeight(s);
//        } else return Double.MAX_VALUE / 2;
        return 0;
    }
}
