package Search;

import java.util.ArrayList;
import java.util.List;

abstract public class ASearchNode {
    ASearchNode prev;
    IProblemState currentProblemState;

    public List<ASearchNode> getNeighbors() {
        List<ASearchNode> neighbors = new ArrayList<>();
        List<IProblemState> neighborStates = currentProblemState.getNeighborStates();

        for (IProblemState state : neighborStates) {
            ASearchNode newNode = createSearchNode(state);
                neighbors.add(newNode);
        }
        return neighbors;
    }

    public boolean isGoal() {
        return currentProblemState.isGoalState();
    }

    public IProblemMove getLastMove() {
        return currentProblemState.getStateLastMove();
    }

    abstract public double getH();

    abstract public double getG();

    abstract public double getF();

    abstract public ASearchNode createSearchNode(IProblemState currentProblemState);

   public abstract void setG(double g);

   public short getImmediate(){
       if(currentProblemState instanceof RoomMapState)
           return ((RoomMapState)currentProblemState).getImmediate();
       return 0;
   }
}
