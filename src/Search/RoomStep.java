package Search;

public class RoomStep implements IProblemMove {
    public enum MOVE {UP, DOWN, LEFT, RIGHT, UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT}

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
        if (_move == MOVE.UP_RIGHT)
            return "UP_RIGHT";
        if (_move == MOVE.UP_LEFT)
            return "UP_LEFT";
        if (_move == MOVE.DOWN_RIGHT)
            return "DOWN_RIGHT";
        if (_move == MOVE.DOWN_LEFT)
            return "DOWN_LEFT";
        return null;
    }
}
