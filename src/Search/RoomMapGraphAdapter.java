package Search;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.alg.tour.TwoOptHeuristicTSP;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import rlforj.examples.ExampleBoard;
import rlforj.los.BresLos;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

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
        addVerticesToGraph(watchedDictionary,visualLineDictionary, s, threshold, maxLeavesCount);
        connectPrunnableVerticesInGraph();
        addAgentToGraph(s);
    }

//    public Graph<PositionVertex, UndirectedWeightedEdge> createGraph(TreeMap<Position, HashSet<Position>> watchedDictionary, RoomMapState s, double threshold) {
//        // Create the graph object
//        Graph<PositionVertex, UndirectedWeightedEdge> g = new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);
//        addVerticesToGraph(watchedDictionary, prunnableVertices, unPrunnableVertices, s, threshold);
//        connectPrunnableVerticesInGraph(prunnableVertices);
//        addAgentToGraph(prunnableVertices, s);
//        return g;
//    }

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
        ShortestPathAlgorithm.SingleSourcePaths<Position, UndirectedWeightedEdge> agentPaths=DistanceService.getPositionPaths(agentPosition);
        PositionVertex agentVertex = new PositionVertex(agentPosition, PositionVertex.TYPE.UNPRUNNABLE);
        graph.addVertex(agentVertex);
        for (Map.Entry<Position, PositionVertex> entry1 : prunnableVertices.entrySet()) {
            Position key1 = entry1.getKey();
            PositionVertex value1 = entry1.getValue();
            UndirectedWeightedEdge edge = graph.addEdge(agentVertex, value1);
            graph.setEdgeWeight(edge, agentPaths.getWeight(key1));
        }
    }

    private void connectPrunnableVerticesInGraph() {
        for (Map.Entry<Position, PositionVertex> entry1 : prunnableVertices.entrySet()) {
            Position key1 = entry1.getKey();
            ShortestPathAlgorithm.SingleSourcePaths<Position, UndirectedWeightedEdge> key1Paths=DistanceService.getPositionPaths(key1);
            PositionVertex value1 = entry1.getValue();
            for (Map.Entry<Position, PositionVertex> entry2 : prunnableVertices.entrySet()) {
                Position key2 = entry2.getKey();
                PositionVertex value2 = entry2.getValue();
                if (key1.equals(key2) || graph.containsEdge(value2, value1)) continue;
                UndirectedWeightedEdge edge = graph.addEdge(value1, value2);
                graph.setEdgeWeight(edge, key1Paths.getWeight(key2));
            }
        }
    }

    private void addVerticesToGraph(TreeMap<Position, HashSet<Position>> watchedDictionary, HashMap<Position, HashSet<Double>> visualLineDictionary, RoomMapState s, double threshold, int maxLeavesCount) {
        for (Map.Entry<Position, HashSet<Position>> entry : watchedDictionary.entrySet()) {
            Position key = entry.getKey();
            HashSet<Position> value = entry.getValue();
//            double weight=1.0/visualLineDictionary.get(key).size();
//            double weight=1.0/value.size();
            double weight=1.0/visualLineDictionary.get(key).size()+1.5/value.size();
            if (weight< threshold || maxLeavesCount <= 0) break;
//            System.out.println(weight);
//            if (!s.getSeen().contains(key) && !closeToRarePoint(key)) {
            if (!s.getSeen().contains(key)) {
                maxLeavesCount--;
//                System.out.println(key);
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

    private boolean closeToRarePoint(Position key) {
        BresLos a = new BresLos(true);
        ExampleBoard b=RoomMapService.b;
        for (Position position : unPrunnableVertices.keySet()) {
//            if ((a.existsLineOfSight(b, position.getX(), position.getY(), key.getX(), key.getY(), true)) || DistanceService.euclideanDistance(position,key)<=3)
            if ((a.existsLineOfSight(b, position.getX(), position.getY(), key.getX(), key.getY(), true)))
                    return true;
        }
        return false;
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
        return new PrimMinimumSpanningTree<>(graph).getSpanningTree().getWeight();
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

    public int getMinDistance(Position position) {
        int minWeight=Integer.MAX_VALUE;
        for (PositionVertex vertex : graph.vertexSet()) {

        }
        return 0;
    }

    public void makeComplete() {
        for (PositionVertex positionVertex : graph.vertexSet()) {
            for (PositionVertex vertex : graph.vertexSet()) {
                if(positionVertex.equals(vertex))continue;
                if(graph.getEdge(positionVertex,vertex)==null){
                    UndirectedWeightedEdge edge = graph.addEdge(positionVertex, vertex);
                    graph.setEdgeWeight(edge, Integer.MAX_VALUE);
                }
            }
        }
    }
}
