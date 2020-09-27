package Search;

import javafx.collections.ObservableList;

public class HeuristicSearchNode extends BlindSearchNode {
    public enum F_COMPUTATION_METHOD {
        WASTAR("WA*"), XDP("XDP"), XUP("XUP");

        private String name;

        private F_COMPUTATION_METHOD(String name){this.name = name;}

        public String toString(){return name;}
    }

    ;

    public static double w = 1.0; //Weight for Weighted A*
    public static F_COMPUTATION_METHOD f_computation_method = F_COMPUTATION_METHOD.WASTAR;//Sub-Optimal F algorithm, when w=1 algorithm is regular A*
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
        switch (f_computation_method) {
            case WASTAR:
                /* f = g + w*h
                 *
                 * This is a simple modification to weighted A* that finds w-optimal solutions.
                 * Weighted A* does not need to re-open states to find w-optimal solutions.
                 */
                return g + (w * h);
            case XDP:
                /* f = (1/2w)[g+(2w-1)h+sqrt((g-h)^2+4wgh)]
                 *
                 * XDP stands for convex downward parabola.
                 * This is a small modification to weighted A*.
                 * In Weighted A*, the set of states with the same priority are on a straight line.
                 * XDP changes this straight line to a parabola.
                 * XDP causes the best-first search to find near-optimal paths
                 *      near the start and paths that are up to (2w-1) supoptimal near the goal.
                 * Overall the paths found are still w-optimal, and re-openings are not required.
                 */
                return (1 / (2 * w)) * (g + ((2 * w - 1) * h) + Math.sqrt(Math.pow(g - h, 2) + 4 * w * g * h));
            case XUP:
                /* f = (1/2w)(g + h + sqrt((g+h)^2+4w(w-1)h^2))
                 *
                 * XUP stands for convex upward parabola.
                 * XUP is similar to XDP, except it looks for (2w-1)-optimal paths
                 *      near the start and optimal paths near the start.
                 */
                return (1 / (2 * w)) * (g + h + Math.sqrt(Math.pow(g + h, 2) + 4 * w * (w - 1) * h * h));
        }
        return g + h;
    }


    @Override
    public ASearchNode createSearchNode(IProblemState currentProblemState) {
        double g = this.g + currentProblemState.getStateLastMoveCost();
        return new HeuristicSearchNode(this, currentProblemState, g, heuristic);
    }

    public void calculateH(IProblemState problemState) {
        h = this.heuristic.getHeuristic(problemState);
    }

    public static void setF_computation_method(String f_computation_method) {
        switch (f_computation_method){
            case "WA*":
                HeuristicSearchNode.f_computation_method = F_COMPUTATION_METHOD.WASTAR;
            case "XUP":
                HeuristicSearchNode.f_computation_method = F_COMPUTATION_METHOD.XUP;
            case "XDP":
                HeuristicSearchNode.f_computation_method = F_COMPUTATION_METHOD.XDP;
        }
    }

    public static void setW(double w) {
        HeuristicSearchNode.w = w;
    }
}
