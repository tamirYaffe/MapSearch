import Search.*;

import java.util.*;

public class Model {
    public int[][] map = null;
    public String consoleString = "";
    private ArrayList<Position> solutionList = new ArrayList<>();
    private int solutionIndex = 0;
    public Position agent;
    private String resultsFileName = "Results/Results.csv";
    private HashMap<Position, HashSet<Position>> watchedDictionary;

    private static int mapChooser = 0;

    /**
     * csvResults is an array that represents the current run's record for the csv
     * csvResults[0] = Map's Name
     * csvResults[1] = Map's Height
     * csvResults[2] = Map's Width
     * csvResults[3] = Number of terrain positions in the map
     * csvResults[4] = Solver's Name
     * csvResults[5] = Algorithm's Name
     * csvResults[6] = Weight
     * csvResults[7] = is No Whites on
     * csvResults[8] = is Farthest on
     * csvResults[9] = is Bounded on
     * csvResults[10] = Heuristic (if available)
     * csvResults[11] = Time took to finish the run
     * csvResults[12] = Number of Generated Nodes
     * csvResults[13] = Number of Duplicate Nodes
     * csvResults[14] = Number of Expanded Nodes
     * csvResults[15] = Graph Type
     * csvResults[16] = Distance Factor (for bounded jump)
     * csvResults[17] = Solution Length
     * csvResults[18] = Start Position
     * csvResults[18] = Line of Sight method
     * csvResults[18] = Movement method
     * csvResults[18] = Root H (heuristic value)
     */
    private String[] csvResults = new String[23];

    public void loadMap(int[][] map, String name) {
        csvResults = new String[23];
        this.map = map;
        //Map1
        agent = new Position(58, 54);
//        agent = new Position(400, 120);
        //w_encounter1
        if (agent.getY() >= map.length || agent.getX() >= map[0].length || map[agent.getY()][agent.getX()] != 0)
            agent = new Position(212, 93);
        //mazes
        if (agent.getY() >= map.length || agent.getX() >= map[0].length || map[agent.getY()][agent.getX()] != 0)
            agent = new Position(0, 0);
        //lt_shop
        if (agent.getY() >= map.length || agent.getX() >= map[0].length || map[agent.getY()][agent.getX()] != 0)
            agent = new Position(0, 1);
        //It_ruinedhouse_n
        if (agent.getY() >= map.length || agent.getX() >= map[0].length || map[agent.getY()][agent.getX()] != 0)
            agent = new Position(0, 2);
        //den101d
        if (agent.getY() >= map.length || agent.getX() >= map[0].length || map[agent.getY()][agent.getX()] != 0)
            agent = new Position(38, 10);
        //den020d
        if (agent.getY() >= map.length || agent.getX() >= map[0].length || map[agent.getY()][agent.getX()] != 0)
            agent = new Position(0, 20);
        //map1_22X28
        if (agent.getY() >= map.length || agent.getX() >= map[0].length || map[agent.getY()][agent.getX()] != 0)
            agent = new Position(13, 19);
        //orz106d
        if (agent.getY() >= map.length || agent.getX() >= map[0].length || map[agent.getY()][agent.getX()] != 0)
            agent = new Position(0, 13);
        //random
        if (agent.getY() >= map.length || agent.getX() >= map[0].length || map[agent.getY()][agent.getX()] != 0) {
            int x, y;
            do {
                y = (int) (Math.random() * map.length);
                x = (int) (Math.random() * map[0].length);
            } while (map[y][x] != 0);
            agent = new Position(y, x);
        }
        csvResults[0] = name;
        csvResults[1] = "" + map.length;
        csvResults[2] = "" + map[0].length;
        agent = new Position(7, 6);
        agent = new Position(0, 39);
    }

    public void generateMap(int rows, int columns) {
        MazeGenerator mapGenerator = new MazeGenerator();
        this.map = mapGenerator.generate(rows, columns);
        agent = mapGenerator.getStartPosition();
        csvResults = new String[23];
        consoleString = "";
        csvResults[0] = "RoomMap Random map";
        csvResults[1] = "" + rows;
        csvResults[2] = "" + columns;
    }

    public void generateMap(/*int rows, int columns*/) {
//        MapGenerator mapGenerator=new MapGenerator();
//        map=mapGenerator.generate(rows,columns);
        mapChooser++;
        csvResults = new String[23];
        int[][] map = null;
        switch (mapChooser % 3) {
            case 1:
                map = new int[][]{
                        {0, 0, 0, 0, 0, 1, 0, 1},
                        {1, 1, 1, 1, 0, 1, 0, 0},
                        {0, 0, 0, 0, 0, 1, 0, 1},
                        {0, 1, 0, 1, 0, 1, 0, 0},
                        {0, 1, 0, 0, 0, 1, 1, 0},
                        {1, 1, 0, 0, 0, 1, 0, 0},
                        {0, 1, 0, 0, 1, 1, 0, 1},
                        {0, 0, 0, 0, 0, 0, 0, 0}};
                agent = new Position(7, 6);
                break;
            case 2:
                map = new int[][]{
                        {0, 0, 0, 0, 0, 0},
                        {0, 1, 0, 0, 1, 0},
                        {0, 1, 0, 0, 1, 0},
                        {0, 1, 1, 1, 1, 0},
                        {0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0}};
                agent = new Position(5, 2);
                break;
            case 0:
                map = new int[][]{
                        {0, 0, 0, 0, 0},
                        {1, 1, 0, 1, 1},
                        {0, 0, 0, 0, 0},
                        {0, 1, 1, 1, 0},
                        {0, 1, 0, 0, 0}};
                agent = new Position(4, 0);
        }
        consoleString = "";
        this.map = map;
        csvResults[0] = "RoomMap basic mini map";
//        csvResults[0] = "RoomMap basic map";
        csvResults[1] = String.valueOf(map.length);
        csvResults[2] = String.valueOf(map[0].length);
    }


    public void densityGraphBuilder(int rows, int columns) {
        if (map == null)
            generateMap(rows, columns);
        rows = map.length;
        columns = map[0].length;
        ArrayList<Position> blankList = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (map[i][j] == 1) {
                    blankList.add(new Position(i, j));
                }
            }
        }
        MapWriter mapWriter = new MapWriter();
        resultsFileName = csvResults[0] + " " + map.length + "x" + map[0].length;
        String path = "DensMaps/" + resultsFileName;
        resultsFileName = "Results/" + resultsFileName + ".csv";
        do {
            csvResults[0] = "Density Graph Map " + blankList.size() + " obstacles";
            mapWriter.createFiles(this, path, csvResults[0]);
            Runtime.getRuntime().gc();
//            try {
//                solveMap("4-way", "Zero", "Symmetric BresLos", "Farther Frontiers", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the BRFS!\n\n");
//            }
//            try {
//                solveMap("4-way", "Singleton", "Symmetric BresLos", "Farther Frontiers", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the Singleton!\n\n");
//            }
//            try {
//                solveMap("4-way", "MST", "Symmetric BresLos", "Farther Frontiers", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the MST!\n\n");
//            }
//            try {
//                solveMap("4-way", "TSP", "Symmetric BresLos", "Farther Frontiers", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the TSP!\n\n");
//            }
//            try {
//                solveMap("Jump", "Zero", "Symmetric BresLos", "Farther Frontiers", 2, check_No_Whites.isSelected(), check_Farthest.isSelected(), check_Bounded.isSelected());
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the BRFS!\n\n");
//            }
//            try {
//                solveMap("Jump", "Singleton", "Symmetric BresLos", "Farther Frontiers", 2, check_No_Whites.isSelected(), check_Farthest.isSelected(), check_Bounded.isSelected());
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the Singleton!\n\n");
//            }
//            try {
//                solveMap("Jump", "MST", "Symmetric BresLos", "Farther Frontiers", 2, check_No_Whites.isSelected(), check_Farthest.isSelected(), check_Bounded.isSelected());
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the MST!\n\n");
//            }
//            try {
//                solveMap("Jump", "TSP", "Symmetric BresLos", "Farther Frontiers", 2, check_No_Whites.isSelected(), check_Farthest.isSelected(), check_Bounded.isSelected());
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the TSP!\n\n");
//            }
//            try {
//                solveMap("4-way", "Zero", "8-way", "All", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the BRFS!\n\n");
//            }
//            try {
//                solveMap("4-way", "Singleton", "8-way", "All", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the Singleton!\n\n");
//            }
//            try {
//                solveMap("4-way", "MST", "8-way", "All", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the MST!\n\n");
//            }
//            try {
//                solveMap("4-way", "TSP", "8-way", "Frontiers", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the TSP!\n\n");
//            }
//            try {
//                solveMap("4-way", "Zero", "4-way", "All", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the BRFS!\n\n");
//            }
//            try {
//                solveMap("4-way", "Singleton", "4-way", "All", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the Singleton!\n\n");
//            }
//            try {
//                solveMap("4-way", "MST", "4-way", "All", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the MST!\n\n");
//            }
//            try {
//                solveMap("4-way", "TSP", "4-way", "Frontiers", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the TSP!\n\n");
//            }
//            try {
//                solveMap("Expanding Border", "Zero", "Symmetric BresLos", "All", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the BRFS EB!\n\n");
//            }
//            try {
//                solveMap("Expanding Border", "Singleton", "Symmetric BresLos", "All", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the Singleton EB!\n\n");
//            }
//            try {
//                solveMap("Expanding Border", "MST", "Symmetric BresLos", "All", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the MST EB!\n\n");
//            }
//            try {
//                solveMap("Expanding Border", "TSP", "Symmetric BresLos", "Frontiers", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the TSP EB!\n\n");
//            }
//            try {
//                solveMap("Expanding Border", "Zero", "8-way", "All", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the BRFS EB!\n\n");
//            }
//            try {
//                solveMap("Expanding Border", "Singleton", "8-way", "All", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the Singleton EB!\n\n");
//            }
//            try {
//                solveMap("Expanding Border", "MST", "8-way", "All", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the MST EB!\n\n");
//            }
//            try {
//                solveMap("Expanding Border", "TSP", "8-way", "Frontiers", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the TSP EB!\n\n");
//            }
//            try {
//                solveMap("Expanding Border", "Zero", "4-way", "All", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the BRFS EB!\n\n");
//            }
//            try {
//                solveMap("Expanding Border", "Singleton", "4-way", "All", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the Singleton EB!\n\n");
//            }
//            try {
//                solveMap("Expanding Border", "MST", "4-way", "All", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the MST EB!\n\n");
//            }
//            try {
//                solveMap("Expanding Border", "TSP", "4-way", "Frontiers", 2);
//            } catch (Exception e) {
//                System.out.println(consoleString + "\ncouldn't finish the TSP EB!\n\n");
//            }
//            mapWriter.createFiles(this, "DensMaps", csvResults[0]);
            for (int i = 0; i < 1 && !blankList.isEmpty(); i++) {
                boolean removed = false;
                while (!removed) {
                    int randI = (int) (blankList.size() * Math.random());
                    Position p = blankList.remove(randI);
                    int neighbors = 0;
                    int x = p.getX(), y = p.getY();
                    int xm = 0;//x minus 1
                    int xp = 0;//x plus 1
                    int ym = 0;//y minus 1
                    int yp = 0;//y plus 1
                    if (x < map[0].length - 1 && map[y][x + 1] == 1) {
                        neighbors++;
                        xp++;
                    }
                    if (x > 0 && map[y][x - 1] == 1) {
                        neighbors++;
                        xm++;
                    }
                    if (y < map.length - 1 && map[y + 1][x] == 1) {
                        neighbors++;
                        yp++;
                    }
                    if (y > 0 && map[y - 1][x] == 1) {
                        neighbors++;
                        ym++;
                    }
                    if (neighbors <= 1 || (neighbors == 2 && xp == xm && ym == yp)) {
                        removed = true;
                        map[y][x] = 0;
                    } else {
                        blankList.add(p);
                    }

                }

            }
        } while (!blankList.isEmpty());
    }

    public void solveMap(String movement, String heuristic, String los, String heuristicGraph, String algorithm, double distFactor, double w, boolean isNoWhites, boolean isFarthest, boolean isBounded, boolean computeAllPaths) {
        consoleString = "";
        if (map == null)
            generateMap();
        ASearch solver = new AStarSearch();
        if (!movement.startsWith("Jump") && !movement.startsWith("Exp") && heuristic.equals("Zero"))
            solver = new BreadthFirstSearch();
//        UniformCostSearch solver = new UniformCostSearch();
//        PureHeuristicSearch solver = new PureHeuristicSearch();
        long totalTime = 0;
        RoomMap problem = new RoomMap(map, agent, movement, heuristic, los, heuristicGraph, algorithm, distFactor, w, isNoWhites, isFarthest, isBounded, computeAllPaths);
        DistanceService.setRoomMap(problem);
        watchedDictionary = problem.getVisualDictionary();
        consoleString += "\nSolver: " + solver.getSolverName();
        consoleString += "\nAlgorithm: " + algorithm;
        consoleString += "\nWeight: " + w;
        consoleString += "\nisNoWhites: " + (isNoWhites?"Yes":"No");
        consoleString += "\nisFarthest: " + (isFarthest?"Yes":"No");
        consoleString += "\nisBounded: " + (isBounded?"Yes":"No");
        consoleString += "\nH alg: " + heuristic;
        consoleString += "\nLOS: " + los;
        consoleString += "\nGraph: " + heuristicGraph;
        long startTime = System.nanoTime();
        List<IProblemMove> solution = solver.solve(problem);
        long finishTime = System.nanoTime();
        double cost = checkSolution(problem, solution);
        if (cost >= 0)        // valid solution
        {
            // printSolution(problem, solution);
            updateSolution(problem, solution);
            consoleString += "\nRoot H: " + ASearch.rootH;
            consoleString += "\nGenerated: " + ASearch.generated;
            consoleString += "\nDuplicates: " + ASearch.duplicates;
            consoleString += "\nExpanded: " + ASearch.expanded;
            consoleString += "\nCost:  " + cost;
            consoleString += "\nMoves: " + solution.size();
            totalTime += (finishTime - startTime) / 1000000.0;
            int timeMinuts = (int) ((totalTime / 1000) % 60);
            consoleString += "\nTime:  " + (totalTime > 60000 ? (totalTime / 60000) + ":" + (timeMinuts > 9 ? timeMinuts : "0" + timeMinuts) + " min " : ((finishTime - startTime) / 1000000.0) + "ms") + "\n\n";
            consoleString += solution;
            //csvResults[3] = Solver's Name
            csvResults[3] = solver.getSolverName();
            //csvResults[4] = Number of terrain positions in the map
            csvResults[4] = Integer.toString(problem.getNumberOfPositions());
            // csvResults[5] = Algorithm's Name
            csvResults[5] = algorithm;
            // csvResults[6] = Weight
            csvResults[6] = String.valueOf(w);
            //csvResults[7] = is No Whites on
            csvResults[7] = (isNoWhites?"X":"");
            //csvResults[8] = is Farthest on
            csvResults[8] = (isFarthest?"X":"");
            //csvResults[9] = is Bounded on
            csvResults[9] = (isBounded?"X":"");
            //csvResults[10] = Heuristic
            csvResults[10] = heuristic;
            //csvResults[11] = Time took to finish the run
            csvResults[11] = Double.toString(totalTime);
            //csvResults[12] = Number of Generated Nodes
            csvResults[12] = Integer.toString(ASearch.generated);
            //csvResults[13] = Number of Duplicate Nodes
            csvResults[13] = Integer.toString(ASearch.duplicates);
            //csvResults[14] = Number of Expanded Nodes
            csvResults[14] = Integer.toString(ASearch.expanded);
            // csvResults[15] = Graph Type
            csvResults[15] = heuristicGraph;
            // csvResults[16] = Distance Factor (for bounded jump)
            csvResults[16] = "" + distFactor;
            //csvResults[17] = Solution Length
            csvResults[17] = Integer.toString((int) cost);
            //csvResults[18] = Start Position
            csvResults[18] = problem.getStartPosition().toString().replace(",", ";");
            // csvResults[19] = Line of Sight method
            csvResults[19] = los;
            // csvResults[20] = Movement method
            csvResults[20] = movement;
            // csvResults[21] = Root H (heuristic value)
            csvResults[21] = Double.toString(ASearch.rootH);
            // csvResults[22] = Date
            csvResults[22] = new Date().toString();

            RoomMapCSVWriter.writeToCSV(resultsFileName, csvResults);
        } else {                // invalid solution
            consoleString += "\nInvalid solution.";
        }
        int totalTimeMinuts = (int) ((totalTime / 1000) % 60);
        consoleString += "\n\nTotal time:  " + (int) (totalTime / 60000) + ":" + (totalTimeMinuts > 9 ? totalTimeMinuts : "0" + totalTimeMinuts) + " min\n";
        System.out.println(consoleString);
    }

    private double checkSolution(IProblem instance, List<IProblemMove> solution) {
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

    public void printSolution() {
        for (Position position : solutionList) {
            System.out.println(position);
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
        for (Position position : solutionList) {
            map[position.getY()][position.getX()] = 2;
        }
    }

    public HashMap<Position, HashSet<Position>> getWatchedDictionary() {
        return watchedDictionary;
    }
}
