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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

        //Carga el ComboBox
        countryBox.getItems().clear();
        loadCountries();


        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        //Cargamos los datos en la tabla

        tvAirport.getItems().clear();
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

        Airport selectedAirport= (Airport) tvAirport.getSelectionModel().getSelectedItem();

        if (selectedAirport != null) {

            if (validateFields()) {

            if (util.Utility.isNumber(codeField.getText())) {

                int code = Integer.parseInt(codeField.getText());
                String name = nameField.getText();
                String country = String.valueOf(countryBox.getValue());

                selectedAirport.setCode(code);
                selectedAirport.setName(name);
                selectedAirport.setCountry(country);
                selectedAirport.setStatus(Status.ACTIVE); //Siempre luego de modificarlo se activa

            }//end isNumber

        }else{util.FXUtility.showErrorAlert("Error", "Complete the fields");}

        }else {util.FXUtility.showErrorAlert("Error", "No airport has been selected");}

        //Vuelve a cargar la lista con el archivo ya modificado
        tvAirport.getItems().clear();
        tvAirport.setItems(convertToObservableList(airportsList));

    }

    @javafx.fxml.FXML
    public void createAirport(ActionEvent actionEvent) {

        if(validateFields()) {

            if (util.Utility.isNumber(codeField.getText())) {
                int code = Integer.parseInt(codeField.getText());
                String name = nameField.getText();
                String country = String.valueOf(countryBox.getValue());


                Airport newAirport = new Airport(code,name,country,Status.ACTIVE);

                addAirportToJson(newAirport);

            }//end isNumber

        }else{util.FXUtility.showErrorAlert("Error", "No airport has been selected");} //end validateFields

        codeField.clear();
        nameField.clear();
        countryBox.getSelectionModel().clearSelection();

        //Vuelve a crear la lista con el archivo ya modificado
        airportsList = new DoublyLinkedList();
        loadList();
        tvAirport.getItems().clear();
        tvAirport.setItems(convertToObservableList(airportsList));

    }//end createAirport

    @javafx.fxml.FXML
    public void deleteAirport(ActionEvent actionEvent) {

        Airport selectedAirport= (Airport) tvAirport.getSelectionModel().getSelectedItem();

        if (selectedAirport != null) {

            removeAirportFromJson(selectedAirport);

        }else {util.FXUtility.showErrorAlert("Error", "No airport has been selected");}

        //Vuelve a crear la lista con el archivo ya modificado
        airportsList = new DoublyLinkedList();
        loadList();
        tvAirport.getItems().clear();
        tvAirport.setItems(convertToObservableList(airportsList));

    }

    private void addAirportToJson(Airport newAirport) {
        try {
            Gson gson = new Gson();
            //Leer la lista actual de aeropuertos desde el archivo
            List<Airport> airports;
            try (FileReader reader = new FileReader("src/main/resources/ucr/project/airports.json")) {
                airports = gson.fromJson(reader, new TypeToken<List<Airport>>(){}.getType());
            }

            if (airports == null) {
                airports = new ArrayList<>();
            }

            //Agregar el nuevo aeropuerto a la lista
            airports.add(newAirport);

            //Guardar la lista actualizada en el archivo JSON
            try (FileWriter writer = new FileWriter("src/main/resources/ucr/project/airports.json")) {
                gson.toJson(airports, writer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeAirportFromJson(Airport airportToRemove) {

        try {
            Gson gson = new Gson();
            List<Airport> airports;

            // Leer la lista actual de aeropuertos desde el archivo
            try (FileReader reader = new FileReader("src/main/resources/ucr/project/airports.json")) {
                airports = gson.fromJson(reader, new TypeToken<List<Airport>>(){}.getType());
            }

            if (airports == null) {
                airports = new ArrayList<>();
            }

            // Buscar y eliminar el aeropuerto por código
            airports.removeIf(a -> a.getCode() == airportToRemove.getCode());

            // Guardar la lista actualizada en el archivo JSON
            try (FileWriter writer = new FileWriter("src/main/resources/ucr/project/airports.json")) {
                gson.toJson(airports, writer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void loadCountries() {
        List<String> countries = Arrays.asList(
                "Costa Rica",
                "Estados Unidos",
                "México",
                "Canadá",
                "España",
                "Francia",
                "Alemania",
                "Brasil",
                "Argentina",
                "Japón"
        );

        countryBox.getItems().clear();  // Limpia elementos previos
        countryBox.getItems().addAll(countries);  // Agrega la lista de países
    }



    public boolean validateFields() {
        String code = codeField.getText();
        String name = nameField.getText();
        String country = String.valueOf(countryBox.getValue());

        if (code == null || code.trim().isEmpty()) {
            util.FXUtility.showErrorAlert("Error", "Code is empty");
            return false;
        }
        if (name == null || name.trim().isEmpty()) {
            util.FXUtility.showErrorAlert("Error", "Name is empty");
            return false;
        }
        if (country == null || country.trim().isEmpty() || countryBox.getValue() == null) {
            util.FXUtility.showErrorAlert("Error", "No country selected");
            return false;
        }

        return true;
    }
}
