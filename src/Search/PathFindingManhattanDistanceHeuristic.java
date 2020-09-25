package Search;

public class PathFindingManhattanDistanceHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof PathFindingState) {
            PathFindingState state = (PathFindingState) problemState;
            PathFindingProblem problem = (PathFindingProblem) state.getProblem();
            return Math.abs(problem.getGoalPosition().getY() - state.getPosition().getY()) +
                   Math.abs(problem.getGoalPosition().getX() - state.getPosition().getX());
        }
        return 0;
    }
}
