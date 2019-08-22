package Search;

import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import java.util.*;

public class RoomMapHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof RoomMapState) {
            RoomMapState s = (RoomMapState) problemState;
            RoomMap r = (RoomMap) s.getProblem();
            double h = 0;
            TreeMap<Position, HashSet<Position>> watchedDictionary = r.getWatchedDictionary();
            HashMap<Position, HashSet<Double>> visualLineDictionary=r.getVisualLineDictionary();
            RoomMapGraphAdapter g = new RoomMapGraphAdapter(watchedDictionary,visualLineDictionary, s, 0.0, 5);
            PrimMinimumSpanningTree<PositionVertex, UndirectedWeightedEdge> primMinimumSpanningTree = new PrimMinimumSpanningTree<>(g.getGraph());
            h = primMinimumSpanningTree.getSpanningTree().getWeight();
            return h;
        } else return Double.MAX_VALUE / 2;
    }

}
