package Search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class BreadthFirstSearch extends ASearch {
    // Define lists here ...
    LinkedList<ASearchNode> openList;
    HashSet<IProblemState> antiReGenerator;
    //    HashMap<String, String> antiReGenerator;
    HashMap<IProblemState, ASearchNode> closedList;

    @Override
    public String getSolverName() {
        return "BFS";
    }

    @Override
    public ASearchNode createSearchRoot(IProblemState problemState) {
        ASearchNode newNode = new BlindSearchNode(problemState);
        return newNode;
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
        return antiReGenerator.contains(node._currentProblemState);
//        return openList.contains(node._currentProblemState);
    }

    @Override
    public boolean isClosed(ASearchNode node) {
        return closedList.containsKey(node._currentProblemState);
    }

    @Override
    public void addToOpen(ASearchNode node) {
        openList.add(node);
        antiReGenerator.add(node._currentProblemState);
    }

    @Override
    public void addToClosed(ASearchNode node) {
        closedList.put(node._currentProblemState, node);
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
