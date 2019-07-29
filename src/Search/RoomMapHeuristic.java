package Search;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.jgraph.*;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.jgrapht.util.SupplierUtil;
import com.mxgraph.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import static javafx.application.Platform.exit;

public class RoomMapHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof RoomMapState) {
            RoomMapState s = (RoomMapState) problemState;
            RoomMap r = (RoomMap) s.getProblem();
            double h = 0;
            TreeMap<Position, HashSet<Position>> watchedDictionary = r.getWatchedDictionary();
            HashMap<Position, HashSet<Position>> watchingDictionary = r.getVisualDictionary();
            Graph<PositionVertex, DefaultWeightedEdge> g = createGraph(watchedDictionary, watchingDictionary, s);
            try {
                printGraph(g);
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return h;
        } else return Double.MAX_VALUE / 2;
    }

    private void printGraph(Graph<PositionVertex, DefaultWeightedEdge> g) throws IOException {
        File imgFile = new File("resources/graph.png");
        imgFile.createNewFile();
        JGraphXAdapter<PositionVertex, DefaultWeightedEdge> graphAdapter =
                new JGraphXAdapter<PositionVertex, DefaultWeightedEdge>(g);
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
        layout.setInterHierarchySpacing(layout.getInterHierarchySpacing() * 15);
        layout.setInterRankCellSpacing(layout.getInterRankCellSpacing() * 15);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image =
                mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        imgFile = new File("resources/graph.png");
        ImageIO.write(image, "PNG", imgFile);

    }

    private Graph<PositionVertex, DefaultWeightedEdge> createGraph(TreeMap<Position, HashSet<Position>> watchedDictionary, HashMap<Position, HashSet<Position>> watchingDictionary, RoomMapState s) {


        // Create the graph object
        Graph<PositionVertex, DefaultWeightedEdge> g =
                new WeightedMultigraph<>(null, SupplierUtil.DEFAULT_WEIGHTED_EDGE_SUPPLIER);

        HashMap<Position, PositionVertex[]> vertices = new HashMap<>();
        vertices.put(s.getPosition(), new PositionVertex[2]);
        PositionVertex[] agentVertex = vertices.get(s.getPosition());
        agentVertex[0] = new PositionVertex(s.getPosition(), PositionVertex.TYPE.UNPRUNNABLE);
        g.addVertex(vertices.get(s.getPosition())[0]);
        int i = 0;
        for (Position position : watchedDictionary.keySet()) {
            if (s.getSeen().contains(position)) {
                HashSet<Position> leftovers = watchedDictionary.remove(position);
                continue;
            }
//            if (++i > 6) break;
            PositionVertex vertex = new PositionVertex(position, PositionVertex.TYPE.UNPRUNNABLE);
            if (!vertices.containsKey(position)) {
                vertices.put(position, new PositionVertex[2]);
                PositionVertex[] value = vertices.get(position);
                value[0] = vertex;
                g.addVertex(vertex);
            }
        }
        i = 0;
        for (Map.Entry<Position, HashSet<Position>> entry : watchedDictionary.entrySet()) {
//            if (i++ > 256) continue;
            Position key = entry.getKey();
            HashSet<Position> value = entry.getValue();
            for (Position position : value) {
                PositionVertex vertex = new PositionVertex(position, PositionVertex.TYPE.PRUNEABLE);
                if (key.equals(position)) continue;
                if (!vertices.containsKey(key)) {
                    vertices.put(key, new PositionVertex[2]);
                    vertices.get(key)[1] = vertex;
                    g.addVertex(vertex);
                }
                if (!vertices.containsKey(position)) {
                    vertices.put(position, new PositionVertex[2]);
                    vertices.get(position)[1] = vertex;
                    g.addVertex(vertex);
                }
                PositionVertex[] positionVertices = vertices.get(position);
                PositionVertex[] keyVertices = vertices.get(key);
                if (positionVertices[1] == null) {
                    positionVertices[1] = vertex;
                    g.addVertex(vertex);
                } else {
                    g.addVertex(vertex);
                }
                if (g.containsVertex(vertex) && g.containsVertex(keyVertices[0]) && !g.containsEdge(vertex, keyVertices[0])) {
                    DefaultWeightedEdge e = g.addEdge(vertex, keyVertices[0]);
                    g.setEdgeWeight(e, 0);
                }
                DefaultWeightedEdge e = g.addEdge(agentVertex[0], positionVertices[1]);
                g.setEdgeWeight(e, auclidianDistance(s.getPosition(), position));
            }
        }

        return g;
    }

    private double minDistance(HashSet<Position> positions, Position currPosition) {
        Position distantPosition = null;
        double minDistance = Double.MAX_VALUE;
        for (Position position : positions) {
            double distance = auclidianDistance(position, currPosition);
            if (distance < minDistance)
                minDistance = distance;
        }
        return minDistance;
    }

    private double auclidianDistance(Position p1, Position p2) {
        return Math.sqrt(Math.abs(p1.getY() - p2.getY()) + Math.abs(p1.getX() - p2.getX()));
    }
}
