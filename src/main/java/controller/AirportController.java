package controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import domain.Airport;
import domain.Status;
import domain.list.DoublyLinkedList;
import domain.list.ListException;
import domain.list.Node;
import domain.security.AuthenticationService;
import domain.security.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

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

    @FXML
    private TableColumn codeColumn;
    @FXML
    private TableColumn nameColumn;
    @FXML
    private TableColumn statusColumn;
    @FXML
    private TableColumn countryColumn;

    private DoublyLinkedList airportsList = new DoublyLinkedList();


    @FXML
    public void initialize() throws ListException {

        //Cargamos los aeropuertos desde json
        loadList();

        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        //Cargamos los datos en la tabla
        tvAirport.setItems(convertToObservableList(airportsList));


    }


    public ObservableList<Airport> convertToObservableList(DoublyLinkedList list) {
        ObservableList<Airport> airports = FXCollections.observableArrayList();
        Node aux = airportsList.first;

        while (aux != null) { // Recorre la lista
            Object data = aux.data;
            Airport current = (Airport) data;
            airports.add(current); // Agrega el aeropuerto a la ObservableList
            aux = aux.next; // Avanzamos al siguiente nodo
        } // End while

        return airports;
    }

    private void loadList(){

        //Carga la lista desde JSON a la airportsList
            try (FileReader reader = new FileReader("src/main/resources/ucr/project/airports.json")) {
                Gson gson = new Gson();
                List<Airport> airports = gson.fromJson(reader, new TypeToken<List<Airport>>(){}.getType());

                for (Airport a : airports)
                    airportsList.add(a);

            } catch (Exception e) {e.printStackTrace();}

    }

    @javafx.fxml.FXML
    public void activateDeactivate(ActionEvent actionEvent) {

        Airport selectedAirport= (Airport) tvAirport.getSelectionModel().getSelectedItem();

        if (selectedAirport != null) {

            if (selectedAirport.status == Status.ACTIVE) {
                selectedAirport.deactivate(); //Lo desactiva



            } else if (selectedAirport.status == Status.INACTIVE) {
                selectedAirport.activate(); //Lo activa
            }

        } else {util.FXUtility.showErrorAlert("Error", "No airport has been selected");}

        tvAirport.getItems().clear();
        tvAirport.setItems(convertToObservableList(airportsList));

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
