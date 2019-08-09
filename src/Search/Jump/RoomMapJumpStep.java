package Search.Jump;

import Search.DistanceService;
import Search.IProblemMove;
import Search.Position;
import org.jgrapht.GraphPath;

import java.lang.reflect.Array;
import java.util.Arrays;

public class RoomMapJumpStep implements IProblemMove {
    final Position source;
    final Position target;
    Position[] path;

    public RoomMapJumpStep(Position source, Position target) {
        this.source = source;
        this.target = target;
        GraphPath path = DistanceService.minPath(source, target);
        this.path = new Position[(int) (1+path.getWeight())];
        int i=0;
        for (Object o : path.getVertexList()) {
            this.path[i++]=(Position)o;
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(path);
    }
}
