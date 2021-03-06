package View;

import Search.Position;
import javafx.geometry.Pos;
import javafx.scene.Node;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.image.Image;
//import java.util.ArrayList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;


public class MapGrid extends GridPane {
    private int[][] map;


    public double getCellHeight() {
        return this.getHeight() / map.length;
    }

    public double getCellWidth() {
        return this.getWidth() / map[0].length;
    }


    public void setMap(int[][] map, Position agent) {
        this.map = map;
        int numCols = map[0].length;
        int numRows = map.length;
        getChildren().clear();
        getRowConstraints().clear();
        getColumnConstraints().clear();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                add(drawCell(i, j), j, i);
//                if (map[i][j] == 2)
//                    add(drawCharacter(), j, i);
            }
        }
        //drawing chracter
        add(drawCharacter(), agent.getX(), agent.getY());
    }

    private Node drawCharacter() {
        //        Region charcter = setRegion("-fx-background-image: url('/images/drone.png');");
        return setRegion("-fx-background-image: url('/images/green.png');");
    }

//    public Node drawSolution() {
//        Region solutionCell = setRegion("-fx-background-image: url('/images/sol_coin.png');");
//        return solutionCell;
//    }


    private Node drawCell(int cellPositionRow, int cellPositionColumn) {
        Region cell = setRegion();
        if (map[cellPositionRow][cellPositionColumn] == 1)
            cell.setStyle("-fx-background-image: url('/images/black.png');-fx-background-repeat: no-repeat; -fx-background-size: cover,auto");
//            cell.setStyle("-fx-background-image: url('/images/wall2.jpg');-fx-background-repeat: no-repeat; -fx-background-size: cover,auto");
        else if (map[cellPositionRow][cellPositionColumn] == 4)
            cell.setStyle("-fx-border-color: black; -fx-background-image: url('/images/grey.png');-fx-background-repeat: no-repeat; -fx-background-size: cover,auto");
//            cell.setStyle("-fx-background-image: url('/images/wall2.jpg');-fx-background-repeat: no-repeat; -fx-background-size: cover,auto");
        else if (map[cellPositionRow][cellPositionColumn] == 3)
            cell.setStyle("-fx-border-color: black; -fx-background-image: url('/images/blue.png');-fx-background-repeat: no-repeat; -fx-background-size: cover,auto");
//            cell.setStyle("-fx-background-image: url('/images/wall2.jpg');-fx-background-repeat: no-repeat; -fx-background-size: cover,auto");
        else if (map[cellPositionRow][cellPositionColumn] == 2)
            cell.setStyle(" -fx-border-color: black; -fx-background-color: red;-fx-background-repeat: no-repeat; -fx-background-size: cover,auto");
//            cell.setStyle("-fx-background-image: url('/images/wall2.jpg');-fx-background-repeat: no-repeat; -fx-background-size: cover,auto");
        else
            cell.setStyle("-fx-border-color: black; -fx-background-image: url('/images/white.png');-fx-background-repeat: no-repeat; -fx-background-size: cover,auto");
        return cell;
    }

    /**
     * Sets and returns a region with basic settings and input style implementation.
     *
     * @param style - the region style to implement.
     * @return - region with basic settings and input style implementation.
     */
    private Region setRegion(String style) {
        Region region = new Region();
        region.setMaxSize(300, 300);
        region.setMinSize(1, 1);
        region.setPrefSize(300, 300);
        region.setStyle(String.format("%s -fx-background-repeat: no-repeat; -fx-background-size: cover,auto", style));
        return region;
    }

    /**
     * Sets and returns a region with basic settings.
     *
     * @return - a region with basic settings.
     */
    private Region setRegion() {
        Region region = new Region();
        region.setMaxSize(300, 300);
        region.setMinSize(1, 1);
        region.setPrefSize(300, 300);
        return region;
    }

//    public void drawSolution(ArrayList<Position> solutionList) {
//        Canvas canvas = new Canvas();
//        getParent().getChildrenUnmodifiable().add(canvas);
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        gc.drawImage(Image.impl_fromPlatformImage("/images/blank2.jpg"), 0, 0);
//    }

    public boolean isSet() {
        return map!=null;
    }

    public void showWatchers(HashMap<Position, HashSet<Position>> watchedDictionary, int x, int y) {
        if (watchedDictionary!=null){
            Position pivot = new Position(y,x);
            HashSet<Position> watchers = watchedDictionary.get(pivot);
            for (Position watcher : watchers) {
                if (map[watcher.getY()][watcher.getX()]!=2 && map[watcher.getY()][watcher.getX()]!=1 && map[watcher.getY()][watcher.getX()]!=4 && map[watcher.getY()][watcher.getX()]!=3) {
                    Region watcherCell = setRegion();
                    watcherCell.setStyle("-fx-border-color: black; -fx-background-image: url('/images/grey.png');-fx-background-repeat: no-repeat; -fx-background-size: cover,auto");
                    add(watcherCell, watcher.getX(), watcher.getY());
                }
            }
            Region pivotCell = setRegion();
            pivotCell.setStyle("-fx-border-color: black; -fx-background-image: url('/images/blue.png');-fx-background-repeat: no-repeat; -fx-background-size: cover,auto");
            add(pivotCell,pivot.getX(),pivot.getY());
        }
    }
}
