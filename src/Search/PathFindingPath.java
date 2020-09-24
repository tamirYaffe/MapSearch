package Search;

import java.util.ArrayList;
import java.util.List;

public class PathFindingPath {
    private List<Position> path;
    private double pathCost;

    public PathFindingPath(List<Position> path, double pathCost) {
        this.path = path;
        this.pathCost = pathCost;
    }

    public List<Position> getPath() {
        return path;
    }

    public void setPath(List<Position> path) {
        this.path = path;
    }

    public double getPathCost() {
        return pathCost;
    }

    public void setPathCost(double pathCost) {
        this.pathCost = pathCost;
    }

    @Override
    public String toString() {
        return "PathFindingPath:\n" +
                "path:" + path +
                "\npathCost:" + pathCost;
    }
}
