package Search;

public class HeuristicSearchNode extends BlindSearchNode {
    private double h;
    private IHeuristic heuristic;

	public HeuristicSearchNode(IProblemState currentProblemState) {
        super(currentProblemState);
        heuristic = currentProblemState.getProblem().getProblemHeuristic();
//        h = heuristic.getHeuristic(currentProblemState);
    }


    private HeuristicSearchNode(ASearchNode prev, IProblemState currentProblemState, double g, IHeuristic heuristic) {
        super(prev, currentProblemState, g);
        this.heuristic = heuristic;
//        h = this.heuristic.getHeuristic(currentProblemState);
    }

    public void setH(double h) {
        this.h = h;
    }

    @Override
    public double getH() {
        return h;
    }


    @Override
    public double getF() {
        return g + h;
    }


    @Override
    public ASearchNode createSearchNode(IProblemState currentProblemState) {
        double g = this.g + currentProblemState.getStateLastMoveCost();
        return new HeuristicSearchNode(this, currentProblemState, g, heuristic);
    }
    public void calculateH(IProblemState problemState){
        h = this.heuristic.getHeuristic(problemState);
    }
}
