package Search;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DistanceService {
    static DijkstraShortestPath dijkstraShortestPath;
    private static HashMap<Position,HashMap<Position,Double>> distances=new HashMap<>();

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
        Graph<Position, UndirectedWeightedEdge> graph= new DefaultUndirectedWeightedGraph<>(UndirectedWeightedEdge.class);
        Set<Position> verticesPositions= roomMap.getVisualDictionary().keySet();
        for (Position vertexPosition : verticesPositions) {
            graph.addVertex(vertexPosition);
        }
        for (Position vertexPosition : verticesPositions) {
           addEdges(graph,vertexPosition,roomMap.getVisualDictionary());
        }
        dijkstraShortestPath = new DijkstraShortestPath(graph);
        ShortestPathAlgorithm.SingleSourcePaths<Position, UndirectedWeightedEdge> paths=DistanceService.getPositionPaths(new Position(7,6));

        System.out.println(paths.getWeight(new Position(0,0)));
    }


    private static void addEdges(Graph<Position, UndirectedWeightedEdge> graph, Position vertexPosition, HashMap<Position, HashSet<Position>> verticesPositions) {
        Position moveTo=new Position(vertexPosition.getY()-1,vertexPosition.getX());
        addEdgeIfValid(graph,vertexPosition,verticesPositions,moveTo);

        moveTo=new Position(vertexPosition.getY()+1,vertexPosition.getX());
        addEdgeIfValid(graph,vertexPosition,verticesPositions,moveTo);

        moveTo=new Position(vertexPosition.getY(),vertexPosition.getX()-1);
        addEdgeIfValid(graph,vertexPosition,verticesPositions,moveTo);

        moveTo=new Position(vertexPosition.getY(),vertexPosition.getX()+1);
        addEdgeIfValid(graph,vertexPosition,verticesPositions,moveTo);

    }

    private static void addEdgeIfValid(Graph<Position, UndirectedWeightedEdge> graph, Position vertexPosition, HashMap<Position, HashSet<Position>> verticesPositions,Position moveTo){
        if(verticesPositions.containsKey(moveTo)){
            UndirectedWeightedEdge edge = graph.addEdge(vertexPosition, moveTo);
            if(edge!=null)
                graph.setEdgeWeight(edge, 1);
        }
    }

    public static double minPathWeight(Position source, Position sink){
        return dijkstraShortestPath.getPathWeight(source,sink);
    }

    public static ShortestPathAlgorithm.SingleSourcePaths<Position, UndirectedWeightedEdge> getPositionPaths(Position position){
        return dijkstraShortestPath.getPaths(position);
    }
}
