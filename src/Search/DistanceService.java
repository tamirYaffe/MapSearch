package Search;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.TreeSingleSourcePathsImpl;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DistanceService {
    private static DijkstraShortestPath<Position, UndirectedWeightedEdge> dijkstraShortestPath;
    //    static BiconnectivityInspector biconnectivityInspector;
    private static Graph<Position, UndirectedWeightedEdge> pathsGraph = new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);
    private static Graph<Position, UndirectedWeightedEdge> graph = new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);
//    static HashSet<Position> cutPoints;


    public static double minDistance(HashSet<Position> positions, Position currPosition) {
        Position distantPosition = null;
        double minDistance = Double.MAX_VALUE;
        for (Position position : positions) {
            double distance = manhattanDistance(position, currPosition);
            if (distance < minDistance)
                minDistance = distance;
        }
        return minDistance;
    }

    public static double euclideanDistance(Position p1, Position p2) {
        return Math.sqrt(Math.pow(p1.getY() - p2.getY(), 2) + Math.pow(p1.getX() - p2.getX(), 2));
    }

    public static double manhattanDistance(Position p1, Position p2) {
        return Math.abs(p1.getY() - p2.getY()) + Math.abs(p1.getX() - p2.getX());
    }

    public static void setRoomMap(RoomMap roomMap) {
        Set<Position> verticesPositions = roomMap.getVisualDictionary().keySet();
        for (Position vertexPosition : verticesPositions) {
            graph.addVertex(vertexPosition);
            pathsGraph.addVertex(vertexPosition);
        }
        for (Position vertexPosition : verticesPositions) {
            addEdges(graph, vertexPosition, roomMap.getVisualDictionary());
        }
        dijkstraShortestPath = new DijkstraShortestPath<>(graph);
//        biconnectivityInspector= new BiconnectivityInspector(graph);
//        cutPoints=new HashSet<>(DistanceService.biconnectivityInspector.getCutpoints());
    }


    private static void addEdges(Graph<Position, UndirectedWeightedEdge> graph, Position vertexPosition, HashMap<Position, HashSet<Position>> verticesPositions) {
        Position moveTo = new Position(vertexPosition.getY() - 1, vertexPosition.getX());
        addEdgeIfValid(graph, vertexPosition, verticesPositions, moveTo);

        moveTo = new Position(vertexPosition.getY() + 1, vertexPosition.getX());
        addEdgeIfValid(graph, vertexPosition, verticesPositions, moveTo);

        moveTo = new Position(vertexPosition.getY(), vertexPosition.getX() - 1);
        addEdgeIfValid(graph, vertexPosition, verticesPositions, moveTo);

        moveTo = new Position(vertexPosition.getY(), vertexPosition.getX() + 1);
        addEdgeIfValid(graph, vertexPosition, verticesPositions, moveTo);

    }

    private static void addEdgeIfValid(Graph<Position, UndirectedWeightedEdge> graph, Position vertexPosition, HashMap<Position, HashSet<Position>> verticesPositions, Position moveTo) {
        if (verticesPositions.containsKey(moveTo)) {
            UndirectedWeightedEdge edge = graph.addEdge(vertexPosition, moveTo);
            if (edge != null)
                graph.setEdgeWeight(edge, 1);
        }
    }

    public static double getPathWeight(Position source, Position sink) {
        return dijkstraShortestPath.getPathWeight(source, sink);
    }

    public static GraphPath<Position, UndirectedWeightedEdge> minPath(Position source, Position sink) {
        return dijkstraShortestPath.getPath(source, sink);
    }

    public static ShortestPathAlgorithm.SingleSourcePaths<Position, UndirectedWeightedEdge> getPositionPaths(Position position) {
        return dijkstraShortestPath.getPaths(position);
    }

    public static double getWeight(Position current, Position other) {
        UndirectedWeightedEdge edge = pathsGraph.getEdge(current, other);
        if (edge == null) {
//            addEdgesToPathsGraphWithUpdates(getPositionPaths(current), current);
//            addEdgesToPathsGraph(getPositionPaths(current), current);
//            edge = pathsGraph.getEdge(current, other);
            edge = pathsGraph.addEdge(current,other);
            pathsGraph.setEdgeWeight(edge,getPathWeight(current,other));
        }
        return pathsGraph.getEdgeWeight(edge);
    }

    private static void addEdgesToPathsGraph(ShortestPathAlgorithm.SingleSourcePaths<Position, UndirectedWeightedEdge> positionPaths, Position position) {
        UndirectedWeightedEdge edge = null;
        for (Position vertex : pathsGraph.vertexSet()) {
            if (!pathsGraph.containsEdge(position, vertex)) {
                edge = pathsGraph.addEdge(position, vertex);
                pathsGraph.setEdgeWeight(edge, positionPaths.getWeight(vertex));
            }
        }
    }

    private static void addEdgesToPathsGraphWithUpdates(ShortestPathAlgorithm.SingleSourcePaths<Position, UndirectedWeightedEdge> positionPaths, Position current) {
        UndirectedWeightedEdge edge = null;
        Map<Position, Pair<Double, UndirectedWeightedEdge>> distsMap = ((TreeSingleSourcePathsImpl<Position, UndirectedWeightedEdge>) positionPaths).getDistanceAndPredecessorMap();
        for (Map.Entry<Position, Pair<Double, UndirectedWeightedEdge>> entry1 : distsMap.entrySet()) {
            Position key1 = entry1.getKey();
            if (key1.equals(current)) continue;
            Pair<Double, UndirectedWeightedEdge> value1 = entry1.getValue();
            if (!pathsGraph.containsEdge(current,key1))
                addPrevEdgesToGraph(distsMap, current,key1);
//                addSingleEdgeToGraph(positionPaths, key1, value1.getFirst());
        }
//        printGraph("paths1.png");
    }

    private static double addPrevEdgesToGraph(Map<Position, Pair<Double, UndirectedWeightedEdge>> map, Position prev, Position current) {
        if (map.get(current).getFirst()==0) return 0;
        else {
            UndirectedWeightedEdge edge = pathsGraph.addEdge(prev, current);
            if (edge==null)
                return pathsGraph.getEdge(prev,current).getWeight();
            else {
                Pair<Double, UndirectedWeightedEdge> predVal = map.get(current);
                Position pred = Graphs.getOppositeVertex(graph,predVal.getSecond(),current);
                UndirectedWeightedEdge stepEdge = pathsGraph.addEdge(pred, current);
                if (stepEdge!=null){
                    pathsGraph.setEdgeWeight(stepEdge,1);
                }
                double weight = addPrevEdgesToGraph(map,prev,pred)+1;
                pathsGraph.setEdgeWeight(edge,weight);
                return weight;
            }

        }
//        if (map.get(position).getFirst()==0)return 0;
//        else {
//            Position prev = map.get(position).getSecond().getSource().getPosition();
//            UndirectedWeightedEdge edge = pathsGraph.addEdge(prev, position);
//            if (edge!=null){
//                pathsGraph.setEdgeWeight(edge,1);
//            }
//            edge = pathsGraph.addEdge(prev, position);
//            if (edge!=null){
//                pathsGraph.setEdgeWeight(edge,1);
//            }
//            return addPrevEdgesToGraph(map,prev, current)+1;
//        }
    }

    private static void addSingleEdgeToGraph(ShortestPathAlgorithm.SingleSourcePaths<Position, UndirectedWeightedEdge> positionPaths, Position position, Double delta) {
        UndirectedWeightedEdge edge;
        for (Position vertex : pathsGraph.vertexSet()) {
            if (!pathsGraph.containsEdge(position, vertex)) {
                edge = pathsGraph.addEdge(position, vertex);
                double weight = positionPaths.getWeight(vertex);
                if (weight > delta) {
                    pathsGraph.setEdgeWeight(edge, weight - delta);
                }
            }
        }
    }

    public static void printGraph(String path) {
        try {
//            File imgFile = new File("resources/graph.png");
            File imgFile = new File(path);
            imgFile.createNewFile();
            JGraphXAdapter<Position, UndirectedWeightedEdge> graphAdapter =
                    new JGraphXAdapter<Position, UndirectedWeightedEdge>(pathsGraph);
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

    public static GraphPath<Position,UndirectedWeightedEdge> getPath(Position source, Position target) {
        GraphPath<Position, UndirectedWeightedEdge> path = dijkstraShortestPath.getPath(source, target);
        if (!pathsGraph.containsEdge(source, target))
            pathsGraph.setEdgeWeight(pathsGraph.addEdge(source,target),path.getWeight());
        return path;
    }
}
