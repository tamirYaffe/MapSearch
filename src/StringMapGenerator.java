import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

public class StringMapGenerator {

    public int[][] generate(Object o) {
        if (!(o instanceof File))
            return null;
        BufferedReader br;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader((File) o));
            Stream<String> s = br.lines();
            s.forEach(s1 -> sb.append(s1).append("\n"));
            if (((File) o).getName().endsWith("map")) {
                return generate(sb.toString());
            } else if (((File) o).getName().startsWith("Instance")) {
                return generateInstance(sb.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        }
        return generate(sb.toString());
    }

    public int[][] generate(String mapStr) {
        int rowsNum, colNum;
        int counter = 0;
        counter = skipToNum(mapStr, counter);
        rowsNum = getNum(mapStr, counter);
        counter += (int) Math.log10(rowsNum) + 1;
        counter = skipToNum(mapStr, counter);
        colNum = getNum(mapStr, counter);
        counter += Math.log10(colNum) + 6;
        String[] rows = mapStr.substring(counter).split("\n");
        int[][] map = new int[rowsNum][colNum];
        for (int row = 0; row < rowsNum; row++) {
            for (int col = 0; col < colNum; col++, counter++) {
                map[row][col] = (rows[row].charAt(col) == '.' || rows[row].charAt(col) == 'G') ? 0 : 1;
            }
        }
        return cleanMap(map);
//        return map;
    }

    public int[][] generateInstance(String mapStr) {

        String[] splited = mapStr.split("\n");
        String[] rowcol = splited[2].split(",");
        int rows = extractNumber(rowcol[0]);
        int cols = extractNumber(rowcol[1]);
        int[][] map = new int[rows][cols];
        for (int row = 0; row < rows && row + 3 < splited.length; row++) {
            for (int col = 0; col < cols; col++) {
                map[row][col] = (splited[row + 3].charAt(col) == '.' || splited[row + 3].charAt(col) == 'G') ? 0 : 1;
            }
        }
        return cleanMap(map);
//        return map;
    }


    private int extractNumber(String num) {
        try {
            return Integer.parseInt(num);
        } catch (Exception e) {
            //System.out.println("Couldn't read Numbers in: \"" + num + "\"");
            e.printStackTrace();
        }
        return 0;
    }

    private int[][] cleanMap(int[][] map) {
        int[] redundantRow = new int[map.length], redundantCol = new int[map[0].length];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == 1) {
                    redundantCol[j]++;
                    redundantRow[i]++;
                }
            }
        }
        int firstRow = 0, lastRow = map.length - 1, firstCol = 0, lastCol = map[0].length - 1;
        for (int i = 0; i < redundantRow.length; i++) {
            int i1 = redundantRow[i];
            if (i1 == map[0].length)
                firstRow++;
            else break;
        }
        for (int j = 0; j < redundantCol.length; j++) {
            int i2 = redundantCol[j];
            if (i2 == map.length)
                firstCol++;
            else break;
        }
        for (int i = redundantRow.length - 1; i >= 0; i--) {
            int x = redundantRow[i];
            if (x == map[0].length)
                lastRow--;
            else break;
        }
        for (int i = redundantCol.length - 1; i >= 0; i--) {
            int x = redundantCol[i];
            if (x == map.length)
                lastCol--;
            else break;
        }
        int[][] newMap = new int[lastRow - firstRow + 1][lastCol - firstCol + 1];
        for (int i = 0; i < newMap.length; i++) {
            for (int j = 0; j < newMap[0].length; j++) {
                newMap[i][j] = map[firstRow + i][firstCol + j];
            }
        }
        return newMap;
//        boolean[] redundentRows = new boolean[map.length];
//        boolean[] redundentCols = new boolean[map[0].length];
//        Arrays.fill(redundentCols, true);
//        Arrays.fill(redundentRows, true);
//        int essentialRowCount = map.length, essentialColCount = map[0].length;
//        for (int i = 0; i < map.length; i++) {
//            for (int j = 0; j < map[0].length; j++) {
//                if (map[i][j] != 1) {
//                    redundentCols[j] = false;
//                    redundentRows[i] = false;
//                    essentialRowCount--;
//                    essentialColCount--;
//                }
//            }
//        }
//        int[][] newMap = new int[essentialRowCount][essentialColCount];
//        for (int i = 0; i < ; i++) {
//
//        }
    }

    private int getNum(String str, int counter) {
        int res = 0;
        while (Character.isDigit(str.charAt(counter))) {
            res = res * 10 + Integer.parseInt("" + str.charAt(counter));
            counter++;
        }
        return res;
    }

    private int skipToNum(String mapStr, int counter) {
        while (!Character.isDigit(mapStr.charAt(counter))) {
            counter++;
        }
        return counter;
    }


}
