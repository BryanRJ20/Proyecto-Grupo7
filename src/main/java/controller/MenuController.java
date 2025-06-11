package controller;

import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class MenuController {


    @javafx.fxml.FXML
    private BorderPane bp;
    @javafx.fxml.FXML
    private AnchorPane ap;

    @javafx.fxml.FXML
    public void exit(ActionEvent actionEvent) {
        System.exit(0);
    }

    @javafx.fxml.FXML
    public void passengers(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void simulation(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void flights(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void Home(ActionEvent actionEvent) {
        this.bp.setCenter(ap);
    }

    @javafx.fxml.FXML
    public void statistics(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void airports(ActionEvent actionEvent) {
    }
}
