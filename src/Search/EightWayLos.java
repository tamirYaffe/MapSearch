package Search;

import rlforj.los.ILosAlgorithm;
import rlforj.los.ILosBoard;
import rlforj.math.Line2I;
import rlforj.math.Point2I;
import rlforj.util.BresenhamLine;

import java.util.List;
import java.util.Vector;

public class EightWayLos implements ILosAlgorithm {

    private Vector<Point2I> path;

    public boolean existsLineOfSight(ILosBoard b, int startX, int startY,
                                     int x1, int y1, boolean calculateProject) {
        int dx = x1 - startX, dy = y1 - startY;
        int adx = dx > 0 ? dx : -dx, ady = dy > 0 ? dy : -dy;
        int len = (Math.max(adx, ady)) + 1;//Max number of points on the path.

        // array to store path.
        int[] px = new int[len], py = new int[len];

        //Start to finish path
        // North West
        if (dx < 0 && dy < 0) {
            return existsStraightLineOfSight(b, startX, startY, x1, y1, -1, -1);
        }
        // West
        else if (dx < 0 && dy == 0) {
            return existsStraightLineOfSight(b, startX, startY, x1, y1, -1, 0);
        }
        // South West
        else if (dx < 0 && dy > 0) {
            return existsStraightLineOfSight(b, startX, startY, x1, y1, -1, 1);
        }
        // North
        else if (dx == 0 && dy < 0) {
            return existsStraightLineOfSight(b, startX, startY, x1, y1, 0, -1);
        }
        // South
        else if (dx == 0 && dy > 0) {
            return existsStraightLineOfSight(b, startX, startY, x1, y1, 0, 1);
        }
        // North East
        else if (dx > 0 && dy < 0) {
            return existsStraightLineOfSight(b, startX, startY, x1, y1, 1, -1);
        }
        // East
        else if (dx > 0 && dy == 0) {
            return existsStraightLineOfSight(b, startX, startY, x1, y1, 1, 0);
        }
        // South East
        else if (dx > 0 && dy > 0) {
            return existsStraightLineOfSight(b, startX, startY, x1, y1, 1, 1);
        }
        // same position
        else if (dx == 0 && dy == 0) return true;
        return false;
    }

    private boolean existsStraightLineOfSight(ILosBoard b, int x, int y, int goalX, int goalY, int dx, int dy) {
        while (b.contains(x, y)) {
            if (x == goalX && y == goalY) return true;
            if (b.isObstacle(x, y)) return false;
            x += dx;
            y += dy;
        }
        return false;
    }

    public List<Point2I> getProjectPath() {
        return path;
    }

    /**
     * A Eucleidian 2D line class represented by integers.
     *
     * @author Jonathan Duerig - modified by Shawn Seiref and Tamir Yaffe
     */
    private class Line {
        public Position near;

        public Position far;

        public Line(Position newNear, Position newFar) {
            near = newNear;
            far = newFar;
        }

        public boolean isBelow(final Position point) {
            return relativeSlope(point) > 0;
        }

        public boolean isBelowOrContains(final Position point) {
            return relativeSlope(point) >= 0;
        }

        public boolean isAbove(final Position point) {
            return relativeSlope(point) < 0;
        }

        public boolean isAboveOrContains(final Position point) {
            return relativeSlope(point) <= 0;
        }

        public boolean doesContain(final Position point) {
            return relativeSlope(point) == 0;
        }

        // negative if the line is above the point.
        // positive if the line is below the point.
        // 0 if the line is on the point.
        public int relativeSlope(final Position point) {
            return (far.getY() - near.getY()) * (far.getX() - point.getX()) - (far.getY() - point.getY())
                    * (far.getX() - near.getX());
        }

        @Override
        public String toString() {
            return "( " + near + " -> " + far + " )";
        }

    }
}
