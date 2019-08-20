package Search.Jump;

import Search.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class JumpModel {
    public int[][] map = null;
    public String consoleString = "";
    public ArrayList<Position> solutionList = new ArrayList<>();
    public int solutionIndex = 0;
    public Position agent;

    /**
     * csvResults is an array that represents the current run's record for the csv
     * csvResults[0] = Map's Name`
     * csvResults[1] = Map's Height
     * csvResults[2] = Map's Width
     * csvResults[3] = Number of terrain positions in the map
     * csvResults[4] = Solver's Name
     * csvResults[5] = Heuristic (if available)
     * csvResults[6] = Time took to finish the run
     * csvResults[7] = Number of Generated Nodes
     * csvResults[8] = Number of Duplicate Nodes
     * csvResults[9] = Number of Expanded Nodes
     * csvResults[10] = threshold (if available)
     * csvResults[11] = Max leaves count (if available)
     * csvResults[12] = Solution Length
     * csvResults[13] = Start Position
     * csvResults[14] = Line of Sight method
     * csvResults[15] = Root H (heuristic value)
     */
    String[] csvResults = new String[16];

    public void loadMap(int[][] map, String name) {
        csvResults = new String[16];
        this.map = map;
        int x, y;
        do {
            y = (int) (Math.random() * map.length);
            x = (int) (Math.random() * map[0].length);
        } while (map[y][x] != 0);
        agent = new Position(y, x);
        csvResults[0] = name;
        csvResults[1] = "" + map.length;
        csvResults[2] = "" + map[0].length;
    }

    public void loadMap(int[][] map, Position agentPosition) {
        this.map = map;
        agent = agentPosition;
    }

    public void generateMap(int rows, int columns) {
//        MapGenerator mapGenerator=new MapGenerator();
//        map=mapGenerator.generate(rows,columns);
        csvResults = new String[16];
        int map[][] = {
                {0, 0, 0, 0, 0, 1, 0, 1},
                {1, 1, 1, 1, 0, 1, 0, 0},
                {0, 0, 0, 0, 0, 1, 0, 1},
                {0, 1, 0, 1, 0, 1, 0, 0},
                {0, 1, 0, 0, 0, 1, 1, 0},
                {1, 1, 0, 0, 0, 1, 0, 0},
                {0, 1, 0, 0, 1, 1, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0}};
        consoleString = "";
        this.map = map;
        agent = new Position(7, 6);
        csvResults[0] = "RoomMap basic map";
        csvResults[1] = "8";
        csvResults[2] = "8";
        csvResults[3] = "42";

    }

    public void solveMap() {
        if (map == null)
            generateMap(0, 6);
//        try {
//                        agent = new Position(13, 19);
//        agent = new Position(38,10);
//        agent = new Position(2,20);
//        agent = new Position(7,6);
//        agent = new Position(0,1);
//        bfsRun();
//        generateMap(0, 0);
        AstarJumpRun();
//        AstarRun();
//        } catch (Exception e) {
//            agent = new Position(7, 6);
//            AstarJumpRun();
//        }
    }
//            [[(19,13), (19,12), (19,11), (19,10), (19,9), (19,8), (19,7), (19,6)], [(19,6), (19,5), (18,5), (17,5), (16,5), (15,5), (14,5), (13,5), (12,5), (12,4), (12,3), (12,2), (11,2)], [(11,2), (11,3), (11,4), (10,4)], [(10,4), (9,4), (8,4), (8,3)], [(8,3), (7,3)], [(7,3), (6,3), (6,4), (6,5), (6,6), (5,6), (4,6), (3,6), (2,6)], [(2,6), (1,6), (0,6), (0,7), (0,8), (1,8), (1,9), (1,10), (1,11), (1,12), (1,13), (1,14)], [(1,14), (1,15)]]
//            [[(19,13), (19,12), (19,11), (19,10), (19,9), (19,8), (19,7), (19,6)], [(19,6), (19,5), (18,5), (17,5), (16,5), (15,5), (14,5), (13,5), (12,5), (12,4), (12,3), (12,2), (11,2)], [(11,2), (11,3), (11,4), (10,4)], [(10,4), (9,4), (8,4), (8,3)], [(8,3), (8,4), (7,4), (7,5), (6,5), (6,6), (6,7), (6,8), (5,8), (4,8), (3,8), (2,8), (1,8), (0,8)], [(0,8), (1,8), (1,9), (1,10), (1,11), (1,12), (1,13), (1,14)]]

    private void bfsRun() {
//        System.out.println("---------- run 1 ----------");
        consoleString = "------ run 1 ------";
        List<ASearch> solvers = new ArrayList<ASearch>();
        BreadthFirstSearch bfs = new BreadthFirstSearch();
        solvers.add(bfs);
        solveInstances(solvers, "roomMap");
    }

    private void AstarRun() {
//        System.out.println("---------- run 2 ----------");
        consoleString = "------ run 2 ------";
        List<ASearch> solvers = new ArrayList<ASearch>();
        AStarSearch aStar = new AStarSearch();
        solvers.add(aStar);
        solveInstances(solvers, "roomMap");
    }

    private void AstarJumpRun() {
//        System.out.println("---------- run 2 ----------");
        consoleString = "------ run 3 ------";
        List<ASearch> solvers = new ArrayList<ASearch>();
        AStarSearch aStar = new AStarSearch();
        solvers.add(aStar);
        solveInstances(solvers, "roomMapJump");
    }


    private void solveInstances(List<ASearch> solvers, String instancesType) {
        {
//            agent = new Position(61,1);
            long totalTime = 0;
            String instance = instancesType;
            RoomMapJump problem = new RoomMapJump(map, agent);
            DistanceService.setRoomMap(problem);
            for (ASearch solver : solvers) {
//                System.out.println("Solver: " + solver.getSolverName());
                consoleString += "\nSolver: " + solver.getSolverName();
                consoleString += "\nH alg: " + (solver.getSolverName().equals("BFS") ? "None" : problem.getHeuristicName().substring(7));
                consoleString += "\nLOS: " + problem.getVisualAlgorithm();
                long startTime = System.nanoTime();
                List<IProblemMove> solution = solver.solve(problem);
                consoleString += "\nRoot H: " + (solver.getSolverName().equals("BFS") ? "None" : ASearch.rootH);
                long finishTime = System.nanoTime();
                double cost = checkSolution(problem, solution);
                if (cost >= 0)        // valid solution
                {
                    // printSolution(problem, solution);
                    updateSolution(problem, solution);
                    // System.out.println("Closed: " + solver.closed);
                    // System.out.println("Cost:  " + cost);
                    // System.out.println("Moves: " + solution.size());
                    // System.out.println("Time:  " + (finishTime - startTime) / 1000000.0 + " ms");
                    // System.out.println(solution);
                    consoleString += "\nGenerated: " + solver.generated;
                    consoleString += "\nDuplicates: " + solver.duplicates;
                    consoleString += "\nExpanded: " + solver.expanded;
                    consoleString += "\nCost:  " + cost;
                    consoleString += "\nMoves: " + solution.size();
                    consoleString += "\nTime:  " + (finishTime - startTime) / 1000000.0 + " ms\n\n";
                    consoleString += solution;
                    totalTime += (finishTime - startTime) / 1000000.0;

                    //            csvResults[3] = Number of terrain positions in the map
                    csvResults[4] = Integer.toString(problem.getNumberOfPositions());
                    //csvResults[4] = Solver's Name
                    csvResults[3] = solver.getSolverName();
                    //csvResults[5] = Heuristic (if available)
//                    csvResults[5] = " ";
                    csvResults[5] = problem.getHeuristicName();
                    //csvResults[6] = Time took to finish the run
                    csvResults[6] = Double.toString(totalTime);
                    //csvResults[7] = Number of Generated Nodes
                    csvResults[7] = Integer.toString(solver.generated);
                    //csvResults[8] = Number of Duplicate Nodes
                    csvResults[8] = Integer.toString(solver.duplicates);
                    //csvResults[9] = Number of Expanded Nodes
                    csvResults[9] = Integer.toString(solver.expanded);
                    //csvResults[10] = threshold( if available)
                    csvResults[10] = "0";
                    //csvResults[11] = Max leaves count ( if available)
                    csvResults[11] = "5";
                    //csvResults[12] = Solution Length
                    csvResults[12] = Integer.toString((int) cost);
                    //csvResults[13] = Start Position
                    csvResults[13] = problem.getStartPosition().toString().replace(",", ";");
                    // csvResults[14] = Line of Sight method
                    csvResults[14] = problem.getVisualAlgorithm();
                    // csvResults[15] = Root H (heuristic value)
                    csvResults[15] = Double.toString(ASearch.rootH);
                    RoomMapCSVWriter.writeToCSV("Results.csv", csvResults);
                } else {                // invalid solution
                    // System.out.println("Invalid solution.");
                    consoleString += "\nInvalid solution.";
                }
            }
//            System.out.println("");
//            System.out.println("Total time:  " + totalTime / 60000.0 + " min");
            int totalTimeMinuts = (int) ((totalTime / 1000) % 60);
            consoleString += "\n\nTotal time:  " + (int) (totalTime / 60000) + ":" + (totalTimeMinuts > 9 ? totalTimeMinuts : "0" + totalTimeMinuts) + " min\n";
//            System.out.println("");
            System.out.println(consoleString);

        }

    }


    public double checkSolution(IProblem instance, List<IProblemMove> solution) {
        if (solution == null)
            return -1;
        double cost = 0;
        IProblemState currentState = instance.getProblemState();
        for (IProblemMove move : solution) {
            currentState = currentState.performMove((RoomMapJumpStep) move);
            if (currentState.getStateLastMove() != null)
                cost += currentState.getStateLastMoveCost();
        }
        if (currentState.isGoalState())
            return cost;
        return -1;
    }

    private void updateSolution(RoomMap problem, List<IProblemMove> solution) {
        solutionList = new ArrayList<>();
        solutionIndex = 0;

        IProblemState currentState = problem.getProblemState();
        RoomMapJumpState s = (RoomMapJumpState) currentState;
//        if (s.equals(problem.getStartPosition()))
//        solutionList.addAll(Arrays.asList(((RoomMapJumpStep) s.getStateLastMove()).path));
//        map[((RoomMapState) currentState).getPosition().getY()][((RoomMapState) currentState).getPosition().getX()] = 2;
        for (IProblemMove move : solution) {
            RoomMapJumpStep m = (RoomMapJumpStep) move;
            currentState = currentState.performMove(m);
            solutionList.addAll(((RoomMapJumpStep) ((RoomMapJumpState) currentState).getStateLastMove()).path);
//            solutionList.add(new Position(((RoomMapJumpState) currentState).getPosition()));
//        map[((RoomMapState) currentState).getPosition().getY()][((R.oomMapState) currentState).getPosition().getX()] = 2;
        }
    }

    public void printSolution(IProblem instance, List<IProblemMove> solution) {
        IProblemState currentState = instance.getProblemState();
        for (IProblemMove move : solution) {
            RoomMapJumpStep m = (RoomMapJumpStep) move;
            currentState = currentState.performMove(m);
            System.out.println(currentState);
        }
    }

    public void showNextMove() {
        if (!solutionList.isEmpty()) {
            solutionIndex++;
            if (solutionIndex == solutionList.size())
                solutionIndex = 0;
            Position nextPosition = solutionList.get(solutionIndex);
            agent = new Position(nextPosition);
        }
    }

    public void showBeforeMove() {
        if (!solutionList.isEmpty()) {
            solutionIndex--;
            if (solutionIndex == -1)
                solutionIndex = solutionList.size() - 1;
            Position nextPosition = solutionList.get(solutionIndex);
            agent = new Position(nextPosition);
        }
    }

    public void showAllSolution() {
        for (int i = 0; i < solutionList.size(); i++) {
            Position position = solutionList.get(i);
            map[position.getY()][position.getX()] = 2;
        }
    }
}
