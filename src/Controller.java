import Search.Position;
import View.MapGrid;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;

public class Controller {
    public Label xIndex;
    public Label yIndex;
    Model model;

    @FXML

    public TextField textField_rowSize;
    public TextField textField_columnSize;
    public Button btn_generateMap;
    public Button btn_solveMap;
    public Button btn_loadMap;
    public MapGrid mapGrid;
    public TextArea solution;


    public void setModel(Model model) {
        this.model = model;
        solution.setWrapText(true);
        solution.setEditable(false);
        solution.setMinHeight(250);
        solution.setMinWidth(150);
    }

    /**
     * Generates maze by the user rows and columns input in the text filed.
     */
    public void generateMap() {
        solution.setText("");
        String rowSize = textField_rowSize.getText();
        String columnSize = textField_columnSize.getText();

        if (isInteger(rowSize) && isInteger(columnSize) && Integer.valueOf(rowSize) > 4 && Integer.valueOf(columnSize) > 4) {
            int rows = Integer.valueOf(rowSize);
            int columns = Integer.valueOf(columnSize);
            model.generateMap(rows, columns);
            mapGrid.setMap(model.map, model.agent);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("input alert");
            alert.setHeaderText("Generate Maze");
            alert.setContentText("insert only numbers greater then 4");
            alert.show();
        }

    }

    private File loadMapFile(String location) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Map");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("map files", "*.map"));
        File file = checkIfExists(location);
        fc.setInitialDirectory(file);
        //showing the file chooser
        return fc.showOpenDialog(null);
    }

    public void loadMap(ActionEvent event) {
        String path = "";
        File file = loadMapFile(path);
        if (file != null) {
//            model.loadMap(new StringMapGenerator().generate(file),new Position(22,13));
            model.loadMap(new StringMapGenerator().generate(file), file.getName());
            mapGrid.setMap(model.map, model.agent);
        }
        event.consume();
    }

    private File checkIfExists(String location) {
        File file = new File(System.getProperty("user.dir") + "/" + location);
        if (!file.exists())
            file.mkdir();
        return file;
    }


    public void solveMap() {
        model.solveMap();
        mapGrid.setMap(model.map, model.agent);
        solution.setText(model.consoleString);
    }

    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void getFocus(MouseEvent mouseEvent) {
        mapGrid.requestFocus();
        double sizeX = mapGrid.getCellWidth();
        double sizeY = mapGrid.getCellHeight();
        double x = mouseEvent.getX() / sizeX;
        double y = mouseEvent.getY() / sizeY;
        xIndex.setText("X: " + (int) x);
        yIndex.setText("Y: " + (int) y);
        if (solution.getText() != "") {
            if (mouseEvent.isShiftDown()) {
                model.showBeforeMove();
            } else {
                model.showNextMove();
            }
            mapGrid.setMap(model.map, model.agent);
        }
    }

    public void KeyPressed(KeyEvent keyEvent) {
        System.out.println(keyEvent.getCode());
        if (solution.getText() != "") {
            if (keyEvent.getCode().toString() == "UP") {
                model.showNextMove();
            }
            if (keyEvent.getCode().toString() == "DOWN") {
                model.showBeforeMove();
            }
            if (keyEvent.getCode().toString() == "SPACE") {
                model.showAllSolution();
//                mapGrid.drawSolution(model.solutionList);
            }
            if (keyEvent.getCode().toString() == "ESCAPE") {
                System.exit(0);
            }
            if (keyEvent.getCode().toString() == "TAB") {
                btn_solveMap.fire();
            }
            mapGrid.setMap(model.map, model.agent);
        }
        keyEvent.consume();
    }

}
