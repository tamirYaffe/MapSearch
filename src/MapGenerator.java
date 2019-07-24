import java.util.ArrayList;

public class MapGenerator {
    public int[][] generate(int row, int column) {
        int[][] maze;
        if(row<2 || column<2)
            maze= new int[10][10];
        else
            maze = new int[row][column];
        Position startPosition;
        setMazeAllWalls(maze);
        startPosition=generateRandomEntrancePosition(row,column);
        maze[startPosition.getRowIndex()][startPosition.getColumnIndex()]=0;
        generateMaze(maze,startPosition);
        return maze;
    }

    /**
     * generate a maze from the input maze(starts all walls) and start position.
     * @param maze- the maze we work on.
     * @param startPosition- start position in the maze.
     */
    private void generateMaze(int[][] maze, Position startPosition) {
        ArrayList<Position> walls=new ArrayList<>();
        addWalls(maze,walls,startPosition);
        while (!walls.isEmpty()){
            Position randomWall=getRandomWall(walls);// also removes the wall from the list
            connectWall(maze,walls,randomWall);
        }
    }

    /**
     * connects a wall to the rest of the maze.
     * @param maze- the maze we work on.
     * @param walls- array of walls to connect.
     * @param randomWall- the wall we wish to connect to the maze.
     */
    private void connectWall(int[][] maze, ArrayList<Position> walls, Position randomWall) {
        if(isEdgeWall(maze,randomWall)){
            connectEdgeWall(maze,walls,randomWall);
        }
        else{
            connectRegularWall(maze,walls,randomWall);
        }

    }

    /**
     * checks if the input wall is on the input maze edges.
     * @param maze- the maze we work on.
     * @param randomWall- the wall we check.
     * @return true if the input wall is on the input maze edges, otherwise false.
     */
    private boolean isEdgeWall(int[][] maze, Position randomWall) {
        Position top=new Position(randomWall.getRowIndex()-1,randomWall.getColumnIndex());
        Position bottom=new Position(randomWall.getRowIndex()+1,randomWall.getColumnIndex());
        Position left=new Position(randomWall.getRowIndex(),randomWall.getColumnIndex()-1);
        Position right=new Position(randomWall.getRowIndex(),randomWall.getColumnIndex()+1);
        return !positionOnMaze(maze,top) || !positionOnMaze(maze,right) || !positionOnMaze(maze,bottom) || !positionOnMaze(maze,left);
    }

    /**
     * connects the input edge wall to the rest of the maze.
     * @param maze- the maze we work on.
     * @param walls- array of walls to connect.
     * @param randomWall- the wall we wish to connect to the maze.
     */
    private void connectEdgeWall(int[][] maze, ArrayList<Position> walls, Position randomWall) {
        Position top=new Position(randomWall.getRowIndex()-1,randomWall.getColumnIndex());
        Position bottom=new Position(randomWall.getRowIndex()+1,randomWall.getColumnIndex());
        Position left=new Position(randomWall.getRowIndex(),randomWall.getColumnIndex()-1);
        Position right=new Position(randomWall.getRowIndex(),randomWall.getColumnIndex()+1);

        if(!positionOnMaze(maze,top) || !positionOnMaze(maze,bottom)){
            if(!checkTopBottomEdge(maze,walls,randomWall))
                checkLeftRightEdge(maze,walls,randomWall);
        }
        else{
            if(!checkLeftRightEdge(maze,walls,randomWall))
                checkTopBottomEdge(maze,walls,randomWall);
        }
        if(maze[randomWall.getRowIndex()][randomWall.getColumnIndex()]==1 && walls.size()<2){
            if(positionOnMaze(maze,left) && maze[left.getRowIndex()][left.getColumnIndex()]==0){
                maze[randomWall.getRowIndex()][randomWall.getColumnIndex()]=0;
                maze[right.getRowIndex()][right.getColumnIndex()]=0;
                addWalls(maze,walls,right);
            }
            if(positionOnMaze(maze,right) && maze[right.getRowIndex()][right.getColumnIndex()]==0){
                maze[randomWall.getRowIndex()][randomWall.getColumnIndex()]=0;
                maze[left.getRowIndex()][left.getColumnIndex()]=0;
                addWalls(maze,walls,left);
            }
            if(positionOnMaze(maze,top) && maze[top.getRowIndex()][top.getColumnIndex()]==0){
                maze[randomWall.getRowIndex()][randomWall.getColumnIndex()]=0;
                maze[bottom.getRowIndex()][bottom.getColumnIndex()]=0;
                addWalls(maze,walls,bottom);
            }
            if(positionOnMaze(maze,bottom) && maze[bottom.getRowIndex()][bottom.getColumnIndex()]==0){
                maze[randomWall.getRowIndex()][randomWall.getColumnIndex()]=0;
                maze[top.getRowIndex()][top.getColumnIndex()]=0;
                addWalls(maze,walls,top);
            }
        }

    }

    /**
     * connects the input wall to the rest of the maze.
     * @param maze- the maze we work on.
     * @param walls- array of walls to connect.
     * @param randomWall- the wall we wish to connect to the maze.
     */
    private void connectRegularWall(int[][] maze, ArrayList<Position> walls, Position randomWall) {
        //checks left-right
        if(Math.random()<0.5){
            if(!checkLeftRightRegular(maze,walls,randomWall))
                checkTopBottomRegular(maze,walls,randomWall);
        }
        //checks top-bottom
        else {
            if(!checkTopBottomRegular(maze,walls,randomWall))
                checkLeftRightRegular(maze,walls,randomWall);
        }
    }

    /**
     * checks if we the input random wall is separating between two passes(top and bottom) in the maze path.
     * if true connect the wall and the passage to the rest of the maze.
     * @param maze
     * @param walls
     * @param randomWall
     * @return true if we the input random wall is separating between two passes(top and bottom) in the maze path, otherwise false.
     */
    private boolean checkTopBottomRegular(int[][] maze, ArrayList<Position> walls, Position randomWall) {
        Position top=new Position(randomWall.getRowIndex()-1,randomWall.getColumnIndex());
        Position bottom=new Position(randomWall.getRowIndex()+1,randomWall.getColumnIndex());

        if(maze[top.getRowIndex()][top.getColumnIndex()]==0 && maze[bottom.getRowIndex()][bottom.getColumnIndex()]==1){
            maze[randomWall.getRowIndex()][randomWall.getColumnIndex()]=0;
            maze[bottom.getRowIndex()][bottom.getColumnIndex()]=0;
            addWalls(maze,walls,bottom);
            return true;
        }
        if(maze[top.getRowIndex()][top.getColumnIndex()]==1 && maze[bottom.getRowIndex()][bottom.getColumnIndex()]==0){
            maze[randomWall.getRowIndex()][randomWall.getColumnIndex()]=0;
            maze[top.getRowIndex()][top.getColumnIndex()]=0;
            addWalls(maze,walls,top);
            return true;
        }
        return false;
    }
    /**
     * checks if we the input random wall is separating between two passes(left and right) in the maze path.
     * if true connect the wall and the passage to the rest of the maze.
     * @param maze
     * @param walls
     * @param randomWall
     * @return true if we the input random wall is separating between two passes(left and right) in the maze path, otherwise false.
     */
    private boolean checkLeftRightRegular(int[][] maze, ArrayList<Position> walls, Position randomWall) {
        Position left=new Position(randomWall.getRowIndex(),randomWall.getColumnIndex()-1);
        Position right=new Position(randomWall.getRowIndex(),randomWall.getColumnIndex()+1);

        if(maze[left.getRowIndex()][left.getColumnIndex()]==0 && maze[right.getRowIndex()][right.getColumnIndex()]==1){
            maze[randomWall.getRowIndex()][randomWall.getColumnIndex()]=0;
            maze[right.getRowIndex()][right.getColumnIndex()]=0;
            addWalls(maze,walls,right);
            return true;
        }
        if(maze[left.getRowIndex()][left.getColumnIndex()]==1 && maze[right.getRowIndex()][right.getColumnIndex()]==0){
            maze[randomWall.getRowIndex()][randomWall.getColumnIndex()]=0;
            maze[left.getRowIndex()][left.getColumnIndex()]=0;
            addWalls(maze,walls,left);
            return true;
        }
        return false;
    }
    /**
     * checks if we the input random edge wall is separating between two passes(top and bottom) in the maze path.
     * if true connect the wall to the rest of the maze.
     * @param maze
     * @param walls
     * @param randomWall
     * @return true if we the input random wall is separating between two passes(top and bottom) in the maze path, otherwise false.
     */
    private boolean checkTopBottomEdge(int[][] maze, ArrayList<Position> walls, Position randomWall) {
        Position top=new Position(randomWall.getRowIndex()-1,randomWall.getColumnIndex());
        Position bottom=new Position(randomWall.getRowIndex()+1,randomWall.getColumnIndex());
        if((positionOnMaze(maze,top) && maze[top.getRowIndex()][top.getColumnIndex()]==0) && !positionOnMaze(maze,bottom)){
            maze[randomWall.getRowIndex()][randomWall.getColumnIndex()]=0;
            return true;
        }
        if((positionOnMaze(maze,bottom) && maze[bottom.getRowIndex()][bottom.getColumnIndex()]==0) && !positionOnMaze(maze,top)){
            maze[randomWall.getRowIndex()][randomWall.getColumnIndex()]=0;
            return true;
        }
        return false;
    }
    /**
     * checks if we the input random edge wall is separating between two passes(left and right) in the maze path.
     * if true connect the wall to the rest of the maze.
     * @param maze
     * @param walls
     * @param randomWall
     * @return true if we the input random wall is separating between two passes(left and right) in the maze path, otherwise false.
     */
    private boolean checkLeftRightEdge(int[][] maze, ArrayList<Position> walls, Position randomWall) {
        Position left=new Position(randomWall.getRowIndex(),randomWall.getColumnIndex()-1);
        Position right=new Position(randomWall.getRowIndex(),randomWall.getColumnIndex()+1);
        if((positionOnMaze(maze,left) && maze[left.getRowIndex()][left.getColumnIndex()]==0) && !positionOnMaze(maze,right)){
            maze[randomWall.getRowIndex()][randomWall.getColumnIndex()]=0;
            return true;
        }
        if((positionOnMaze(maze,right) && maze[right.getRowIndex()][right.getColumnIndex()]==0) && !positionOnMaze(maze,left)){
            maze[randomWall.getRowIndex()][randomWall.getColumnIndex()]=0;
            return true;
        }
        return false;
    }

    /**
     * returns a random wall from the input walls array.
     * @param walls- rray of walls
     * @return a random wall from the input walls array.
     */
    private Position getRandomWall(ArrayList<Position> walls) {
        int wallsNumber=walls.size();
        int randomWallIndex=randomWithRange(wallsNumber);
        return walls.remove(randomWallIndex);
    }

    /**
     * adds neighbors walls of the input position in the maze to the input walls array.
     * @param maze- the maze we work on.
     * @param walls- array of walls.
     * @param pos- position in the maze.
     */
    private void addWalls(int[][] maze, ArrayList<Position> walls, Position pos) {
        for (int i = -1; i <=1; i++)
            for (int j = -1; j <=1 ; j++)
                if(i!=j && !(i==1 && j==-1) && !(i==-1 && j==1) && positionOnMaze(maze,pos.getRowIndex()+i,pos.getColumnIndex()+j))
                    if(maze[pos.getRowIndex()+i][pos.getColumnIndex()+j]==1)//cell is a wall
                        walls.add(new Position(pos.getRowIndex()+i,pos.getColumnIndex()+j));
    }
    /**
     * checks if the input position row and column is in the maze limits.
     * @param maze- the maze we work with.
     * @param row- the current row position.
     * @param column- the current column position.
     * @return true if the input position row and column is in the maze limits, otherwise false.
     */
    private boolean positionOnMaze(int[][] maze, int row, int column) {
        return (row<maze.length && column<maze[0].length && row>=0 && column>=0);
    }
    /**
     * checks if the input position is in the maze limits.
     * @param maze- the maze we work with.
     * @param pos- the position to check
     * @return true if the input position is in the maze limits, otherwise false.
     */
    private boolean positionOnMaze(int[][] maze,Position pos) {
        return positionOnMaze(maze,pos.getRowIndex(),pos.getColumnIndex());
    }

    /**
     * returns a random int number in the input range.
     * @param range- the range of number to return a random number.
     * @return a random int number in the input range.
     */
    private int randomWithRange(int range) {
        return (int) (Math.random() * range);
    }
    /**
     * sets all input maze map with walls(1).
     * @param maze- the maze we work with.
     */
    private void setMazeAllWalls(int[][] maze) {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                maze[i][j] = 1;
            }
        }
    }
    /**
     * returns a random start Position in the limits of the input row and column size.
     * returned position is on border/edge.
     * @param rowSize-row size.
     * @param columnSize- column size.
     * @return a random start Position in the limits of the input row and column size.
     */
    private Position generateRandomEntrancePosition(int rowSize, int columnSize) {
        int startRow, startColumn;
        startRow = randomWithRange(rowSize);
        if (startRow == 0 || startRow == rowSize - 1)
            startColumn = randomWithRange(columnSize);
        else if (Math.random() < 0.5)
            startColumn = 0;
        else
            startColumn = columnSize-1;
        return new Position(startRow, startColumn);
    }
    /**
     * returns a random exit Position in the limits of the input row and column size that not equal to the input start position.
     * returned position is on border/edge.
     * @param rowSize-row size.
     * @param columnSize- column size.
     * @param startPosition- start position of the maze
     * @return a random exit Position in the limits of the input row and column size.
     */
    private Position generateRandomExitPosition(int[][] maze, Position startPosition, int rowSize, int columnSize) {
        int endRow, endColumn;
        do{
            endRow = randomWithRange(rowSize);
            if (endRow == 0 || endRow == rowSize - 1)
                endColumn = randomWithRange(columnSize);
            else if (Math.random() < 0.5)
                endColumn = 0;
            else
                endColumn = columnSize-1;
        }while (maze[endRow][endColumn]==1 || (endRow==startPosition.getRowIndex() && endColumn==startPosition.getColumnIndex()));
        return new Position(endRow, endColumn);
    }

    private class Position {
        private int row;
        private int column;

        /**
         * an empty constructor.
         */
        public Position() {
            this.row = 0;
            this.column = 0;
        }

        /**
         * a constructor using row and column.
         * @param row-the row position
         * @param column-the column position
         */
        public Position(int row, int column) {
            this.row = row;
            this.column = column;
        }

        /**
         * copy constructor
         * @param other- the other pposition to copy from
         */
        public Position(Position other) {
            this.row = other.row;
            this.column = other.column;
        }

        public Position(String s){
            String row="",column="";
            int i=0;
            s=s.substring(1);
            while(s.charAt(i)!=','){
                row+=s.charAt(i);
                i++;
            }
            i++;
            while(s.charAt(i)!='}'){
                column+=s.charAt(i);
                i++;
            }

            //<editor-fold desc="tamir added lines">
            if(row.charAt(0)=='{')
                row=row.substring(1);
            //</editor-fold>

            this.row = Integer.parseInt(row);
            this.column=Integer.parseInt(column);

        }

        //<editor-fold desc="getters and setters">
        public int getRowIndex() {
            return row;
        }

        public int getColumnIndex() {
            return column;
        }
        //</editor-fold>

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj == this) return true;
            if (!(obj instanceof Position))return false;
            Position other = (Position)obj;
            return row==other.row && column==other.column;
        }

        @Override
        public String toString() {
            return "{"+row+","+column+"}";
        }

    }

}
