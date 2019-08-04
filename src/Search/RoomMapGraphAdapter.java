package Search;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import javafx.util.Pair;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.alg.tour.TwoOptHeuristicTSP;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.util.*;
import rlforj.examples.ExampleBoard;
import rlforj.los.BresLos;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import static Search.DistanceService.manhattanDistance;
import static Search.DistanceService.minPathWeight;

public class RoomMapGraphAdapter {
    private Graph<PositionVertex, UndirectedWeightedEdge> graph;
    private HashMap<Position, PositionVertex> prunnableVertices;
    private HashMap<Position, PositionVertex> unPrunnableVertices;

    public RoomMapGraphAdapter(TreeMap<Position, HashSet<Position>> watchedDictionary, HashMap<Position, HashSet<Double>> visualLineDictionary, RoomMapState s, double threshold, int maxLeavesCount) {
        graph = new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);
        prunnableVertices = new HashMap<>();
        unPrunnableVertices = new HashMap<>();
        addVerticesToGraph(watchedDictionary, visualLineDictionary, s, threshold, maxLeavesCount);
        connectPrunnableVerticesInGraph();
        addAgentToGraph(s);
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


    private void addAgentToGraph(RoomMapState s) {
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
//            ShortestPathAlgorithm.SingleSourcePaths<Position, UndirectedWeightedEdge> key1Paths=DistanceService.getPositionPaths(key1);
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

    private void addVerticesToGraph(TreeMap<Position, HashSet<Position>> watchedDictionary, HashMap<Position, HashSet<Double>> visualLineDictionary, RoomMapState s, double threshold, int maxLeavesCount) {
        for (Map.Entry<Position, HashSet<Position>> entry : watchedDictionary.entrySet()) {
            Position key = entry.getKey();
            HashSet<Position> value = entry.getValue();
            double weight = 1.0 / visualLineDictionary.get(key).size();
//            double weight=1.0/value.size();
//            double weight = 7.0 / visualLineDictionary.get(key).size() + 0.3 / value.size();
            if (weight < threshold || maxLeavesCount <= 0) break;
//            System.out.println(weight);
//            if (!s.getSeen().contains(key) && !closeToRarePoint(key)) {
            if (!s.getSeen().contains(key)) {
                maxLeavesCount--;
                PositionVertex watchedVertex = new PositionVertex(key, PositionVertex.TYPE.UNPRUNNABLE);
                unPrunnableVertices.put(key, watchedVertex);
                graph.addVertex(watchedVertex);
                for (Position position : value) {
                    if (position.equals(key)) continue;
                    PositionVertex vertex = new PositionVertex(position, PositionVertex.TYPE.PRUNEABLE);
                    prunnableVertices.put(position, vertex);
                    graph.addVertex(vertex);
                    UndirectedWeightedEdge edge = graph.addEdge(vertex, watchedVertex);
                    graph.setEdgeWeight(edge, 0);
                }
            }
        }
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
            UndirectedWeightedEdge edge = graph.addEdge(start, vertex);
            graph.setEdgeWeight(edge, 0);
        }
        graph.removeEdge(start, start);
        TwoOptHeuristicTSP<PositionVertex, UndirectedWeightedEdge> twoOptHeuristicTSP = new TwoOptHeuristicTSP<>();

        return twoOptHeuristicTSP.getTour(graph).getWeight();

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
