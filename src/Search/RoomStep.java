package Search;

public class RoomStep implements IProblemMove {
    public enum MOVE {UP, DOWN, LEFT, RIGHT}

    MOVE _move;

    public RoomStep
            (
                    MOVE move
            ) {
        _move = move;
    }

    @Override
    public String toString() {
        if (_move == MOVE.UP)
            return "UP";
        if (_move == MOVE.DOWN)
            return "DOWN";
        if (_move == MOVE.LEFT)
            return "LEFT";
        if (_move == MOVE.RIGHT)
            return "RIGHT";
        return null;
    }
}
