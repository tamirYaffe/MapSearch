package Search;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MinimumSpanningTree;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.util.SupplierUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class RoomMapHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof RoomMapState) {
            RoomMapState s = (RoomMapState) problemState;
            RoomMap r = (RoomMap) s.getProblem();
            double h = 0;
            TreeMap<Position, HashSet<Position>> watchedDictionary = r.getWatchedDictionary();
            HashMap<Position, HashSet<Position>> watchingDictionary = r.getVisualDictionary();
//            double start = System.nanoTime();
            Graph<PositionVertex, UndirectedWeightedEdge> g = createGraph(watchedDictionary, s);
            PrimMinimumSpanningTree<PositionVertex, UndirectedWeightedEdge> primMinimumSpanningTree = new PrimMinimumSpanningTree<>(g);
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

    private void printGraph(Graph<PositionVertex, UndirectedWeightedEdge> g, HashMap<Position, PositionVertex> prunnableVertices, HashMap<Position, PositionVertex> unPrunnableVertices) {
        try {

            File imgFile = new File("resources/graph.png");
            imgFile.createNewFile();
            JGraphXAdapter<PositionVertex, UndirectedWeightedEdge> graphAdapter =
                    new JGraphXAdapter<PositionVertex, UndirectedWeightedEdge>(g);
//        mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
            mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
//        layout.setInterHierarchySpacing(layout.getInterHierarchySpacing() * 15);
//        layout.setInterRankCellSpacing(layout.getInterRankCellSpacing() * 15);
            layout.execute(graphAdapter.getDefaultParent());


            BufferedImage image =
                    mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
            imgFile = new File("resources/graph.png");
            ImageIO.write(image, "PNG", imgFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    private Graph<PositionVertex, UndirectedWeightedEdge> createGraph(TreeMap<Position, HashSet<Position>> watchedDictionary, RoomMapState s) {
        // Create the graph object
        Graph<PositionVertex, UndirectedWeightedEdge> g =
                new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);

        HashMap<Position, PositionVertex> prunnableVertices = new HashMap<>();
        HashMap<Position, PositionVertex> unPrunnableVertices = new HashMap<>();
        double threshold = 0.2;
        addVerticesToGraph(g, watchedDictionary, prunnableVertices, unPrunnableVertices, s, threshold);
        connectPrunnableVerticesInGraph(g, prunnableVertices);
        addAgentToGraph(g, prunnableVertices, s);
        //prune phase
        pruneGraph(g, prunnableVertices);
//        printGraph(g, prunnableVertices, unPrunnableVertices);
        return g;
    }

    private void pruneGraph(Graph<PositionVertex, UndirectedWeightedEdge> g, HashMap<Position, PositionVertex> prunnableVertices) {
        for (PositionVertex prunedVertex : prunnableVertices.values()) {
            ArrayList<UndirectedWeightedEdge> edges = new ArrayList<>(g.edgesOf(prunedVertex));
            for (UndirectedWeightedEdge edge1 : edges) {
                PositionVertex vertex1 = getOtherEdgeSide(edge1, prunedVertex);
                for (UndirectedWeightedEdge edge2 : edges) {
                    PositionVertex vertex2 = getOtherEdgeSide(edge2, prunedVertex);
                    if (vertex1.getPosition().equals(vertex2.getPosition())) continue;
                    edgeRemovalUpdate(g, prunedVertex, vertex1, vertex2, edge1, edge2);
                }
            }
            g.removeVertex(prunedVertex);
        }
    }

    private void edgeRemovalUpdate(Graph<PositionVertex, UndirectedWeightedEdge> g, PositionVertex prunedVertex, PositionVertex vertex1, PositionVertex vertex2, UndirectedWeightedEdge edge1, UndirectedWeightedEdge edge2) {
        UndirectedWeightedEdge oldEdge = g.getEdge(vertex1, vertex2);
        if (oldEdge == null) {
            UndirectedWeightedEdge newEdge = g.addEdge(vertex1, vertex2);
            g.setEdgeWeight(newEdge, edge1.getWeight() + edge2.getWeight());
        } else {
            g.setEdgeWeight(oldEdge, Math.min(edge1.getWeight() + edge2.getWeight(), oldEdge.getWeight()));
        }
        g.removeEdge(edge1);
        g.removeEdge(edge2);
    }

    private PositionVertex getOtherEdgeSide(UndirectedWeightedEdge edge, PositionVertex value1) {
        return edge.getSource().equals(value1) ? edge.getTarget() : edge.getSource();
    }


    private void addAgentToGraph(Graph<PositionVertex, UndirectedWeightedEdge> g, HashMap<Position, PositionVertex> vertices, RoomMapState s) {
        Position agentPosition = s.getPosition();
        PositionVertex agentVertex = new PositionVertex(agentPosition, PositionVertex.TYPE.UNPRUNNABLE);
        g.addVertex(agentVertex);
        for (Map.Entry<Position, PositionVertex> entry1 : vertices.entrySet()) {
            Position key1 = entry1.getKey();
            PositionVertex value1 = entry1.getValue();
            UndirectedWeightedEdge edge = g.addEdge(agentVertex, value1);
            g.setEdgeWeight(edge, euclideanDistance(key1, agentPosition));
        }
    }

    private void connectPrunnableVerticesInGraph(Graph<PositionVertex, UndirectedWeightedEdge> g, HashMap<Position, PositionVertex> vertices) {
        for (Map.Entry<Position, PositionVertex> entry1 : vertices.entrySet()) {
            Position key1 = entry1.getKey();
            PositionVertex value1 = entry1.getValue();
            for (Map.Entry<Position, PositionVertex> entry2 : vertices.entrySet()) {
                Position key2 = entry2.getKey();
                PositionVertex value2 = entry2.getValue();
                if (key1.equals(key2) || g.containsEdge(value2, value1)) continue;
                UndirectedWeightedEdge edge = g.addEdge(value1, value2);
                g.setEdgeWeight(edge, euclideanDistance(key1, key2));
            }
        }
    }

    private void addVerticesToGraph(Graph<PositionVertex, UndirectedWeightedEdge> g, TreeMap<Position, HashSet<Position>> watchedDictionary, HashMap<Position, PositionVertex> vertices, HashMap<Position, PositionVertex> unPrunnableVertices, RoomMapState s, double threshold) {
        for (Map.Entry<Position, HashSet<Position>> entry : watchedDictionary.entrySet()) {
            Position key = entry.getKey();
            HashSet<Position> value = entry.getValue();
            if ((1.0 / value.size()) < threshold) break;
            if (!s.getSeen().contains(key)) {
                PositionVertex watchedVertex = new PositionVertex(key, PositionVertex.TYPE.UNPRUNNABLE);
                unPrunnableVertices.put(key, watchedVertex);
                g.addVertex(watchedVertex);
                for (Position position : value) {
                    if (position.equals(key)) continue;
                    PositionVertex vertex = new PositionVertex(position, PositionVertex.TYPE.PRUNEABLE);
                    vertices.put(position, vertex);
                    g.addVertex(vertex);
                    UndirectedWeightedEdge edge = g.addEdge(vertex, watchedVertex);
                    g.setEdgeWeight(edge, 0);
                }
            }
        }
    }

    private double minDistance(HashSet<Position> positions, Position currPosition) {
        Position distantPosition = null;
        double minDistance = Double.MAX_VALUE;
        for (Position position : positions) {
            double distance = euclideanDistance(position, currPosition);
            if (distance < minDistance)
                minDistance = distance;
        }
        return minDistance;
    }

    private double euclideanDistance(Position p1, Position p2) {
        return Math.sqrt(Math.abs(p1.getY() - p2.getY()) + Math.abs(p1.getX() - p2.getX()));
    }
}
