package Search.Jump;

import Search.DistanceService;
import Search.IProblemMove;
import Search.Position;
import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.Arrays;

public class RoomMapJumpStep implements IProblemMove {
    final Position source;
    final Position target;
    ArrayList<Position> path;

    public RoomMapJumpStep(Position source, Position target) {
        this.source = source;
        this.target = target;
        GraphPath<Position, Search.UndirectedWeightedEdge> path = DistanceService.minPath(source, target);
        this.path = new ArrayList<>(path.getVertexList());
//        int i=0;
//        for (Object o : path.getVertexList()) {
//            this.path[i++]=(Position)o;
//        }
    }

    public double getWeight(){
        return path.size()-1;
    }

    @Override
    public String toString() {
        return Arrays.toString(path.toArray());
    }
}
