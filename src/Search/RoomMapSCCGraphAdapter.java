package Search;

//import com.mxgraph.layout.mxIGraphLayout;
//import com.mxgraph.util.mxCellRenderer;
//import org.jgrapht.ext.JGraphXAdapter;
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import org.jgrapht.alg.tour.HeldKarpTSP;
//import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import javafx.util.Pair;

import java.util.List;
import java.util.*;


class RoomMapSCCGraphAdapter {
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
                    PositionVertex vertex = new PositionVertex(position, PositionVertex.TYPE.PRUNNABLE);
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
        return new Pair<>(next, minEdgeWeight);
    }

//    public double getTSPWeight(Position startPosition) {
//        DefaultUndirectedWeightedGraph<SCCSubGraph, UndirectedWeightedEdge> graph = new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);
//        SCCSubGraph start = new SCCSubGraph(UndirectedWeightedEdge.class);
//        start.addVertex(new PositionVertex(new Position(), PositionVertex.TYPE.UNPRUNNABLE));
//        graph.addVertex(start);
//        for (SCCSubGraph sccSubGraph : connectedComponentsGraphs) {
//            graph.addVertex(sccSubGraph);
//            for (SCCSubGraph otherSccSubGraph : connectedComponentsGraphs) {
//                if (sccSubGraph.equals(otherSccSubGraph)) continue;
//                graph.addVertex(otherSccSubGraph);
//                Pair<Position, Double> ans = getMinEdge(startPosition, otherSccSubGraph);
//                UndirectedWeightedEdge edge = graph.addEdge(sccSubGraph, otherSccSubGraph);
//                if (edge != null) {
//                    graph.setEdgeWeight(edge, ans.getValue());
//                }
//                startPosition = ans.getKey();
//            }
//        }
//        for (SCCSubGraph vertex : graph.vertexSet()) {
//            UndirectedWeightedEdge edge = graph.addEdge(start, vertex);
//            graph.setEdgeWeight(edge, 0);
//        }
//        graph.removeEdge(start, start);
//        HeldKarpTSP<SCCSubGraph, UndirectedWeightedEdge> heldKarpTSP = new HeldKarpTSP<>();
//        return heldKarpTSP.getTour(graph).getWeight();
//
//    }

//    public void printGraph(String path, Graph graph) {
//        try {
////            File imgFile = new File("resources/graph.png");
//            File imgFile = new File(path);
//            imgFile.createNewFile();
//            JGraphXAdapter<PositionVertex, UndirectedWeightedEdge> graphAdapter =
//                    new JGraphXAdapter<PositionVertex, UndirectedWeightedEdge>(graph);
//            mxCircleLayout layout = new mxCircleLayout(graphAdapter,3.0);
////            mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
////            layout.setInterHierarchySpacing(layout.getInterHierarchySpacing() * 15);
////            layout.setInterRankCellSpacing(layout.getInterRankCellSpacing() * 15);
//            layout.execute(graphAdapter.getDefaultParent());
//            BufferedImage image =
//                    mxCellRenderer.createBufferedImage(graphAdapter, null, 1, Color.WHITE, true, null);
//            imgFile = new File(path);
//            ImageIO.write(image, "PNG", imgFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.exit(0);
//    }
}
