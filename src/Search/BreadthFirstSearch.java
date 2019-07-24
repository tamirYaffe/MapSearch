package Search;

import java.util.HashMap;
import java.util.LinkedList;

public class BreadthFirstSearch extends ASearch {
    // Define lists here ...
    LinkedList<ASearchNode> openList;
//    HashMap<String, String> antiReGenerator;
    HashMap<String, ASearchNode> closedList;

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
//        return antiReGenerator.containsKey(node._currentProblemState.toString());
        return openList.contains(node._currentProblemState);
    }

    @Override
    public boolean isClosed(ASearchNode node) {
        return closedList.containsKey(node._currentProblemState.toString());
    }

    @Override
    public void addToOpen(ASearchNode node) {
        openList.add(node);
//        antiReGenerator.put(node._currentProblemState.toString(),"");
    }

    @Override
    public void addToClosed(ASearchNode node) {
        closedList.put(node._currentProblemState.toString(),node);
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
