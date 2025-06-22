package controller;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class FlightFXController {
    @javafx.fxml.FXML
    private TableView tvFlights;
    @javafx.fxml.FXML
    private TextField capacityField;
    @javafx.fxml.FXML
    private DatePicker datePicker;
    @javafx.fxml.FXML
    private TableColumn numberColumn;
    @javafx.fxml.FXML
    private TableColumn originColumn;
    @javafx.fxml.FXML
    private TableColumn capacityColumn;
    @javafx.fxml.FXML
    private Text txtMessage;
    @javafx.fxml.FXML
    private Pane mainPain;
    @javafx.fxml.FXML
    private TableColumn statusColumn;
    @javafx.fxml.FXML
    private ComboBox destinationBox;
    @javafx.fxml.FXML
    private TableColumn destinationColumn;
    @javafx.fxml.FXML
    private TableColumn dateColumn;
    @javafx.fxml.FXML
    private ComboBox originBox;

    @javafx.fxml.FXML
    public void assignPassengersAction(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void createFlight(ActionEvent actionEvent) {
    }
}
