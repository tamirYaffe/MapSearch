package Search;

import com.sun.xml.internal.ws.dump.LoggingDumpTube;

public class PathFindingMove implements IProblemMove {
    public enum MOVE {UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT}

    private MOVE move;
    private static double sqrt2 = Math.sqrt(2);

    public PathFindingMove(MOVE move) {
        this.move = move;
    }

    @Override
    public Position getNewPosition(int x, int y) {
        switch (move) {
            case UP:
                return new Position(y-1,x);
            case DOWN:
                return new Position(y+1,x);
            case LEFT:
                return new Position(y,x-1);
            case RIGHT:
                return new Position(y, x+1);
            case UP_LEFT:
                return new Position(y-1,x-1);
            case UP_RIGHT:
                return new Position(y-1,x+1);
            case DOWN_LEFT:
                return new Position(y+1,x-1);
            case DOWN_RIGHT:
                return new Position(y+1,x+1);
        }
        return null;
    }

    @Override
    public Position getNewPosition(Position newPosition) {
        switch (move){
            case UP:
                return new Position(newPosition.getY()-1,newPosition.getX());
            case DOWN:
                return new Position(newPosition.getY()+1, newPosition.getX());
            case LEFT:
                return new Position(newPosition.getY(), newPosition.getX()-1);
            case RIGHT:
                return new Position(newPosition.getY(), newPosition.getX()+1);
            case UP_LEFT:
                return new Position(newPosition.getY()-1, newPosition.getX()-1);
            case UP_RIGHT:
                return new Position(newPosition.getY()-1, newPosition.getX()+1);
            case DOWN_LEFT:
                return new Position(newPosition.getY()+1, newPosition.getX()-1);
            case DOWN_RIGHT:
                return new Position(newPosition.getY()+1, newPosition.getX()+1);
        }
        return null;
    }

    @Override
    public double getCost() {
        switch (move) {
            case UP:
            case DOWN:
            case LEFT:
            case RIGHT:
                return 1.0;
            case UP_LEFT:
            case UP_RIGHT:
            case DOWN_LEFT:
            case DOWN_RIGHT:
                return sqrt2;
        }
        return 1000;
    }

    @Override
    public String toString() {
        switch (move) {
            case UP:
                return "UP";
            case DOWN:
                return "DOWN";
            case LEFT:
                return "LEFT";
            case RIGHT:
                return "RIGHT";
            case UP_LEFT:
                return "UP_LEFT";
            case UP_RIGHT:
                return "UP_RIGHT";
            case DOWN_LEFT:
                return "DOWN_LEFT";
            case DOWN_RIGHT:
                return "DOWN_RIGHT";
        }
        return null;
    }
}
