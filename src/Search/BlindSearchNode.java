package Search;

public class BlindSearchNode extends ASearchNode {
    double g;

	public BlindSearchNode(IProblemState currentProblemState) {
        prev = null;
        this.currentProblemState = currentProblemState;
        g = 0;
    }

	public BlindSearchNode(ASearchNode prev, IProblemState currentProblemState, double g) {
        this.prev = prev;
        this.currentProblemState = currentProblemState;
        this.g = g;
    }

    @Override
    public double getH() {
        return 0;
    }

    @Override
    public double getG() {
        return g;
    }

    @Override
    public double getF() {
        return g;
    }

    @Override
    public ASearchNode createSearchNode
            (
                    IProblemState currentProblemState
            ) {
        double g = this.g + currentProblemState.getStateLastMoveCost();
        return new BlindSearchNode(this, currentProblemState, g);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlindSearchNode) {
            BlindSearchNode otherNode = (BlindSearchNode) obj;
            return currentProblemState.equals(otherNode.currentProblemState);
        }
        return false;
    }

}
