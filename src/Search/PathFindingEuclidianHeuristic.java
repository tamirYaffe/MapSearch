package Search;

public class PathFindingEuclidianHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        if (problemState instanceof PathFindingState) {
            PathFindingState state = (PathFindingState) problemState;
            PathFindingProblem problem = (PathFindingProblem) state.getProblem();
            return Math.sqrt(
                    Math.pow(problem.getGoalPosition().getY() - state.getPosition().getY(), 2) +
                    Math.pow(problem.getGoalPosition().getX() - state.getPosition().getX(), 2));
        }
        return 0;
    }
}
