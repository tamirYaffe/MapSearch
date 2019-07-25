import View.MapGrid;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class Controller {
    Model model;

    @FXML

    public TextField textField_rowSize;
    public TextField textField_columnSize;
    public Button btn_generateMap;
    public Button btn_solveMap;
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
    public void generateMap(){
        String rowSize=textField_rowSize.getText();
        String columnSize=textField_columnSize.getText();

        if(isInteger(rowSize) && isInteger(columnSize) && Integer.valueOf(rowSize)>4 && Integer.valueOf(columnSize)>4){
            int rows = Integer.valueOf(rowSize);
            int columns = Integer.valueOf(columnSize);
            model.generateMap(rows, columns);
            mapGrid.setMap(model.map);
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("input alert");
            alert.setHeaderText("Generate Maze");
            alert.setContentText("insert only numbers greater then 4");
            alert.show();
        }

    }
    public void solveMap() {
        model.solveMap();
        mapGrid.setMap(model.map);
        solution.setText(model.consoleString);
    }

    private boolean isInteger( String input ) {
        try {
            Integer.parseInt( input );
            return true;
        }
        catch( NumberFormatException e ) {
            return false;
        }
    }

    public void getFocus(MouseEvent mouseEvent) {
        mapGrid.requestFocus();
    }

}
