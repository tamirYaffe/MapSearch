import Search.Position;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class creates random Maze with Prim algorithm for generating mazes.
 */
public class MazeGenerator {


    private int[][] grid;
    private Position startPosition;
    protected HashMap<Position, Position> parentMap = new HashMap<>();//key - position, value- parent position

    public MazeGenerator() {
    }

    public int[][] generate(int row, int col) {
        if (row < 1 || col < 1)
            return null;

        // Build grid and initialize with only walls (1)
        grid = new int[row][col];
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                grid[y][x] = 1;
            }
        }

        // Select random point and open as start node
        startPosition = new Position(0, 0);
        grid[0][0] = 0;

        // Iterate through direct neighbors of node
        ArrayList<Position> frontier = new ArrayList<>();
        iterate(grid, startPosition, frontier);

        while (!frontier.isEmpty()) {

            // Pick current node at random
            Position cu = frontier.remove((int) (Math.random() * frontier.size()));
            Position op = opposite(cu);
            try {
                // If both node and its opposite are walls
                if (grid[cu.getY()][cu.getX()] == 1) {
                    if (grid[op.getY()][op.getX()] == 1) {
                        // Open path between the nodes
                        grid[cu.getY()][cu.getX()] = 0;
                        grid[op.getY()][op.getX()] = 0;
                        // Iterate through direct neighbors of node, same as earlier
                        iterate(grid, op, frontier);
                    }
                }
            } catch (Exception e) { // Ignore NullPointer and ArrayIndexOutOfBounds
            }
        }

        try {
            return grid;
        } catch (Exception e) {
            return null;
        }
    }

    private void iterate(int[][] maz, Position p, ArrayList<Position> frontier) {
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0 || x != 0 && y != 0)
                    continue;
                try {
                    if (maz[p.getY() + x][p.getX() + y] == 0) continue;
                } catch (Exception e) { // ignore ArrayIndexOutOfBounds
                    continue;
                }
                // Add eligible points to frontier
                Position c = new Position(p.getY() + x, p.getX() + y);
                frontier.add(c);
                parentMap.put(c, p);

            }
    }

    /**
     * [1][a][1]
     * [1][b][1]
     * [1][c][1]
     * In this example:
     * 'c' is the Parent of 'b'
     * => 'a' is the Opposite of 'b'
     * "Opposite from the Parent's side"
     */
    private Position opposite(Position p) {
        Position parent = parentMap.getOrDefault(p, null);
        if (p != null && parent != null) {
            if (p.getY() != parent.getY())
                return new Position(p.getY() + (p.getY() > parent.getY() ? 1 : -1), p.getX());
            if (p.getX() != parent.getX())
                return new Position(p.getY(), p.getX() + (p.getX() > parent.getX() ? 1 : -1));
        }
        return null;
    }


    public Position getStartPosition() {
        return startPosition;
    }
}


//public class MazeGenerator {
//
//    private int [][] grid;
//    private Position startPos;
//    protected ArrayList<Position> neighbors = new ArrayList<>();
//
//    /**
//     * ganerate a new maze
//     * @param rows - the number of rows that the maze is going to have
//     * @param columns - the number of columns that the maze is going to have
//     * @return Maze
//     */
//    public int [][] generate(int rows, int columns) {
//        if (rows <= 1 || columns <= 1){
//            rows = 100;
//            columns = 100;
//        }
//        grid = new int[rows][columns];
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                grid[i][j] = 1;
//            }
//        }
//
//        //get the startPos position (randomly)
//        startPos = findRandomPositionOnFrame(rows, columns);
//        grid[startPos.getY()][startPos.getX()] = 0;
//        neighbors.add(startPos);
//
//
//        //add cells with value 0 to maze
//        int count = 0;
//        boolean flag = false;
//        while (!neighbors.isEmpty()) {
//
//            int r = (int) (Math.random() * neighbors.size()); // find random neighbor from list
//            Position pos = new Position( neighbors.get(r)); // save random position
//            neighbors.remove(r); // remove random position from list
//
//            //break wall if allowed and check that only one or less neighbors with value 0
//            count = 0;
//            int posRow = pos.getY();
//            int posCol = pos.getX();
//            count = countWallBreaks(grid,0, posRow, posRow-1, posCol, count);
//            count = countWallBreaks(grid, 0, posCol, posRow, posCol-1,count);
//            count = countWallBreaks(grid, posRow+1, grid.length, posRow+1, posCol, count);
//            count = countWallBreaks(grid, posCol+1, grid[0].length, posRow, posCol+1, count);
//            if (count <= 1) {
//                grid[posRow][posCol] = 0;
//                flag = true;
//            }
//
//            //add valid neighbors to list, only if pos was a valid neighbor and value was 1 before
//            if (flag) {
//                addValidNeighbors(grid, 0, posRow, posRow-1, posCol);
//                addValidNeighbors(grid, 0, posCol, posRow, posCol-1);
//                addValidNeighbors(grid, posRow+1, grid.length, posRow+1, posCol);
//                addValidNeighbors(grid, posCol+1, grid[0].length, posRow, posCol+1);
//                flag = false;
//            }
//            for (int i = 0; i < grid.length; i++) {
//                System.out.print("[");
//                for (int j = 0; j < grid[0].length; j++) {
//                    System.out.print("["+grid[i][j]+"]");
//                }
//                System.out.println("]");
//            }
//            System.out.println("\n");
//        }
//
//        Position goal = findRandomPositionOnFrame(rows, columns);
//
//        // while the startPos and goal are on the same side of the frame or the goal is on a wall, find another goal position
//        while (goal.getX()==startPos.getX() ||
//                goal.getY()==startPos.getY() ||
//                grid[goal.getY()][goal.getX()]!=0)
//            goal = findRandomPositionOnFrame(rows, columns);
//
//        //initialize maze with final grid
//        return grid;
//    }
//
//    /**
//     * add the valid neighbors to the list if it had the value 1
//     * @param grid - the grid of the map
//     * @param pos - the index that could be out of range
//     * @param limit - the range that pos cant pass
//     * @param i - row index
//     * @param j - column index
//     */
//    private void addValidNeighbors(int[][] grid, int pos, int limit, int i, int j) {
//        if (pos< limit && grid[i][j]==1){
//            Position tmp = new Position(i,j);
//            neighbors.add(tmp);
//        }
//    }
//
//    /**
//     * check if its OK to break a wall (if only 1 neighbor has the value 0)
//     * @param grid - the grid of the map
//     * @param pos - the index that could be out of range
//     * @param limit - the range that pos cant pass
//     * @param i - row index
//     * @param j - column index
//     * @param count - the counter
//     * @return count or count++ depends if passed the check
//     */
//    private int countWallBreaks(int [][] grid, int pos, int limit, int i, int j, int count) {
//        return pos < limit && grid[i][j]==0? count+1: count;
//    }
//
//    /**
//     * find a random position on the frame of a 2D array (based on math)
//     * @param rows - the number of rows the 2D array has
//     * @param columns - the number of columns the 2D array has
//     * @returns the random position on the frame
//     */
//    private Position findRandomPositionOnFrame(int rows, int columns) {
//        if (rows < 0 || columns < 0) return null;
//        Position pos;
//        int frameSide = (int) (Math.random() * 4); // choose a side of the frame
//        if (frameSide % 2 == 0) {                  // if its an even number
//            int onframe = (int) (Math.random() * columns);
//            if (frameSide == 0)               // if its top frame side
//                pos = new Position(0, onframe);
//            else pos = new Position(rows - 1, onframe);
//        } else {
//            int onframe = (int) (Math.random() * rows);
//            if (frameSide == 1)
//                pos = new Position(onframe, 0);
//            else pos = new Position(onframe, columns - 1);
//        }
//        return pos;
//    }
//
//    public Position getStartPos() {
//        return startPos;
//    }
//}
