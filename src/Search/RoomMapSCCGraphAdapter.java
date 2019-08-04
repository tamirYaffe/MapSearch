package Search;

import javafx.util.Pair;
import java.util.List;
import java.util.*;


public class RoomMapSCCGraphAdapter {
    private List<SCCSubGraph> connectedComponentsGraphs = new ArrayList<>();

    public RoomMapSCCGraphAdapter(TreeMap<Position, HashSet<Position>> watchedDictionary, HashMap<Position, HashSet<Double>> visualLineDictionary, RoomMapState s, double threshold, int maxLeavesCount) {
        addVerticesToGraph(watchedDictionary, visualLineDictionary, s, threshold, maxLeavesCount);
    }


    private void addVerticesToGraph(TreeMap<Position, HashSet<Position>> watchedDictionary, HashMap<Position, HashSet<Double>> visualLineDictionary, RoomMapState s, double threshold, int maxLeavesCount) {
        for (Map.Entry<Position, HashSet<Position>> entry : watchedDictionary.entrySet()) {
            Position key = entry.getKey();
            HashSet<Position> value = entry.getValue();
            double weight = 1.0 / visualLineDictionary.get(key).size();
            if (weight < threshold || maxLeavesCount <= 0) break;
            if (!s.getSeen().contains(key)) {
                maxLeavesCount--;
                PositionVertex watchedVertex = new PositionVertex(key, PositionVertex.TYPE.UNPRUNNABLE);
                SCCSubGraph subGraph = new SCCSubGraph(UndirectedWeightedEdge.class);
                subGraph.addVertex(watchedVertex);
                subGraph.setRarePoint(watchedVertex.getPosition());
                for (Position position : value) {
                    if (position.equals(key)) continue;
                    PositionVertex vertex = new PositionVertex(position, PositionVertex.TYPE.PRUNEABLE);
                    subGraph.addVertex(vertex);
                    UndirectedWeightedEdge edge = subGraph.addEdge(vertex, watchedVertex);
                    subGraph.setEdgeWeight(edge, 0);
                }
                if (!subGraph.vertexSet().isEmpty())
                    connectedComponentsGraphs.add(subGraph);
            }
        }
    }

    public double getUnseenSCCWeight(RoomMapState s) {
        double minH = Double.MAX_VALUE;
        ArrayList<Integer> a = new ArrayList<>();
        for (int i = 0; i < connectedComponentsGraphs.size(); i++) {
            a.add(i);
        }
        for (ArrayList<Integer> integers : Combination.choose(a, connectedComponentsGraphs.size())) {
            Position current = s.getPosition();
            double h = 0;
            for (Integer integer : integers) {
                Pair<Position, Double> ans = getMinEdge(current, connectedComponentsGraphs.get(integer));
                h += ans.getValue();
                current = ans.getKey();
            }
            if (h < minH)
                minH = h;
        }
        return minH;
    }

    private Pair<Position, Double> getMinEdge(Position current, SCCSubGraph subGraph) {
        double minEdgeWeight = Double.MAX_VALUE;
        Position next = null;
        for (PositionVertex vertex : subGraph.vertexSet()) {
            if (vertex.getType() == PositionVertex.TYPE.UNPRUNNABLE) continue;
            double edgeWeight = DistanceService.getWeight(current, vertex.getPosition());
            if (edgeWeight < minEdgeWeight) {
                minEdgeWeight = edgeWeight;
                next = vertex.getPosition();
            }
        }
//        System.out.println("next: "+next+" weight: "+minEdgeWeight);
        return new Pair<>(next, minEdgeWeight);
    }
}
