package Search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class BreadthFirstSearch extends ASearch {
    // Define lists here ...
    private LinkedList<ASearchNode> openList;
    private HashSet<IProblemState> antiReGenerator;
    //    HashMap<String, String> antiReGenerator;
    private HashMap<IProblemState, ASearchNode> closedList;

    @Override
    public String getSolverName() {
        return "BFS";
    }

    @Override
    public ASearchNode createSearchRoot(IProblemState problemState) {
        return new BlindSearchNode(problemState);
    }

    @Override
    public void initLists() {
        openList = new LinkedList<>();
        closedList = new HashMap<>();
        antiReGenerator = new HashSet<>();
//        antiReGenerator = new HashMap<>();
    }

    @Override
    public ASearchNode getOpen(ASearchNode node) {
        if (isOpen(node))
            return node;
        return null;
    }

    @Override
    public boolean isOpen(ASearchNode node) {
        return antiReGenerator.contains(node.currentProblemState);
//        return openList.contains(node._currentProblemState);
    }

    @Override
    public boolean isClosed(ASearchNode node) {
        return closedList.containsKey(node.currentProblemState);
    }

    @Override
    public void addToOpen(ASearchNode node) {
        openList.add(node);
        antiReGenerator.add(node.currentProblemState);
    }

    @Override
    public void addToClosed(ASearchNode node) {
        closedList.put(node.currentProblemState, node);
    }

    @Override
    public int openSize() {
        return openList.size();
    }

    @Override
    public ASearchNode getBest() {
        return openList.remove(0);
    }


}
