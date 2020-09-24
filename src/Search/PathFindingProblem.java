package Search;

public class PathFindingProblem implements IProblem{

    private int [][] map;
    private Position startPosition;
    private Position goalPosition;
    private IHeuristic heuristic;

    public PathFindingProblem(int[][] map, Position startPosition, Position goalPosition, IHeuristic heuristic) {
        this.map = map;
        this.startPosition = startPosition;
        this.goalPosition = goalPosition;
        this.heuristic = heuristic;
    }

    @Override
    public IProblemState getProblemState() {
        return new PathFindingState(this, startPosition, null);
    }

    @Override
    public IHeuristic getProblemHeuristic() {
        return heuristic;
    }


    public int[][] getMap() {
        return map;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Position startPosition) {
        this.startPosition = startPosition;
    }

    public Position getGoalPosition() {
        return goalPosition;
    }

    public void setGoalPosition(Position goalPosition) {
        this.goalPosition = goalPosition;
    }

    public IHeuristic getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(IHeuristic heuristic) {
        this.heuristic = heuristic;
    }
}
