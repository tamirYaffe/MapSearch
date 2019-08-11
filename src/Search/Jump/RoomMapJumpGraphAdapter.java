package Search.Jump;

import Search.DistanceService;
import Search.Position;
import Search.PositionVertex;
import Search.UndirectedWeightedEdge;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.tour.TwoOptHeuristicTSP;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;
import org.jgrapht.util.VertexToIntegerMapping;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.*;

public class RoomMapJumpGraphAdapter {
    private Graph<PositionVertex, UndirectedWeightedEdge> graph;
    private HashMap<Position, PositionVertex> prunnableVertices;
    private HashMap<Position, PositionVertex> unPrunnableVertices;
    public static final int HUGE_DOUBLE_VALUE = 0x7fffff00;

    public RoomMapJumpGraphAdapter(TreeMap<Position, HashSet<Position>> watchedDictionary, HashMap<Position, HashSet<Double>> visualLineDictionary, RoomMapJumpState s, double threshold, int maxLeavesCount) {
        graph = new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);
        prunnableVertices = new HashMap<>();
        unPrunnableVertices = new HashMap<>();
        addVerticesToGraph(watchedDictionary, visualLineDictionary, s, threshold, maxLeavesCount);
        connectPrunnableVerticesInGraph();
        addAgentToGraph(s);
//        for (Map.Entry<Position, PositionVertex> entry : unPrunnableVertices.entrySet()) {
//            Position key = entry.getKey();
//            PositionVertex value = entry.getValue();
//            if (!key.equals(((RoomMapJump)s.getProblem()).getStartPosition())){
//                graph.removeVertex(value);
//            }
//        }
//        printGraph("basic constructor.png");
//        printGraph("basic pruned constructor.png");
    }

    public RoomMapJumpGraphAdapter(TreeMap<Position, HashSet<Position>> watchedDictionary, HashMap<Position, HashSet<Double>> visualLineDictionary, RoomMapJumpState roomMapJumpState) {
        graph = new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);
        prunnableVertices = new HashMap<>();
        unPrunnableVertices = new HashMap<>();
        for (Map.Entry<Position, HashSet<Position>> entry : watchedDictionary.entrySet()) {
            Position key = entry.getKey();
            if (roomMapJumpState.getSeen().contains(key)) continue;
            HashSet<Position> value = entry.getValue();
//            if (prunnableVertices.size() == watchedDictionary.size()- roomMapJumpState.getSeen().size()) break;
            boolean skip = false;
            for (Position position : value) {
                if (prunnableVertices.containsKey(position)) {
                    skip = true;
                    break;
                }
            }
            if (skip) continue;
            if (!prunnableVertices.containsKey(key)) {
                PositionVertex watchedVertex = new PositionVertex(key, PositionVertex.TYPE.UNPRUNNABLE);
                unPrunnableVertices.put(key, watchedVertex);
                for (Position position : value) {
                    if (unPrunnableVertices.containsKey(position)) continue;
                    PositionVertex vertex = new PositionVertex(position, PositionVertex.TYPE.PRUNEABLE);
                    prunnableVertices.put(position, vertex);
                    graph.addVertex(vertex);
                }
            }
        }
//        if (prunnableVertices.size() + unPrunnableVertices.size() < watchedDictionary.size() - roomMapJumpState.getSeen().size()) {
//            for (Map.Entry<Position, HashSet<Position>> leftovers : watchedDictionary.entrySet()) {
//                Position leftoversKey = leftovers.getKey();
//                HashSet<Position> leftoversValue = leftovers.getValue();
//
//            }
//        }
        Position agentPosition = roomMapJumpState.getPosition();
        PositionVertex agentVertex = new PositionVertex(agentPosition, PositionVertex.TYPE.UNPRUNNABLE);
        graph.addVertex(agentVertex);
        unPrunnableVertices.put(agentPosition, agentVertex);

        for (Map.Entry<Position, PositionVertex> entry : prunnableVertices.entrySet()) {
            Position key = entry.getKey();
            if (surroundedWithJumpPoints(key, visualLineDictionary)) {
                continue;
            }
            PositionVertex value = entry.getValue();
            UndirectedWeightedEdge edge = graph.addEdge(agentVertex, value);
            graph.setEdgeWeight(edge, DistanceService.getWeight(agentPosition, key));
        }
//        for (Map.Entry<Position, PositionVertex> entry1 : prunnableVertices.entrySet()) {
//            Position key1 = entry1.getKey();
//            PositionVertex value1 = entry1.getValue();
//            for (Map.Entry<Position, PositionVertex> entry2 : prunnableVertices.entrySet()) {
//                Position key2 = entry2.getKey();
//                PositionVertex value2 = entry2.getValue();
//                if (key1.equals(key2) || graph.containsEdge(value2, value1)) continue;
//                UndirectedWeightedEdge edge = graph.addEdge(value1, value2);
//                graph.setEdgeWeight(edge, DistanceService.getWeight(key1, key2));
//            }
//        }
//        printGraph("constructor2.png");
    }

    private boolean surroundedWithJumpPoints(Position key, HashMap<Position, HashSet<Double>> watchedDictionary) {
        boolean ans = true;
        Position checkPosition = new Position(key.getY(), key.getX() - 1);
//        if (!prunnableVertices.containsKey(checkPosition)) {
        if (!prunnableVertices.containsKey(checkPosition) && watchedDictionary.containsKey(checkPosition)) {
            ans = false;
        }
        checkPosition = new Position(key.getY(), key.getX() + 1);
//        if (ans && !prunnableVertices.containsKey(checkPosition)) {
        if (ans && !prunnableVertices.containsKey(checkPosition) && watchedDictionary.containsKey(checkPosition)) {
            ans = false;
        }
        checkPosition = new Position(key.getY() - 1, key.getX());
//        if (ans && !prunnableVertices.containsKey(checkPosition)) {
        if (ans && !prunnableVertices.containsKey(checkPosition) && watchedDictionary.containsKey(checkPosition)) {
            ans = false;
        }
        checkPosition = new Position(key.getY() + 1, key.getX());
//        if (ans && !prunnableVertices.containsKey(checkPosition)) {
        if (ans && !prunnableVertices.containsKey(checkPosition) && watchedDictionary.containsKey(checkPosition)) {
            ans = false;
        }
        if (ans) {

            graph.removeVertex(prunnableVertices.get(key));
        }
        return ans;
    }


    public void pruneGraph() {
        for (PositionVertex prunedVertex : prunnableVertices.values()) {
            ArrayList<UndirectedWeightedEdge> edges = new ArrayList<>(graph.edgesOf(prunedVertex));
            for (UndirectedWeightedEdge edge1 : edges) {
                PositionVertex vertex1 = getOtherEdgeSide(edge1, prunedVertex);
                for (UndirectedWeightedEdge edge2 : edges) {
                    PositionVertex vertex2 = getOtherEdgeSide(edge2, prunedVertex);
                    if (vertex1.getPosition().equals(vertex2.getPosition())) continue;
                    edgeRemovalUpdate(vertex1, vertex2, edge1, edge2);
                }
            }
            graph.removeVertex(prunedVertex);
        }
    }

    private void edgeRemovalUpdate(PositionVertex vertex1, PositionVertex vertex2, UndirectedWeightedEdge edge1, UndirectedWeightedEdge edge2) {
        UndirectedWeightedEdge oldEdge = graph.getEdge(vertex1, vertex2);
        if (oldEdge == null) {
            UndirectedWeightedEdge newEdge = graph.addEdge(vertex1, vertex2);
            graph.setEdgeWeight(newEdge, edge1.getWeight() + edge2.getWeight());
        } else {
            graph.setEdgeWeight(oldEdge, Math.min(edge1.getWeight() + edge2.getWeight(), oldEdge.getWeight()));
        }
        graph.removeEdge(edge1);
        graph.removeEdge(edge2);
    }

    private PositionVertex getOtherEdgeSide(UndirectedWeightedEdge edge, PositionVertex value1) {
        return edge.getSource().equals(value1) ? edge.getTarget() : edge.getSource();
    }


    private void addAgentToGraph(RoomMapJumpState s) {
        Position agentPosition = s.getPosition();
//        ShortestPathAlgorithm.SingleSourcePaths<Position, UndirectedWeightedEdge> agentPaths=DistanceService.getPositionPaths(agentPosition);
        PositionVertex agentVertex = new PositionVertex(agentPosition, PositionVertex.TYPE.UNPRUNNABLE);
        graph.addVertex(agentVertex);
        for (Map.Entry<Position, PositionVertex> entry1 : prunnableVertices.entrySet()) {
            Position key1 = entry1.getKey();
            PositionVertex value1 = entry1.getValue();
            UndirectedWeightedEdge edge = graph.addEdge(agentVertex, value1);
            graph.setEdgeWeight(edge, DistanceService.getWeight(agentPosition, key1));
        }
    }

    private void connectPrunnableVerticesInGraph() {
        for (Map.Entry<Position, PositionVertex> entry1 : prunnableVertices.entrySet()) {
            Position key1 = entry1.getKey();
            PositionVertex value1 = entry1.getValue();
            for (Map.Entry<Position, PositionVertex> entry2 : prunnableVertices.entrySet()) {
                Position key2 = entry2.getKey();
                PositionVertex value2 = entry2.getValue();
                if (key1.equals(key2) || graph.containsEdge(value2, value1)) continue;
                UndirectedWeightedEdge edge = graph.addEdge(value1, value2);
                graph.setEdgeWeight(edge, DistanceService.getWeight(key1, key2));
            }
        }
    }

    private void addVerticesToGraph(TreeMap<Position, HashSet<Position>> watchedDictionary, HashMap<Position, HashSet<Double>> visualLineDictionary, RoomMapJumpState s, double threshold, int maxLeavesCount) {
        for (Map.Entry<Position, HashSet<Position>> entry : watchedDictionary.entrySet()) {
            Position key = entry.getKey();
            if (s.getSeen().contains(key)) continue;
            HashSet<Position> value = entry.getValue();
            double weight = 1.0 / visualLineDictionary.get(key).size();
            if (weight < threshold || maxLeavesCount <= 0 || prunnableVertices.size() == watchedDictionary.size())
                break;
            boolean skip = false;
            for (Position position : value) {
                if (prunnableVertices.containsKey(position)) {
                    skip = true;
                    break;
                }
            }
            if (skip) continue;
//            if (!s.getSeen().contains(key)) {
            if (!prunnableVertices.containsKey(key)) {
                maxLeavesCount--;
                PositionVertex watchedVertex = new PositionVertex(key, PositionVertex.TYPE.UNPRUNNABLE);
                unPrunnableVertices.put(key, watchedVertex);
                graph.addVertex(watchedVertex);
                for (Position position : value) {
                    if (unPrunnableVertices.containsKey(position)) continue;
                    PositionVertex vertex = new PositionVertex(position, PositionVertex.TYPE.PRUNEABLE);
                    prunnableVertices.put(position, vertex);
                    graph.addVertex(vertex);
                    UndirectedWeightedEdge edge = graph.addEdge(vertex, watchedVertex);
                    graph.setEdgeWeight(edge, 0);
                }
            }
        }
//        System.out.print("\r prunnable: "+prunnableVertices.size()+"\t unprunnable: "+unPrunnableVertices.size()+"\ttotal: "+watchedDictionary.size());
    }

    public Graph<PositionVertex, UndirectedWeightedEdge> getGraph() {
        return graph;
    }

    public HashMap<Position, PositionVertex> getPrunnableVertices() {
        return prunnableVertices;
    }

    public HashMap<Position, PositionVertex> getUnPrunnableVertices() {
        return unPrunnableVertices;
    }

    public double getPrimMSTWeight() {
        return getSpanningTree();
//        return new PrimMinimumSpanningTree<>(graph).getSpanningTree().getWeight();
    }

    public double getTSPWeight(Position startPosition) {
        PositionVertex start = new PositionVertex(new Position(), PositionVertex.TYPE.PRUNEABLE);
        graph.addVertex(start);
        for (PositionVertex vertex : graph.vertexSet()) {
            if (vertex.getPosition().equals(startPosition)) {
                UndirectedWeightedEdge edge = graph.addEdge(start, vertex);
                graph.setEdgeWeight(edge, 0);
                continue;
            }
            UndirectedWeightedEdge edge = graph.addEdge(start, vertex);
//            graph.setEdgeWeight(edge, 0);
            graph.setEdgeWeight(edge, HUGE_DOUBLE_VALUE);
        }
        graph.removeEdge(start, start);
        TwoOptHeuristicTSP<PositionVertex, UndirectedWeightedEdge> twoOptHeuristicTSP = new TwoOptHeuristicTSP<>();

//        return twoOptHeuristicTSP.getTour(graph).getWeight();
        return twoOptHeuristicTSP.getTour(graph).getWeight() - HUGE_DOUBLE_VALUE;

    }


    public double getSpanningTree() {
        Set<UndirectedWeightedEdge> minimumSpanningTreeEdgeSet = new HashSet<>(graph.vertexSet().size());
        double spanningTreeWeight = 0d;

        final int N = graph.vertexSet().size();

        /*
         * Normalize the graph by mapping each vertex to an integer.
         */
        VertexToIntegerMapping<PositionVertex> vertexToIntegerMapping = Graphs.getVertexToIntegerMapping(graph);
        Map<PositionVertex, Integer> vertexMap = vertexToIntegerMapping.getVertexMap();
        List<PositionVertex> indexList = vertexToIntegerMapping.getIndexList();

        VertexInfo[] vertices = (VertexInfo[]) Array.newInstance(VertexInfo.class, N);
        FibonacciHeapNode<VertexInfo>[] fibNodes =
                (FibonacciHeapNode<VertexInfo>[]) Array.newInstance(FibonacciHeapNode.class, N);
        FibonacciHeap<VertexInfo> fibonacciHeap = new FibonacciHeap<>();

        for (int i = 0; i < N; i++) {
            vertices[i] = new VertexInfo();
            vertices[i].id = i;
            vertices[i].distance = Double.MAX_VALUE;
            fibNodes[i] = new FibonacciHeapNode<>(vertices[i]);

            fibonacciHeap.insert(fibNodes[i], vertices[i].distance);
        }

        while (!fibonacciHeap.isEmpty()) {
            FibonacciHeapNode<VertexInfo> fibNode = fibonacciHeap.removeMin();
            VertexInfo vertexInfo = fibNode.getData();

            PositionVertex p = indexList.get(vertexInfo.id);
            vertexInfo.spanned = true;

            // Add the edge from its parent to the spanning tree (if it exists)
            if (vertexInfo.edgeFromParent != null) {
                minimumSpanningTreeEdgeSet.add(vertexInfo.edgeFromParent);
                spanningTreeWeight += graph.getEdgeWeight(vertexInfo.edgeFromParent);
            }

            // update all (unspanned) neighbors of p
            for (UndirectedWeightedEdge e : graph.edgesOf(p)) {
                PositionVertex q = Graphs.getOppositeVertex(graph, e, p);
                int id = vertexMap.get(q);

                // if the vertex is not explored and we found a better edge, then update the info
                if (!vertices[id].spanned) {
                    double cost = graph.getEdgeWeight(e);

                    if (cost < vertices[id].distance) {
                        vertices[id].distance = cost;
                        vertices[id].edgeFromParent = e;

                        fibonacciHeap.decreaseKey(fibNodes[id], cost);
                    }
                }
            }
        }
//        Graph<PositionVertex, UndirectedWeightedEdge> g = new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);
////        Graph<PositionVertex, UndirectedWeightedEdge> graph2 = graph;
////        graph = new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);
//        for (UndirectedWeightedEdge edge : minimumSpanningTreeEdgeSet) {
//            g.addVertex(edge.getSource());
//            g.addVertex(edge.getTarget());
//            g.addEdge(edge.getSource(),edge.getTarget());
//            g.setEdgeWeight(edge.getSource(),edge.getTarget(),graph.getEdge(edge.getSource(),edge.getTarget()).getWeight());
//        }
//        graph = g;
//        try {
////            File imgFile = new File("resources/graph.png");
//            File imgFile = new File("resources/MST.png");
//            imgFile.createNewFile();
//
//            JGraphXAdapter<PositionVertex, UndirectedWeightedEdge> graphAdapter =
//                    new JGraphXAdapter<PositionVertex, UndirectedWeightedEdge>(graph);
////            mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
//            mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
//            layout.setInterHierarchySpacing(layout.getInterHierarchySpacing() * 2);
//            layout.setInterRankCellSpacing(layout.getInterRankCellSpacing() * 2);
//            layout.execute(graphAdapter.getDefaultParent());
//            BufferedImage image =
//                    mxCellRenderer.createBufferedImage(graphAdapter, null, 5, Color.WHITE, true, null);
//            imgFile = new File("resources/MST.png");
//            ImageIO.write(image, "PNG", imgFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.exit(0);
        return spanningTreeWeight;
    }

    private class VertexInfo {
        public int id;
        public boolean spanned;
        public double distance;
        public UndirectedWeightedEdge edgeFromParent;
    }

    public void printGraph(String path) {
        try {
//            File imgFile = new File("resources/graph.png");
            File imgFile = new File(path);
            imgFile.createNewFile();
            JGraphXAdapter<PositionVertex, UndirectedWeightedEdge> graphAdapter =
                    new JGraphXAdapter<PositionVertex, UndirectedWeightedEdge>(graph);
            mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
//            mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
//            layout.setInterHierarchySpacing(layout.getInterHierarchySpacing() * 15);
//            layout.setInterRankCellSpacing(layout.getInterRankCellSpacing() * 15);
            layout.execute(graphAdapter.getDefaultParent());
            BufferedImage image =
                    mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
            imgFile = new File(path);
            ImageIO.write(image, "PNG", imgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}