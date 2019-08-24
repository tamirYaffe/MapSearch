import Search.*;

import java.util.ArrayList;
import java.util.List;

public class Model {
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
//        agent = new Position(0,13);
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
        agent = new Position(7, 6);

//        int map[][] = {
//                {0, 0, 0, 0, 0},
//                {1, 1, 0, 1, 1},
//                {0, 0, 0, 0, 0},
//                {0, 1, 1, 1, 0},
//                {0, 1, 0, 0, 0}};
//        agent = new Position(4,0);
        consoleString = "";
        this.map = map;
        csvResults[0] = "RoomMap basic map";
        csvResults[1] = "8";
        csvResults[2] = "8";
        csvResults[3] = "42";

    }

    public void solveMap(String movement, String heuristic, String los) {
        if (map == null)
            generateMap(0, 6);
//        agent = new Position(0,13);
//        agent = new Position(13, 19);
//        bfsRun();
//        generateMap(0, 0);
//        if (agent == null)
        AstarRun(movement, heuristic, los);
    }


//    private void bfsRun() {
////        System.out.println("---------- run 1 ----------");
//        consoleString = "------ run 1 ------";
//        List<ASearch> solvers = new ArrayList<ASearch>();
//        BreadthFirstSearch bfs = new BreadthFirstSearch();
//        solvers.add(bfs);
//        solveInstances(solvers, "roomMap", movement, heuristic, los);
//    }

    private void AstarRun(String movement, String heuristic, String los) {
//        System.out.println("---------- run 2 ----------");
        consoleString = "------ run 2 ------";
        List<ASearch> solvers = new ArrayList<ASearch>();
        AStarSearch aStar = new AStarSearch();
        solvers.add(aStar);
        solveInstances(solvers, "roomMap", movement, heuristic, los);
    }


    private void solveInstances(List<ASearch> solvers, String instancesType, String movement, String heuristic, String los) {
        {
//            agent = new Position(61,1);
            long totalTime = 0;
            String instance = instancesType;
            RoomMap problem = new RoomMap(map, agent, movement,heuristic,los);
//            RoomMapJump problem = new RoomMapJump(map, agent);
            DistanceService.setRoomMap(problem);
            for (ASearch solver : solvers) {
//                System.out.println("Solver: " + solver.getSolverName());
                consoleString += "\nSolver: " + solver.getSolverName();
                consoleString += "\nH alg: " + heuristic;
                consoleString += "\nLOS: " + los;
                long startTime = System.nanoTime();
                List<IProblemMove> solution = solver.solve(problem);
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
                    consoleString += "\nRoot H: " + ASearch.rootH;
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
            currentState = currentState.performMove(move);
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
        solutionList.addAll(((RoomMapState) currentState).asPartOfSolution());
        for (IProblemMove move : solution) {
            currentState = currentState.performMove(move);
            solutionList.addAll(((RoomMapState) currentState).asPartOfSolution());
        }
    }

    public void printSolution(IProblem instance, List<IProblemMove> solution) {
        IProblemState currentState = instance.getProblemState();
        for (IProblemMove move : solution) {
            currentState = currentState.performMove(move);
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
