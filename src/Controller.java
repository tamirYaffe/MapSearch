import View.MapGrid;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;

public class Controller {
    private Model model;

    @FXML
    public BorderPane borderPane;
    public MenuItem fileSaveButton;
    public MenuItem fileLoadButton;
    public Label xIndex;
    public Label yIndex;
    public TextField textField_rowSize;
    public TextField textField_columnSize;
    public TextField textField_distanceFactor;
    public Button btn_generateMap;
    public Button btn_solveMap;
    public Button btn_loadMap;
    public MapGrid mapGrid;
    public TextArea solution;
    public ChoiceBox<String> los;
    public ChoiceBox<String> heuristics;
    public ChoiceBox<String> movements;
    public ChoiceBox<String> heuristicGraph;

    void setModel(Model model) {
        String[] heuristicsArray = {"Zero", "Singleton", "MST", "MSP", "TSP"};
        String[] heuristicGraphArray = {"All", "Frontiers", "Front Frontiers","Farther Frontiers"};
        String[] movementsArray = {"4-way", "8-way", "Jump", "Jump (Bounded)", "Expanding Border"};
        String[] losArray = {"4-way", "8-way", "Symmetric BresLos", "Asymmetric BresLos"};
        los.setItems(FXCollections.observableArrayList(losArray));
        heuristics.setItems(FXCollections.observableArrayList(heuristicsArray));
        movements.setItems(FXCollections.observableArrayList(movementsArray));
        heuristicGraph.setItems(FXCollections.observableArrayList(heuristicGraphArray));
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

        if (isInteger(rowSize) && isInteger(columnSize) && Integer.parseInt(rowSize) > 4 && Integer.parseInt(columnSize) > 4) {
            int rows = Integer.parseInt(rowSize);
            int columns = Integer.parseInt(columnSize);
//            model.generateMap(rows, columns);
            model.generateMap();
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
//            model.loadMap(new StringMapGenerator().generate(file),new Position(yIndex,xIndex));
            model.loadMap(new StringMapGenerator().generate(file), file.getName());
            mapGrid.setMap(model.map, model.agent);
        }
        event.consume();
    }

    private File checkIfExists(String location) {
        File file = new File(System.getProperty("user.dir") + "/" + location);
        if (!file.exists()) {
            boolean mkdir = file.mkdir();
            if (!mkdir) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("mkdir alert");
                alert.setHeaderText("Creating directory failed");
                alert.setContentText("Please Try again");
                alert.show();
            }
        }
        return file;
    }


    public void solveMap() {
        if (textField_distanceFactor.getText() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("input alert");
            alert.setHeaderText("LOS Selection");
            alert.setContentText("Please insert Line of Sight method.");
            alert.show();
        } else if (los.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("input alert");
            alert.setHeaderText("LOS Selection");
            alert.setContentText("Please insert Line of Sight method.");
            alert.show();
        } else if (heuristics.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("input alert");
            alert.setHeaderText("Heuristic Selection");
            alert.setContentText("Please insert Heuristic");
            alert.show();
        } else if (movements.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("input alert");
            alert.setHeaderText("Movement Selection");
            alert.setContentText("Please insert Movement method");
            alert.show();
        } else if (heuristicGraph.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("input alert");
            alert.setHeaderText("Heuristic Graph Selection");
            alert.setContentText("Please insert Heuristic Graph method");
            alert.show();
        } else {
            model.solveMap(movements.getValue(), heuristics.getValue(), los.getValue(), heuristicGraph.getValue(),Double.parseDouble(textField_distanceFactor.getText()));
            mapGrid.setMap(model.map, model.agent);
            solution.setText(model.consoleString);
        }
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
        if (mapGrid.isSet()) {
            mapGrid.requestFocus();
            double sizeX = mapGrid.getCellWidth();
            double sizeY = mapGrid.getCellHeight();
            double x = mouseEvent.getX() / sizeX;
            double y = mouseEvent.getY() / sizeY;
            xIndex.setText("X: " + (int) x);
            yIndex.setText("Y: " + (int) y);
            if (solution.getText() != null && !solution.getText().equals("")) {
                if (mouseEvent.isShiftDown()) {
                    model.showBeforeMove();
                } else {
                    model.showNextMove();
                }
                mapGrid.setMap(model.map, model.agent);
            }
        }
    }

    public void KeyPressed(KeyEvent keyEvent) {
        System.out.println(keyEvent.getCode());
        if (solution.getText() != null && !solution.getText().equals("")) {
            if (keyEvent.getCode().getName().equals("Up")) {
                model.showNextMove();
            }
            if (keyEvent.getCode().getName().equals("Down")) {
                model.showBeforeMove();
            }
            if (keyEvent.getCode().getName().equals("Space")) {
                model.showAllSolution();
//                mapGrid.drawSolution(model.solutionList);
            }
            if (keyEvent.isShiftDown() && keyEvent.getCode().getName().equals("P")) {
                model.printSolution();
            }
        }
        if (keyEvent.getCode().getName().equals("Esc")) {
            System.exit(0);
        }
        if (keyEvent.getCode().getName().equals("Tab")) {
            btn_solveMap.fire();
        }
        if (model.map != null && model.agent != null)
            mapGrid.setMap(model.map, model.agent);
        if (keyEvent.isControlDown() && keyEvent.getCode().getName().equals("A")) {
            btn_solveMap.requestFocus();
            movements.setValue("Jump (Bounded)");
            heuristics.setValue("MST");
            heuristicGraph.setValue("Farther Frontiers");
            los.setValue("Symmetric BresLos");
//            btn_solveMap.fire();
        }if (keyEvent.isControlDown() && keyEvent.getCode().getName().equals("S")) {
            btn_solveMap.requestFocus();
            movements.setValue("Expanding Border");
            heuristics.setValue("Zero");
            heuristicGraph.setValue("All");
            los.setValue("4-way");
            btn_solveMap.fire();
        }if (keyEvent.isControlDown() && keyEvent.getCode().getName().equals("D")) {
            btn_solveMap.requestFocus();
            movements.setValue("4-way");
            heuristics.setValue("MST");
            heuristicGraph.setValue("All");
            los.setValue("Symmetric BresLos");
            btn_solveMap.fire();
        }
        keyEvent.consume();
    }

}
