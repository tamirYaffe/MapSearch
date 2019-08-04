package Search;

import org.jgrapht.Graph;
import org.jgrapht.alg.BlockCutpointGraph;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.TreeSingleSourcePathsImpl;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DistanceService {
    static DijkstraShortestPath dijkstraShortestPath;
    static BiconnectivityInspector biconnectivityInspector;
    static Graph<Position, UndirectedWeightedEdge> pathsGraph = new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);
    static Graph<Position, UndirectedWeightedEdge> graph;
    static HashSet<Position> cutPoints;


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
        graph= new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);
        Set<Position> verticesPositions = roomMap.getVisualDictionary().keySet();
        for (Position vertexPosition : verticesPositions) {
            graph.addVertex(vertexPosition);
            pathsGraph.addVertex(vertexPosition);
        }
        for (Position vertexPosition : verticesPositions) {
            addEdges(graph, vertexPosition, roomMap.getVisualDictionary());
        }
        dijkstraShortestPath = new DijkstraShortestPath(graph);
        biconnectivityInspector= new BiconnectivityInspector(graph);
        cutPoints=new HashSet<>(DistanceService.biconnectivityInspector.getCutpoints());
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

    public static double minPathWeight(Position source, Position sink) {
        return dijkstraShortestPath.getPathWeight(source, sink);
    }

    public static ShortestPathAlgorithm.SingleSourcePaths<Position, UndirectedWeightedEdge> getPositionPaths(Position position) {
        return dijkstraShortestPath.getPaths(position);
    }

    public static double getWeight(Position current, Position other) {
        UndirectedWeightedEdge edge = pathsGraph.getEdge(current, other);
        if (edge == null) {
//            addEdgesToPathsGraph(getPositionPaths(current));
            addEdgesToPathsGraph(getPositionPaths(current), current);
            edge = pathsGraph.getEdge(current, other);
        }
        return pathsGraph.getEdgeWeight(edge);
    }

    private static void addEdgesToPathsGraph(ShortestPathAlgorithm.SingleSourcePaths<Position, UndirectedWeightedEdge> positionPaths, Position position) {
        UndirectedWeightedEdge edge;
        for (Position vertex : pathsGraph.vertexSet()) {
            if (!pathsGraph.containsEdge(position, vertex)) {
                edge = pathsGraph.addEdge(position, vertex);
                pathsGraph.setEdgeWeight(edge, positionPaths.getWeight(vertex));
            }
        }
    }

    private static void addEdgesToPathsGraph(ShortestPathAlgorithm.SingleSourcePaths<Position, UndirectedWeightedEdge> positionPaths) {
        UndirectedWeightedEdge edge;
        for (Map.Entry<Position, Pair<Double, UndirectedWeightedEdge>> entry1 : ((TreeSingleSourcePathsImpl<Position, UndirectedWeightedEdge>) positionPaths).getDistanceAndPredecessorMap().entrySet()) {
            Position key1 = entry1.getKey();
            Pair<Double, UndirectedWeightedEdge> value1 = entry1.getValue();
            if (value1 != null)
                addSingleEdgeToGraph(positionPaths, key1, value1.getFirst());
        }
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
}
