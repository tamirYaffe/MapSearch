package Search;

import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class RoomMapJumpStep implements IProblemMove {
    private final Position source;
    private final Position target;
    private ArrayList<Position> path;
    private double pathCost;
    private HashSet<Position> seen;

    public RoomMapJumpStep(Position source, Position target) {
        this.source = source;
        this.target = target;
        PathFindingPath pathFind = DistanceService.getPathFind(source, target);
        this.path = new ArrayList<>(pathFind.getPath());
        this.pathCost = pathFind.getPathCost();
//        GraphPath<Position, Search.UndirectedWeightedEdge> path = DistanceService.minPath(source, target);
//        this.path = new ArrayList<>(path.getVertexList());
//        this.pathCost = this.path.size() - 1;
    }

    public RoomMapJumpStep(Position source, Position target, PathFindingPath pathFind) {
        this.source = source;
        this.target = target;
        this.path = new ArrayList<>(pathFind.getPath());
        this.pathCost = pathFind.getPathCost();
    }

    public RoomMapJumpStep(Position source, Position target, AStarAllPaths.PathFindingSeen findingPathSeen) {
        this(source, target, findingPathSeen.getPathFindingPath());
        this.seen = findingPathSeen.getSeen();
    }

    public ArrayList<Position> getPath() {
        return path;
    }

    public HashSet<Position> getSeen() {
        return seen;
    }

    public void setSeen(HashSet<Position> seen) {
        this.seen = seen;
    }

    @Override
    public double getCost() {
        return pathCost;
    }

    @Override
    public Position getNewPosition(int x, int y) {
        if (source.equals(x, y))
            return target;
        return null;
    }

    @Override
    public Position getNewPosition(Position newPosition) {
        return getNewPosition(newPosition.getX(), newPosition.getY());
    }

    @Override
    public String toString() {
        return Arrays.toString(path.toArray());
    }
}
