import Search.HeuristicSearchNode;
import Search.Position;
import View.MapGrid;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Arrays;

public class Controller {

    private Model model;

    private int densMapIndex = 0;
    private File densMapDir = null;

    @FXML
    public BorderPane borderPane;
    public MenuItem fileSaveButton;
    public MenuItem fileLoadButton;
    public Label xIndex;
    public Label yIndex;
    public TextField textField_rowSize;
    public TextField textField_columnSize;
    public TextField textField_distanceFactor;
    public TextField textField_weight;
    public Button btn_generateMap;
    public Button btn_solveMap;
    public Button btn_loadMap;
    public MapGrid mapGrid;
    public TextArea solution;
    public ChoiceBox<String> los;
    public ChoiceBox<String> heuristics;
    public ChoiceBox<String> movements;
    public ChoiceBox<String> heuristicGraph;
    public ChoiceBox<String> algorithm_choiceBox;
    public CheckBox check_No_Whites;
    public CheckBox check_Farthest;
    public CheckBox check_Bounded;
    public CheckBox check_computeAllPaths;


    void setModel(Model model) {
        String[] heuristicsArray = {"Zero", "Singleton", "MST", "MSP", "TSP"};
        String[] heuristicGraphArray = {"All", "Frontiers", "Front Frontiers"};
        String[] movementsArray = {"4-way", "8-way", "Jump"};
        String[] losArray = {"4-way", "8-way", "Symmetric BresLos", "Asymmetric BresLos"};
        String[] algorithmArray = {"WA*", "XUP", "XDP"};
        los.setItems(FXCollections.observableArrayList(losArray));
        heuristics.setItems(FXCollections.observableArrayList(heuristicsArray));
        movements.setItems(FXCollections.observableArrayList(movementsArray));
        heuristicGraph.setItems(FXCollections.observableArrayList(heuristicGraphArray));
        algorithm_choiceBox.setItems(FXCollections.observableArrayList(algorithmArray));
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

        if (isInteger(rowSize) && isInteger(columnSize)) {
//        if (isInteger(rowSize) && isInteger(columnSize) && Integer.parseInt(rowSize) > 4 && Integer.parseInt(columnSize) > 4) {
            int rows = Integer.parseInt(rowSize);
            int columns = Integer.parseInt(columnSize);
            if (rows < 4 && columns < 4) {
                model.generateMap();
            } else {

                model.generateMap(rows, columns);
            }
            mapGrid.setMap(model.map, model.agent);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("input alert");
            alert.setHeaderText("Generate Maze");
            alert.setContentText("insert only numbers!");
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

    private File showMaps(String location) {
        if (densMapDir == null || densMapIndex == 0) {
            FileChooser fc = new FileChooser();
            fc.setTitle("Load Map");
            File dir = new File(System.getProperty("user.dir") + "/Maps/DensMaps/" + location);
            fc.setInitialDirectory(dir);
            //showing the file chooser
            densMapDir = fc.showOpenDialog(null).getParentFile();
            if (densMapDir != null && densMapDir.isDirectory()) {
                densMapIndex = densMapDir.listFiles().length;
            }
        }
        if (densMapIndex > 0 && densMapDir != null) {
            File file = new File(densMapDir.getPath() + "/Density Graph Map " + (densMapIndex--) + " obstacles.map");
            while (file == null && densMapIndex > 0) {
                file = new File(densMapDir.getPath() + "/Density Graph Map " + (densMapIndex--) + " obstacles.map");
            }
            if (file != null) {
                model.loadMap(new StringMapGenerator().generate(file), file.getName());
                mapGrid.setMap(model.map, model.agent);
            }
        }
        return null;
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
        } else if (algorithm_choiceBox.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("input alert");
            alert.setHeaderText("Algorithm Selection");
            alert.setContentText("Please insert Algorithm to know the F computation method");
            alert.show();
        } else {
            model.solveMap(movements.getValue(), heuristics.getValue(), los.getValue(), heuristicGraph.getValue(),
                    algorithm_choiceBox.getValue(), Double.parseDouble(textField_distanceFactor.getText()),
                    Double.parseDouble(textField_weight.getText()), check_No_Whites.isSelected(),
                    check_Farthest.isSelected(), check_Bounded.isSelected(), check_computeAllPaths.isSelected());
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
            if (mouseEvent.isShiftDown()) {
                mapGrid.showWatchers(model.getWatchedDictionary(), (int) x, (int) y);
            } else if (solution.getText() != null && !solution.getText().equals("")) {
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
//        System.out.println(keyEvent.getCode());
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
            movements.setValue("Jump");
            heuristics.setValue("TSP");
            heuristicGraph.setValue("Frontiers");
            los.setValue("Symmetric BresLos");
            algorithm_choiceBox.setValue("WA*");
//            textField_distanceFactor.setText("1.2");
            textField_weight.setText("1");
            textField_rowSize.setText("15");
            textField_columnSize.setText("15");
            check_No_Whites.setSelected(false);
            check_Farthest.setSelected(false);
            check_Bounded.setSelected(false);
            check_computeAllPaths.setSelected(true);
            File file = new File("resources/SavedMaps/maze_15X15.map");
            model.loadMap(new StringMapGenerator().generate(file), file.getName());
            model.agent = new Position(10,10);
            mapGrid.setMap(model.map, model.agent);
            btn_solveMap.fire();
        }
        if (keyEvent.isControlDown() && keyEvent.getCode().getName().equals("S")) {
            btn_solveMap.requestFocus();
            movements.setValue("Jump");
            heuristics.setValue("Zero");
            heuristicGraph.setValue("All");
            los.setValue("4-way");
//            btn_solveMap.fire();
        }
        if (keyEvent.isControlDown() && keyEvent.getCode().getName().equals("D")) {
            btn_solveMap.requestFocus();
            movements.setValue("Jump");
            heuristics.setValue("TSP");
            heuristicGraph.setValue("Frontiers");
            los.setValue("Symmetric BresLos");
            algorithm_choiceBox.setValue("WA*");
//            textField_distanceFactor.setText("1.2");
            textField_weight.setText("1");
            textField_rowSize.setText("15");
            textField_columnSize.setText("15");
            check_No_Whites.setSelected(true);
            check_Farthest.setSelected(true);
            check_Bounded.setSelected(true);
            File file = new File("resources/SavedMaps/den101d.map");
            model.loadMap(new StringMapGenerator().generate(file), file.getName());
            model.agent = new Position(0, 39);
            mapGrid.setMap(model.map, model.agent);
//            btn_solveMap.fire();
        }
        if (keyEvent.isControlDown() && keyEvent.getCode().getName().equals("F")) {
            movements.setValue("Jump");
            heuristics.setValue("TSP");
            heuristicGraph.setValue("Frontiers");
            los.setValue("Symmetric BresLos");
            textField_rowSize.setText("15");
            textField_columnSize.setText("15");
            model.densityGraphBuilder(15, 15);
        }
        if (keyEvent.isControlDown() && keyEvent.getCode().getName().equals("M")) {
            showMaps("");
        }
        if (keyEvent.isControlDown() && keyEvent.getCode().getName().equals("W")) {
            if (movements.getValue().equals("Jump")) {
                movements.setValue("4-way");
            } else {
                movements.setValue("Jump");
            }
        }
        if (keyEvent.isControlDown() && keyEvent.getCode().getName().equals("Q")) {
            if (los.getValue().equals("4-way")) {
                los.setValue("8-way");
            } else if (los.getValue().equals("8-way")) {
                los.setValue("Symmetric BresLos");
            } else {
                los.setValue("4-way");
            }
        }

        if (keyEvent.isControlDown() && keyEvent.getCode().getName().equals("E")) {
            if (heuristics.getValue().equals("Zero")) {
                heuristics.setValue("Singleton");
            } else if (heuristics.getValue().equals("Singleton")) {
                heuristics.setValue("MST");
            } else if (heuristics.getValue().equals("MST")) {
                heuristics.setValue("TSP");
            } else {
                heuristics.setValue("Zero");
            }
        }

        if (keyEvent.isControlDown() && keyEvent.getCode().getName().equals("G")) {
//            runMaze15X15(new Position(0, 0));
//            runMaze15X15(new Position(0, 12));
//            runMaze15X15(new Position(0, 10));
//            runMaze15X15(new Position(1, 0));
//            runMaze15X15(new Position(2, 4));
//            runMaze15X15(new Position(3, 0));
//            runMaze15X15(new Position(4, 4));
//            runMaze15X15(new Position(4, 6));
//            runMaze15X15(new Position(4, 8));
//            runMaze15X15(new Position(6, 10));
//            runMaze15X15(new Position(6, 14));
//            runMaze15X15(new Position(7, 12));
//            runMaze15X15(new Position(8, 2));
//            runMaze15X15(new Position(8, 6));
//            runMaze15X15(new Position(8, 8));
//            runMaze15X15(new Position(10, 4));
//            runMaze15X15(new Position(10, 8));
//            runMaze15X15(new Position(10, 10));
//            runMaze15X15(new Position(10, 12));
//            runMaze15X15(new Position(11, 0));
//            runMaze15X15(new Position(11, 8));
//            runMaze15X15(new Position(12, 10));
//            runMaze15X15(new Position(12, 12));
//            runMaze15X15(new Position(12, 14));
//            runMaze15X15(new Position(13, 0));
//            runMaze15X15(new Position(13, 12));
//            runMaze15X15(new Position(14, 2));
//            runMaze15X15(new Position(14, 6));
//            runMaze15X15(new Position(14, 14));
            runDen405d(new Position(0, 32));
            runDen405d(new Position(0, 50));
            runDen405d(new Position(1, 21));
            runDen405d(new Position(1, 37));
            runDen405d(new Position(1, 44));
            runDen405d(new Position(3, 9));
            runDen405d(new Position(3, 26));
            runDen405d(new Position(4, 31));
            runDen405d(new Position(6, 0));
            runDen405d(new Position(8, 10));
            runDen405d(new Position(8, 55));
            runDen405d(new Position(12, 41));
            runDen405d(new Position(14, 56));
            runDen405d(new Position(14, 68));
            runDen405d(new Position(15, 23));
            runDen405d(new Position(18, 25));
            runDen405d(new Position(20, 59));
            runDen405d(new Position(21, 61));
            runDen405d(new Position(24, 31));
            runDen405d(new Position(24, 49));
            runDen405d(new Position(25, 52));
            runDen405d(new Position(26, 31));
            runDen405d(new Position(30, 41));
            runDen405d(new Position(33, 51));
            runDen405d(new Position(34, 33));
            runDen405d(new Position(35, 51));
            runDen405d(new Position(37, 37));
            runDen405d(new Position(37, 47));
            runDen405d(new Position(39, 41));
            runLak103d(new Position(6, 0));
            runLak103d(new Position(12, 0));
            runLak103d(new Position(1, 6));
            runLak103d(new Position(12, 7));
            runLak103d(new Position(8, 8));
            runLak103d(new Position(16, 8));
            runLak103d(new Position(21, 11));
            runLak103d(new Position(10, 12));
            runLak103d(new Position(9, 16));
            runLak103d(new Position(3, 19));
            runLak103d(new Position(5, 19));
            runLak103d(new Position(19, 20));
            runLak103d(new Position(22, 20));
            runLak103d(new Position(6, 24));
            runLak103d(new Position(29, 24));
            runLak103d(new Position(6, 28));
            runLak103d(new Position(42, 30));
            runLak103d(new Position(25, 31));
            runLak103d(new Position(21, 32));
            runLak103d(new Position(2, 33));
            runLak103d(new Position(42, 33));
            runLak103d(new Position(15, 35));
            runLak103d(new Position(22, 36));
            runLak103d(new Position(42, 37));
            runLak103d(new Position(10, 41));
            runLak103d(new Position(33, 41));
            runLak103d(new Position(44, 41));
            runLak103d(new Position(8, 44));
            runLak103d(new Position(26, 45));
            runLak103d(new Position(8, 46));
        }
        keyEvent.consume();
    }

    private void runWithWeights() {
        double [] weights = new double[] {1,1.1,1.2,1.4,1.5,1.75,2,3,5,10};
        for (double weight: weights) {
            textField_weight.setText(String.format("%.2f", weight));
            algorithm_choiceBox.setValue("WA*");
            btn_solveMap.fire();
            algorithm_choiceBox.setValue("XUP");
            btn_solveMap.fire();
            algorithm_choiceBox.setValue("XDP");
            btn_solveMap.fire();
        }
    }

    private void runRegular(){

        btn_solveMap.fire();
    }

    private void runWithDistanceFactor() {
        double [] distFactors = new double[] {1,1.1,1.2,1.4,1.5,1.75,2,2.5,3,4,5,8,10};
        for (double df: distFactors) {
            textField_distanceFactor.setText(String.format("%.2f", df));
            runRegular();
        }
    }

    private void runMaze15X15(Position p) {
        btn_solveMap.requestFocus();
        movements.setValue("Jump");
        heuristics.setValue("TSP");
        heuristicGraph.setValue("Frontiers");
        los.setValue("Symmetric BresLos");
        textField_weight.setText("1");
        textField_rowSize.setText("15");
        textField_columnSize.setText("15");
        check_No_Whites.setSelected(false);
        check_Farthest.setSelected(false);
        check_Bounded.setSelected(false);
        File file = new File("resources/SavedMaps/maze_15X15.map");
        model.loadMap(new StringMapGenerator().generate(file), file.getName());
        model.agent = p;
        mapGrid.setMap(model.map, model.agent);
        runAllPermotations();
    }


    private void runDen405d(Position p) {
        btn_solveMap.requestFocus();
        movements.setValue("Jump");
        heuristics.setValue("TSP");
        heuristicGraph.setValue("Frontiers");
        los.setValue("Symmetric BresLos");
        textField_weight.setText("1");
        textField_rowSize.setText("15");
        textField_columnSize.setText("15");
        check_No_Whites.setSelected(false);
        check_Farthest.setSelected(false);
        check_Bounded.setSelected(false);
        File file = new File("resources/SavedMaps/den405d.map");
        model.loadMap(new StringMapGenerator().generate(file), file.getName());
        model.agent = p;
        mapGrid.setMap(model.map, model.agent);
        runAllPermotations();
    }

    private void runLak103d(Position p) {
        btn_solveMap.requestFocus();
        movements.setValue("Jump");
        heuristics.setValue("TSP");
        heuristicGraph.setValue("Frontiers");
        los.setValue("Symmetric BresLos");
        textField_weight.setText("1");
        textField_rowSize.setText("15");
        textField_columnSize.setText("15");
        check_No_Whites.setSelected(false);
        check_Farthest.setSelected(false);
        check_Bounded.setSelected(false);
        File file = new File("resources/SavedMaps/den405d.map");
        model.loadMap(new StringMapGenerator().generate(file), file.getName());
        model.agent = p;
        mapGrid.setMap(model.map, model.agent);
        runAllPermotations();
    }

    private void runAllPermotations() {
        //Optimal:
        runWithWeights();
        algorithm_choiceBox.setValue("WA*");
        textField_weight.setText("1");
        //No Whites:
        check_No_Whites.setSelected(true);
        runRegular();
        //Farthest:
        check_No_Whites.setSelected(false);
        check_Farthest.setSelected(true);
        runRegular();
        //Bounded:
        check_Farthest.setSelected(false);
        check_Bounded.setSelected(true);
        runWithDistanceFactor();
        //No Whites + Farthest:
        check_No_Whites.setSelected(true);
        check_Farthest.setSelected(true);
        check_Bounded.setSelected(false);
        runRegular();
        //No Whites + Bounded:
        check_Farthest.setSelected(false);
        check_Bounded.setSelected(true);
        runWithDistanceFactor();
        //Farthest + Bounded:
        check_No_Whites.setSelected(false);
        check_Farthest.setSelected(true);
        runWithDistanceFactor();
        //AIC:
        check_No_Whites.setSelected(true);
        runWithDistanceFactor();
    }
}
