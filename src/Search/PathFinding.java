package Search;

import java.util.ArrayList;
import java.util.List;

import static Search.RoomMap.MOVEMENT_METHOD;

public class PathFinding {
    private ASearch solver;
    private int[][] map;

    public PathFinding(int[][] map) {
        solver = new AStarSearch();
        this.map = map;
    }

    public PathFindingPath getPath(Position source, Position sink) {
//        PathFindingProblem problem = new PathFindingProblem(map, source, sink, new PathFindingEuclidianHeuristic());
        PathFindingProblem problem = new PathFindingProblem(map, source, sink, new PathFindingManhattanDistanceHeuristic());
        List<IProblemMove> solution = solver.solve(problem);
        return getSolutionPath(problem, solution);
    }

    private PathFindingPath getSolutionPath(IProblem problem, List<IProblemMove> solution) {
        double cost = 0;
        IProblemState currentState = problem.getProblemState();
        List<Position> path = new ArrayList<>(((PathFindingState) currentState).asPartOfSolution());
        for (IProblemMove move : solution) {
            currentState = currentState.performMove(move);
            path.addAll(((PathFindingState) currentState).asPartOfSolution());
            cost+=move.getCost();
        }
        return new PathFindingPath(path, cost);
    }


}
