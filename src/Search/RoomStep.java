package Search;

public class RoomStep implements IProblemMove {


    public enum SIMPLE_MOVE {UP, DOWN, LEFT, RIGHT, UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT}

    public SIMPLE_MOVE _move;

    public RoomStep(SIMPLE_MOVE move) {
        _move = move;
    }

    public RoomStep(Position source, Position target) {
        if (DistanceService.manhattanDistance(source, target) > 1)
            _move = null;
        else {
            int dx = target.getX() - source.getX();
            int dy = target.getY() - source.getY();
            if (dx == 1) _move = SIMPLE_MOVE.RIGHT;
            else if (dx == -1) _move = SIMPLE_MOVE.LEFT;
            else if (dy == 1) _move = SIMPLE_MOVE.DOWN;
            else if (dy == -1) _move = SIMPLE_MOVE.UP;
        }
    }

    @Override
    public Position getNewPosition(int x, int y) {
        switch (_move) {
            case UP:
                return new Position(y - 1, x);
            case DOWN:
                return new Position(y + 1, x);
            case LEFT:
                return new Position(y, x - 1);
            case RIGHT:
                return new Position(y, x + 1);
        }
        return null;
    }

    @Override
    public Position getNewPosition(Position newPosition) {
        return getNewPosition(newPosition.getX(), newPosition.getY());
    }


    @Override
    public double getCost() {
        return 1;
    }

    @Override
    public String toString() {
        switch (_move) {
            case UP:
                return "UP";
            case DOWN:
                return "DOWN";
            case LEFT:
                return "LEFT";
            case RIGHT:
                return "RIGHT";
        }
        return null;
    }


}
