package Search;

import rlforj.los.ILosAlgorithm;
import rlforj.los.ILosBoard;
import rlforj.math.Point2I;

import java.util.List;

public class EightWayLos implements ILosAlgorithm {

    public boolean existsLineOfSight(ILosBoard b, int startX, int startY,
                                     int x1, int y1, boolean calculateProject) {
        int dx = x1 - startX, dy = y1 - startY;

        //Start to finish path

        // Left
        if (dx < 0) {
            // North West
            if (dy < 0) {
                return existsStraightLineOfSight(b, startX, startY, x1, y1, -1, -1);
            }
            // West
            else if (dy == 0) {
                return existsStraightLineOfSight(b, startX, startY, x1, y1, -1, 0);
            }
            // South West
            else {
                return existsStraightLineOfSight(b, startX, startY, x1, y1, -1, 1);
            }
        }
        //Middle
        else if (dx == 0) {
            // North
            if (dy < 0) {
                return existsStraightLineOfSight(b, startX, startY, x1, y1, 0, -1);
            }
            // South
            else if (dy > 0) {
                return existsStraightLineOfSight(b, startX, startY, x1, y1, 0, 1);
            }
        }
        //Right
        else {
            // North East
            if (dy < 0) {
                return existsStraightLineOfSight(b, startX, startY, x1, y1, 1, -1);
            }
            // East
            else if (dy == 0) {
                return existsStraightLineOfSight(b, startX, startY, x1, y1, 1, 0);
            }
            // South East
            else {
                return existsStraightLineOfSight(b, startX, startY, x1, y1, 1, 1);
            }
        }
        // same position
        return true;
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

    public List<Point2I> getProjectPath() { return null; }
}
