package Search;

import java.util.ArrayList;
import java.util.List;

import static Search.PathFindingMove.MOVE.*;

public class PathFindingState implements IProblemState
{
    private PathFindingProblem problem;
    private Position position;
    private IProblemMove lastmove;

    public PathFindingState(PathFindingProblem problem, Position position, IProblemMove lastmove) {
        this.problem = problem;
        this.position = position;
        this.lastmove = lastmove;
    }

    @Override
    public List<IProblemState> getNeighborStates() {
        List<IProblemState> neighborStates = new ArrayList<>();
        List<PathFindingMove> legalMoves = getLegalMoves();

        for (PathFindingMove move: legalMoves){
            IProblemState newState = performMove(move);
            neighborStates.add(newState);
        }
        return neighborStates;
    }

    private List<PathFindingMove> getLegalMoves() {
        List<PathFindingMove> legalMoves = new ArrayList<>();
        PathFindingMove move;
        move = new PathFindingMove(RIGHT);
        checkAndAddMove(legalMoves,move);
        move = new PathFindingMove(LEFT);
        checkAndAddMove(legalMoves,move);
        move = new PathFindingMove(DOWN);
        checkAndAddMove(legalMoves,move);
        move = new PathFindingMove(UP);
        checkAndAddMove(legalMoves,move);
//        move = new PathFindingMove(UP_LEFT);
//        checkAndAddMove(legalMoves,move);
//        move = new PathFindingMove(UP_RIGHT);
//        checkAndAddMove(legalMoves,move);
//        move = new PathFindingMove(DOWN_LEFT);
//        checkAndAddMove(legalMoves,move);
//        move = new PathFindingMove(DOWN_RIGHT);
//        checkAndAddMove(legalMoves,move);
        return legalMoves;
    }

    private void checkAndAddMove(List<PathFindingMove> legalMoves, PathFindingMove move) {
        Position newPosition = move.getNewPosition(position);
        if (!isIllegalPosition(newPosition.getY(), newPosition.getX()))
            legalMoves.add(move);
    }

    private boolean isIllegalPosition(int y, int x) {
        int[][] map = problem.getMap();
        int height = map.length;
        int width = map[0].length;
        if (x < 0 || y < 0 || y >= height || x >= width)
            return true;
        if (Math.abs(y-position.getY())+Math.abs(x- position.getX())==2 && map[y][position.getX()]!=0 && map[position.getY()][x]!=0){
            return true;
        }
        return map[y][x] != 0;
    }

    @Override
    public IProblem getProblem() {
        return problem;
    }

    @Override
    public boolean isGoalState() {
        return position.equals(problem.getGoalPosition());
    }

    @Override
    public IProblemMove getStateLastMove() {
        return lastmove;
    }

    @Override
    public double getStateLastMoveCost() {
        return lastmove.getCost();
    }

    @Override
    public IProblemState performMove(IProblemMove move) {
        if (!(move instanceof PathFindingMove)) return null;
        PathFindingMove m = (PathFindingMove) move;
        return new PathFindingState(problem, m.getNewPosition(position), m);
    }

    public void setProblem(PathFindingProblem problem) {
        this.problem = problem;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public IProblemMove getLastmove() {
        return lastmove;
    }

    public void setLastmove(IProblemMove lastmove) {
        this.lastmove = lastmove;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PathFindingState)) return false;
        PathFindingState that = (PathFindingState) o;
        return position.equals(that.position);
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }

    public ArrayList<Position> asPartOfSolution() {
        ArrayList<Position> pos = new ArrayList<>();
        pos.add(position);
        return pos;
    }
}
