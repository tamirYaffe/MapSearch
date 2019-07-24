import Search.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Model {
    int[][] map;

    public void generateMap(int rows, int columns) {
//        MapGenerator mapGenerator=new MapGenerator();
//        map=mapGenerator.generate(rows,columns);
        int map[][] = { {0, 0, 0, 0, 0,1,1},
                        {1, 1, 1, 1, 0,1,1},
                        {0, 0, 0, 0, 0,1,1},
                        {0, 1, 0, 1, 0,1,1},
                        {0, 1, 0, 0, 0,1,1},
                        {0, 1, 0, 1, 1,1,1},
                        {0, 0, 0, 0,0,0, 2}};

        this.map = map;
    }

    public void solveMap() {

        System.out.println("---------- run 1 ----------");
        List<ASearch> solvers = new ArrayList<ASearch>();
        BreadthFirstSearch bfs = new BreadthFirstSearch();
        solvers.add(bfs);
        solveInstances(solvers, "roomMap");
    }

    private void solveInstances(List<ASearch> solvers, String instancesType) {
        {
            long totalTime = 0;
            String instance = "";
            RoomMap problem = new RoomMap(map);
            for (ASearch solver : solvers) {
                System.out.println("Solver: " + solver.getSolverName());
                long startTime = System.nanoTime();
                List<IProblemMove> solution = solver.solve(problem);
                long finishTime = System.nanoTime();
                double cost = checkSolution(problem, solution);
                if (cost >= 0)        // valid solution
                {
                    printSolution(problem, solution);
                    updateSolution(problem, solution);
                    System.out.println("Closed: " + solver.closed);
                    System.out.println("Cost:  " + cost);
                    System.out.println("Moves: " + solution.size());
                    System.out.println("Time:  " + (finishTime - startTime) / 1000000.0 + " ms");
                    System.out.println(solution);
                    totalTime += (finishTime - startTime) / 1000000.0;
                } else                // invalid solution
                    System.out.println("Invalid solution.");
            }
            System.out.println("");
            System.out.println("Total time:  " + totalTime / 60000.0 + " min");
            System.out.println("");
        }

    }


    public double checkSolution(IProblem instance, List<IProblemMove> solution) {
        if (solution == null)
            return -1;
        double cost = 0;
        IProblemState currentState = instance.getProblemState();
        for (IProblemMove move : solution) {
            currentState = currentState.performMove((RoomStep) move);
            if (currentState.getStateLastMove() != null)
                cost += currentState.getStateLastMoveCost();
        }
        if (currentState.isGoalState())
            return cost;
        return -1;
    }

    private void updateSolution(RoomMap problem, List<IProblemMove> solution) {
        IProblemState currentState = problem.getProblemState();
        map[((RoomMapState) currentState).getPosition().getY()][((RoomMapState) currentState).getPosition().getX()] = 2;
        for (IProblemMove move : solution) {
            RoomStep m = (RoomStep) move;
            currentState = currentState.performMove(m);
            map[((RoomMapState) currentState).getPosition().getY()][((RoomMapState) currentState).getPosition().getX()] = 2;
        }
        for (int i = 0; i < map.length; i++) {
            System.out.println(Arrays.toString(map[i]));
        }
    }

    public void printSolution(IProblem instance, List<IProblemMove> solution) {
        IProblemState currentState = instance.getProblemState();
        for (IProblemMove move : solution) {
            RoomStep m = (RoomStep) move;
            currentState = currentState.performMove(m);
            System.out.println(currentState);
        }
    }

}
