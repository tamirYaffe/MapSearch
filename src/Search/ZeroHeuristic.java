package Search;

import Search.Jump.RoomMapJumpGraphAdapter;
import Search.Jump.RoomMapJumpState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class ZeroHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        return 0;
    }
}
