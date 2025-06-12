package controller;

import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class AirportController {
    @javafx.fxml.FXML
    private TableView tvAirport;
    @javafx.fxml.FXML
    private TextField codeField;
    @javafx.fxml.FXML
    private Text txtMessage;
    @javafx.fxml.FXML
    private Pane mainPain;
    @javafx.fxml.FXML
    private TextField nameField;
    @javafx.fxml.FXML
    private ComboBox countryBox;

    @javafx.fxml.FXML
    public void activateDeactivate(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void modifyAirport(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void createAirport(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void deleteAirport(ActionEvent actionEvent) {
    }
}
