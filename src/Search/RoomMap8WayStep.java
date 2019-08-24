package Search;

public class RoomMap8WayStep extends RoomStep implements IProblemMove {
    public static final double SQRT_OF_TWO = Math.sqrt(2);


    public RoomMap8WayStep(SIMPLE_MOVE move) {
        super(move);
    }

    public RoomMap8WayStep(Position source, Position target) {
        super(source, target);
        if (_move == null) {
            int dx = target.getX() - source.getX();
            int dy = target.getY() - source.getY();
            if (dy == -1 && dx == 1) _move = SIMPLE_MOVE.UP_RIGHT;
            else if (dy == -1 && dx == -1) _move = SIMPLE_MOVE.UP_LEFT;
            else if (dy == 1 && dx == 1) _move = SIMPLE_MOVE.DOWN_RIGHT;
            else if (dy == 1 && dx == -1) _move = SIMPLE_MOVE.DOWN_LEFT;
        }
    }


    @Override
    public Position getNewPosition(int x, int y) {
        Position newPosition = super.getNewPosition(x, y);
        if (newPosition == null) switch (_move) {
            case UP_RIGHT:
                return new Position(y - 1, x + 1);
            case UP_LEFT:
                return new Position(y - 1, x - 1);
            case DOWN_RIGHT:
                return new Position(y + 1, x + 1);
            case DOWN_LEFT:
                return new Position(y + 1, x - 1);
        }
        return newPosition;
    }

    @Override
    public double getCost() {
        return super.toString() == null ? SQRT_OF_TWO : 1;
    }

    @Override
    public Position getNewPosition(Position newPosition) {
        return getNewPosition(newPosition.getX(), newPosition.getY());
    }

    @Override
    public String toString() {
        String s = super.toString();
        if (s == null) switch (_move) {
            case UP_RIGHT:
                return "UP_RIGHT";
            case UP_LEFT:
                return "UP_LEFT";
            case DOWN_RIGHT:
                return "DOWN_RIGHT";
            case DOWN_LEFT:
                return "DOWN_LEFT";
        }
        return null;
    }
}
