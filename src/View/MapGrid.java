package View;

import Search.Position;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.util.ArrayList;

public class MapGrid extends GridPane {
    private int[][] map;
    private Canvas canvas;


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
                if (map[i][j] == 2)
                    add(drawCharacter(), j, i);
            }
        }
        //drawing chracter
        add(drawCharacter(), agent.getX(), agent.getY());
    }

    public Node drawCharacter() {
        Region charcter = setRegion("-fx-background-image: url('/drone.png');");
        return charcter;
    }

    public Node drawSolution() {
        Region solutionCell = setRegion("-fx-background-image: url('/sol_coin.png');");
        return solutionCell;
    }


    public Node drawCell(int cellPositionRow, int cellPositionColumn) {
        Region cell = setRegion();
        if (map[cellPositionRow][cellPositionColumn] == 1)
            cell.setStyle("-fx-background-image: url('/wall2.jpg');-fx-background-repeat: no-repeat; -fx-background-size: cover,auto");
        else
            cell.setStyle("-fx-background-image: url('/blank2.png');-fx-background-repeat: no-repeat; -fx-background-size: cover,auto");
        return cell;
    }

    /**
     * Sets and returns a region with basic settings and input style implementation.
     *
     * @param style - the region style to implement.
     * @returna - region with basic settings and input style implementation.
     */
    private Region setRegion(String style) {
        Region region = new Region();
        region.setMaxSize(300, 300);
        region.setMinSize(1, 1);
        region.setPrefSize(300, 300);
        region.setStyle(style + "-fx-background-repeat: no-repeat; -fx-background-size: cover,auto");
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

    public void drawSolution(ArrayList<Position> solutionList) {
        canvas = new Canvas();
        getParent().getChildrenUnmodifiable().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(Image.impl_fromPlatformImage("/blank2.jpg"),0,0);
    }
}
