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
//            double start = System.nanoTime();
            RoomMapGraphAdapter g = new RoomMapGraphAdapter(watchedDictionary, s, 0.0, 5);
            PrimMinimumSpanningTree<PositionVertex, UndirectedWeightedEdge> primMinimumSpanningTree = new PrimMinimumSpanningTree<>(g.getGraph());
//            System.out.println("prim: " + primMinimumSpanningTree.getSpanningTree().getWeight());
            h = primMinimumSpanningTree.getSpanningTree().getWeight();
//            KruskalMinimumSpanningTree<PositionVertex, UndirectedWeightedEdge> kruskalMinimumSpanningTree = new KruskalMinimumSpanningTree<>(g);
//            System.out.println("kruskal: " + kruskalMinimumSpanningTree.getSpanningTree().getWeight());
//
//            double end = System.nanoTime();
//            System.out.println("\ntime: " + ((end - start) / 1000000) + " ms");
            return h;
        } else return Double.MAX_VALUE / 2;
    }

}
