package Search;

import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.Arrays;

public class RoomMapJumpStep implements IProblemMove {
    private final Position source;
    private final Position target;
    private ArrayList<Position> path;

    public ArrayList<Position> getPath() {
        return path;
    }

    public RoomMapJumpStep(Position source, Position target) {
        this.source = source;
        this.target = target;
        GraphPath<Position, Search.UndirectedWeightedEdge> path = DistanceService.getPath(source, target);
        this.path = new ArrayList<>(path.getVertexList());
    }

    @Override
    public double getCost() {
        return path.size() - 1;
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
