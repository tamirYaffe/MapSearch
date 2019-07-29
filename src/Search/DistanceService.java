package Search;

import java.util.HashSet;

public class DistanceService {

    public static double minDistance(HashSet<Position> positions, Position currPosition) {
        Position distantPosition = null;
        double minDistance = Double.MAX_VALUE;
        for (Position position : positions) {
            double distance = manhattanDistance(position, currPosition);
            if (distance < minDistance)
                minDistance = distance;
        }
        return minDistance;
    }

    public static double euclideanDistance(Position p1, Position p2) {
        return Math.sqrt(Math.pow(p1.getY() - p2.getY(), 2) + Math.pow(p1.getX() - p2.getX(), 2));
    }

    public static double manhattanDistance(Position p1, Position p2) {
        return Math.abs(p1.getY() - p2.getY()) + Math.abs(p1.getX() - p2.getX());
    }
}
