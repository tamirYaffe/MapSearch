package Search;

import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;

import java.util.*;

public class RoomMapMSTHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof RoomMapState) {
            RoomMapState s = (RoomMapState) problemState;
            RoomMap r = (RoomMap) s.getProblem();
            TreeMap<Position, HashSet<Position>> watchedDictionary = r.getWatchedDictionary();
//            double start = System.nanoTime();
            RoomMapGraphAdapter g = new RoomMapGraphAdapter(watchedDictionary, s, 0.0);

//            printGraph(g, "resources/graph.png");
            PrimMinimumSpanningTree<PositionVertex, UndirectedWeightedEdge> primMinimumSpanningTree = new PrimMinimumSpanningTree<>(g.getGraph());
//            System.out.println("prim: " + primMinimumSpanningTree.getSpanningTree().getWeight());
            double h = primMinimumSpanningTree.getSpanningTree().getWeight();
//            KruskalMinimumSpanningTree<PositionVertex, UndirectedWeightedEdge> kruskalMinimumSpanningTree = new KruskalMinimumSpanningTree<>(g);
//            System.out.println("kruskal: " + kruskalMinimumSpanningTree.getSpanningTree().getWeight());
//
//            double end = System.nanoTime();
//            System.out.println("\ntime: " + ((end - start) / 1000000) + " ms");
            return h;
        } else return Double.MAX_VALUE / 2;
    }
}
