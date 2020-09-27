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
    private static final int HUGE_DOUBLE_VALUE = 0x7fffff00;

    public List<IProblemMove> solve ( IProblem problem ) {
        IProblemState problemState = problem.getProblemState();
        ASearchNode goal = abstractSearch(problemState);
        return goalNodeToSolutionPath(goal);
    }

    private ASearchNode abstractSearch ( IProblemState problemState ) {
        initLists();
        ASearchNode Vs = createSearchRoot(problemState);
        hCalculate(Vs);
//        System.out.println(problemState);
        ASearchNode current = null;
        ASearchNode debugPrev;
        addToOpen(Vs);
        rootH = Vs.getH();
        System.out.println("Root.H: " + rootH);
        expanded = 0;
        generated = 0;
        duplicates = 0;
        immediacies = 0;
        boolean admissible = true;
        double start = System.currentTimeMillis();


        while (openSize() > 0) {
            debugPrev = current;
            current = getBest();
            if(current == null)
                continue;
            if(debugPrev != null && current.getF()< debugPrev.getF()){
                System.out.println("problem");
            }
//            System.out.println(((RoomMapState)current._currentProblemState).getPosition() + "," + current.getH());
            if (current.isGoal()) {
                System.out.println("\rexpanded: " + expanded + "\tgenerated: " + generated + "\tduplicates: " + duplicates + "\t\tg: " + current.getG() + "\t\th: " + current.getH() + "\t\tf: " + (current.getF()) + "\t\tTime: " + (System.currentTimeMillis() - start) + "ms" + (admissible ? "\t Admissible\n" : "\t Not Admissible!!!\n"));
                System.out.println("calculated Average PerformMove Time: "+TestTime.calculateAveragePerformMoveTime()+" calculated Average Graph Creation Time: "+TestTime.calculateAverageGraphCreationTime()+" calculated Average H Time: "+TestTime.calculateAverageHTime());
                return current;
            }
            List<ASearchNode> neighbors = current.getNeighbors();
//            System.out.println("\ncurrent:\nlast move: " + current.getLastMove() + "\n" + current.currentProblemState + "g: " + current.getG() + "\t\th: " + current.getH() + "\t\tf: " + (current.getF()) + "\n\n\n");
//            int genID = 0;
            List<ASearchNode> neighborsToAdd = new ArrayList<>();
            for (ASearchNode Vn : neighbors) {
                if (isClosed(Vn)) {
                    continue;
                }
                if (!isOpen(Vn)) {
//                    System.out.println("gen"+(++genID)+"\nlast move: " + Vn._currentProblemState.getStateLastMove() + "\n" + Vn._currentProblemState + "g: " + Vn.getG() + "\t\th: " + Vn.getH() + "\t\tf: " + (Vn.getF()) + "\n");
//                    addToOpen(Vn);
                    neighborsToAdd.add(Vn);
                    generated++;
                } else {
                    duplicates++;
                    if (getOpen(Vn).getG() > Vn.getG()) {
//                        System.out.println("gen"+(++genID)+"\nlast move: " + Vn._currentProblemState.getStateLastMove() + "\n" + Vn._currentProblemState + "g: " + Vn.getG() + "\t\th: " + Vn.getH() + "\t\tf: " + (Vn.getF()) + "\n");
//                        addToOpen(Vn);
                        neighborsToAdd.add(Vn);
                        generated++;
                    }
                }
            }
//            double currentFixedH = current.getH(); // max h(n) - (g(n) - g(prev_n))
//            double currentFixedH = ((HeuristicSearchNode)current).oldF - current.getG(); // max h(n) - (g(n) - g(prev_n))
//            double minF = HUGE_DOUBLE_VALUE;
//            for (ASearchNode node : neighborsToAdd) {
//                hCalculate(node);
//                minF = Math.min(minF, node.getF());
//            }
//            // calculate neighbors h and perform inconsistency correction for parent.
//            for (ASearchNode node : neighborsToAdd) {
//                double hDifference = node.getH() - (node.getG() - current.getG());
//                if(node.getF() == minF)
//                    currentFixedH = Math.max(currentFixedH, hDifference);
//            }
            // perform inconsistency correction for neighbors.
            for (ASearchNode node : neighborsToAdd) {
                hCalculate(node);
                ((HeuristicSearchNode)node).setH(Math.max(node.getH(), node.prev.getH() - (node.getG() - node.prev.getG())));
                addToOpen(node);
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

    private void hCalculate(ASearchNode node) {
        if(node.currentProblemState instanceof RoomMapState){
            double startTime = System.currentTimeMillis();
            ((RoomMapState) node.currentProblemState).createGraphAdapter();
            double endOfGraphBuild = System.currentTimeMillis();
            TestTime.graphCreationSumOfTime+=endOfGraphBuild-startTime;
            ((HeuristicSearchNode) node).calculateH(node.currentProblemState);
            double endOfH = System.currentTimeMillis();
            TestTime.hSumOfTime+=endOfH-endOfGraphBuild;
            TestTime.numOfNodes++;
        }
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
