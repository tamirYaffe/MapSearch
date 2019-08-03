package Search;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import javafx.util.Pair;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.alg.tour.TwoOptHeuristicTSP;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.Multigraph;
import rlforj.examples.ExampleBoard;
import rlforj.los.BresLos;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class RoomMapGraphAdapter {
    private Graph<PositionVertex, UndirectedWeightedEdge> graph;
    private List<Set<PositionVertex>> connectedComponents;
    private List<SCCSubGraph> connectedComponentsGraphs = new ArrayList<>();
    private HashMap<Position, PositionVertex> prunnableVertices;
    private HashMap<Position, PositionVertex> unPrunnableVertices;

    public RoomMapGraphAdapter(TreeMap<Position, HashSet<Position>> watchedDictionary, HashMap<Position, HashSet<Double>> visualLineDictionary, RoomMapState s, double threshold, int maxLeavesCount) {
        graph = new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);
        prunnableVertices = new HashMap<>();
        unPrunnableVertices = new HashMap<>();
        addVerticesToGraph(watchedDictionary, visualLineDictionary, s, threshold, maxLeavesCount);
//        connectPrunnableVerticesInGraph();
//        addAgentToGraph(s);
    }

    public RoomMapGraphAdapter(TreeMap<Position, HashSet<Position>> watchedDictionary, HashMap<Position, HashSet<Double>> visualLineDictionary, RoomMapState s, double threshold, int maxLeavesCount, boolean newGraph) {
        graph = new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);
        prunnableVertices = new HashMap<>();
        unPrunnableVertices = new HashMap<>();
        if (newGraph) {
            addVerticesToGraph(watchedDictionary, visualLineDictionary, s, threshold, maxLeavesCount);
        } else {
            addVerticesToGraph(watchedDictionary, visualLineDictionary, s, threshold, maxLeavesCount);
            connectPrunnableVerticesInGraph();
            addAgentToGraph(s);
        }
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
            graph.setEdgeWeight(edge, DistanceService.getWeight(agentPosition,key1));
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
                graph.setEdgeWeight(edge, DistanceService.getWeight(key1,key2));
            }
        }
    }

    private void addVerticesToGraph(TreeMap<Position, HashSet<Position>> watchedDictionary, HashMap<Position, HashSet<Double>> visualLineDictionary, RoomMapState s, double threshold, int maxLeavesCount) {
        for (Map.Entry<Position, HashSet<Position>> entry : watchedDictionary.entrySet()) {
            Position key = entry.getKey();
            HashSet<Position> value = entry.getValue();
            double weight=1.0/visualLineDictionary.get(key).size();
//            double weight=1.0/value.size();
//            double weight = 7.0 / visualLineDictionary.get(key).size() + 0.3 / value.size();
            if (weight < threshold || maxLeavesCount <= 0) break;
            if (!s.getSeen().contains(key) && !closeToRarePoint(key)) {
//                System.out.println(weight);
                maxLeavesCount--;
                PositionVertex watchedVertex = new PositionVertex(key, PositionVertex.TYPE.UNPRUNNABLE);
                unPrunnableVertices.put(key, watchedVertex);
//                graph.addVertex(watchedVertex);
                SCCSubGraph subGraph = new SCCSubGraph(UndirectedWeightedEdge.class);
                subGraph.addVertex(watchedVertex);
                subGraph.setRarePoint(watchedVertex.getPosition());
                for (Position position : value) {
                    if (position.equals(key)) continue;
                    PositionVertex vertex = new PositionVertex(position, PositionVertex.TYPE.PRUNEABLE);
                    prunnableVertices.put(position, vertex);

//                    graph.addVertex(vertex);
//                    UndirectedWeightedEdge edge = graph.addEdge(vertex, watchedVertex);
//                    graph.setEdgeWeight(edge, 0);

                    subGraph.addVertex(vertex);
                    UndirectedWeightedEdge edge = subGraph.addEdge(vertex, watchedVertex);
                    subGraph.setEdgeWeight(edge,0);
                }
                if(!subGraph.vertexSet().isEmpty())
                    connectedComponentsGraphs.add(subGraph);
            }
        }
    }

    private boolean closeToRarePoint(Position key) {
        BresLos a = new BresLos(true);
        ExampleBoard b = RoomMapService.b;
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
    public double getUnseenSCCWeight(RoomMapState s) {
        double minH=Double.MAX_VALUE;
        ArrayList<Integer> a = new ArrayList<> ();
        for (int i = 0; i < connectedComponentsGraphs.size(); i ++){
            a.add(i);
        }
        for (ArrayList<Integer> integers : Combination.choose(a, connectedComponentsGraphs.size())) {
//            System.out.println("path :"+ integers);
            Position current=s.getPosition();
            double h=0;
            for (Integer integer : integers) {
                Pair<Position,Double>ans=getMinEdge(current,connectedComponentsGraphs.get(integer));
                h+=ans.getValue();
                current=ans.getKey();
            }
//            System.out.println("H: "+h);
            if(h<minH)
                minH=h;
        }
//        }
        return minH;
    }

    private Pair<Position,Double> getMinEdge(Position current, SCCSubGraph subGraph) {
        double minEdgeWeight=Double.MAX_VALUE;
        Position next=null;
        for (PositionVertex vertex : subGraph.vertexSet()) {
            if(vertex.getType()== PositionVertex.TYPE.UNPRUNNABLE)continue;
            double edgeWeight=DistanceService.getWeight(current,vertex.getPosition());
            if(edgeWeight<minEdgeWeight){
                minEdgeWeight=edgeWeight;
                next=vertex.getPosition();
            }
        }
//        System.out.println("next: "+next+" weight: "+minEdgeWeight);
        return new Pair<>(next,minEdgeWeight);
    }
}
