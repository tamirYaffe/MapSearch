package Search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract public class ASearch {

    public static int expanded;
    public static int generated;
    public static int duplicates;
    public static int immediacies;
    public static double rootH = 0.0;

    public List<IProblemMove> solve ( IProblem problem ) {
        IProblemState problemState = problem.getProblemState();
        ASearchNode goal = abstractSearch(problemState);
        return goalNodeToSolutionPath(goal);
    }

    private ASearchNode abstractSearch ( IProblemState problemState ) {
        initLists();
        ASearchNode Vs = createSearchRoot(problemState);
//        System.out.println(problemState);
        rootH = Vs.getH();
        System.out.println("Root.H: " + rootH);
        ASearchNode current;
        addToOpen(Vs);
        expanded = 0;
        generated = 0;
        duplicates = 0;
        immediacies = 0;
        boolean admissible = true;
        double start = System.currentTimeMillis();


        while (openSize() > 0) {
            current = getBest();
//            System.out.println(((RoomMapState)current._currentProblemState).getPosition() + "," + current.getH());
            if (current.isGoal()) {
                System.out.println("\rexpanded: " + expanded + "\tgenerated: " + generated + "\tduplicates: " + duplicates + "\t\tg: " + current.getG() + "\t\th: " + current.getH() + "\t\tf: " + (current.getF()) + "\t\tTime: " + (System.currentTimeMillis() - start) + "ms" + (admissible ? "\t Admissible\n" : "\t Not Admissible!!!\n"));
                return current;
            }
            List<ASearchNode> neighbors = current.getNeighbors();
            System.out.println("\ncurrent:\nlast move: " + current.getLastMove() + "\n" + current.currentProblemState + "g: " + current.getG() + "\t\th: " + current.getH() + "\t\tf: " + (current.getF()) + "\n\n\n");
//            int genID = 0;
            for (ASearchNode Vn : neighbors) {
                if (isClosed(Vn)) {
                    continue;
                }

                if (!isOpen(Vn)) {
//                    System.out.println("gen"+(++genID)+"\nlast move: " + Vn._currentProblemState.getStateLastMove() + "\n" + Vn._currentProblemState + "g: " + Vn.getG() + "\t\th: " + Vn.getH() + "\t\tf: " + (Vn.getF()) + "\n");
                    addToOpen(Vn);
                    generated++;
                } else {
                    duplicates++;
                    if (getOpen(Vn).getG() > Vn.getG()) {
//                        System.out.println("gen"+(++genID)+"\nlast move: " + Vn._currentProblemState.getStateLastMove() + "\n" + Vn._currentProblemState + "g: " + Vn.getG() + "\t\th: " + Vn.getH() + "\t\tf: " + (Vn.getF()) + "\n");
                        addToOpen(Vn);
                        generated++;
                    }
                }
            }
            addToClosed(current);
//            System.out.println(current._currentProblemState);
            expanded++;
            immediacies+=current.getImmediate();
//            if (expanded % 1000 == 0 || (System.currentTimeMillis() - start) % 1000 < 50)
            if (current.getH() > 1000 - current.getG()) admissible = false;
            System.out.print("\rexpanded: " + expanded + "\tgenerated: " + generated + "\tduplicates: " + duplicates + "\t\tg: " + current.getG() + "\t\th: " + current.getH() + "\t\tf: " + (current.getF()) + "\t\tTime: " + (System.currentTimeMillis() - start) + "ms" + (admissible ? "\t Admissible" : "\t Not Admissible!!!"));
//            System.out.print("\rg: " + current.getG() + "\th: " + current.getH() + "\tf: " + (current.getF()) + (admissible? "\t Admissible" : "\t Not Admissible!!!"));
        }

        return null;
    }


    private List<IProblemMove> goalNodeToSolutionPath ( ASearchNode goal ) {
        if (goal == null)
            return null;
        ASearchNode currentNode = goal;
        List<IProblemMove> solutionPath = new ArrayList<>();
        while (currentNode.prev != null) {
            solutionPath.add(currentNode.getLastMove());
            currentNode = currentNode.prev;
        }
        Collections.reverse(solutionPath);
        return solutionPath;
    }

    abstract public String getSolverName();

    abstract public void initLists();

    abstract public ASearchNode getOpen(ASearchNode node);

    abstract public boolean isOpen(ASearchNode node);

    abstract public boolean isClosed(ASearchNode node);

    abstract public ASearchNode createSearchRoot(IProblemState problemState);

    abstract public void addToOpen(ASearchNode node);

    abstract public void addToClosed(ASearchNode node);

    abstract public int openSize();

    abstract public ASearchNode getBest();


}
