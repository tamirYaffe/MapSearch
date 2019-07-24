public class Model {
    int[][] map;
    public void generateMap(int rows, int columns) {
//        MapGenerator mapGenerator=new MapGenerator();
//        map=mapGenerator.generate(rows,columns);
        int map[][] = { {0,0,0,0,0},
                        {0,1,0,1,0},
                        {0,1,0,0,0},
                        {0,1,0,1,1},
                        {0,0,0,0,2} };
        this.map=map;
    }

    public void solveMap() {

    }
}
