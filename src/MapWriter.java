import java.io.*;

public class MapWriter {

    public String getMapStr(int[][] grid) {
        StringBuilder ans = new StringBuilder();
        ans.append("type octile\n");
        ans.append("height ").append(grid.length).append("\n");
        ans.append("width ").append(grid[0].length).append("\n");
        ans.append("map\n");
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                ans.append(grid[j][i] == 0?'.':'@');
            }
            ans.append("\n");
        }
        return ans.toString();
    }

    public void createFiles(Model model, String path, String name) {
        createDir(path);
        String mapStr = getMapStr(model.map);
        File mapFile = new File( "Maps/" + path + "/" + name + ".map");
        try {
            mapFile.createNewFile();
            writeToFile(mapFile, mapStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(File file, String str) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(str);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDir(String dirName) {
        File theDir = new File("Maps/" +dirName);
        if (!theDir.exists()) {
            try {
                theDir.mkdir();
            } catch (SecurityException se) {
                se.getCause();
            }
        }
    }

}
