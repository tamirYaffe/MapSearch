package Search;

public class ZeroHeuristic implements IHeuristic {
    @Override
    public double getHeuristic(IProblemState problemState) {
        return 0;
    }
}
