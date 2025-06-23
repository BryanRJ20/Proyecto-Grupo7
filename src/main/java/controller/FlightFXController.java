package controller;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import domain.Airport;
import domain.Flight;
import domain.Passenger;
import domain.list.CircularDoublyLinkedList;
import domain.tree.AVLTree;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.StringConverter;
import util.DataLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FlightFXController {
    @FXML
    private TableView<Flight> tvFlights;
    @FXML
    private TextField capacityField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TableColumn<Flight, Integer> numberColumn;
    @FXML
    private TableColumn<Flight, String> originColumn;
    @FXML
    private TableColumn<Flight, Integer> capacityColumn;
    @FXML
    private TableColumn<Flight, String> statusColumn;
    @FXML
    private ComboBox<String> destinationBox;
    @FXML
    private TableColumn<Flight, String> destinationColumn;
    @FXML
    private TableColumn<Flight, LocalDateTime> dateColumn;
    @FXML
    private ComboBox<String> originBox;
    @FXML
    private TableColumn<Flight, Integer> occupancyColumn;
    @FXML
    private Spinner<Integer> hours;
    @FXML
    private Spinner<Integer> minutes;
    @FXML
    private Spinner<Integer> seconds;

    private CircularDoublyLinkedList flightsList = new CircularDoublyLinkedList();
    private AVLTree passengersTree = new AVLTree();
    private int counterID = 1141;

    @FXML
    public void initialize() {
        try {
            loadFlightsFromJSON();
            loadTree();
            loadAirports();
            setupTableColumns();
            setupTimeControls();

            tvFlights.getItems().clear();
            tvFlights.setItems(convertToObservableList(flightsList));
        } catch (Exception e) {
            e.printStackTrace();
            showInfoMessage("Error", "Error inicializando vista de vuelos: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        originColumn.setCellValueFactory(new PropertyValueFactory<>("origin"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        occupancyColumn.setCellValueFactory(new PropertyValueFactory<>("occupancy"));

        statusColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Flight, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<Flight, String> cellData) {
                        Flight flight = cellData.getValue();
                        String status = "On Schedule";

                        if (flight.getOccupancy() >= flight.getCapacity()) {
                            status = "Full";
                        } else if (flight.getOccupancy() >= flight.getCapacity() * 0.9) {
                            status = "Almost Full";
                        }

                        return new SimpleStringProperty(status);
                    }
                }
        );

        dateColumn.setCellFactory(column -> {
            TableCell<Flight, LocalDateTime> cell = new TableCell<Flight, LocalDateTime>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatter.format(item));
                    }
                }
            };
            return cell;
        });
    }

    private void setupTimeControls() {
        datePicker.setValue(LocalDate.now());

        hours.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalDateTime.now().getHour()));
        minutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalDateTime.now().getMinute()));
        seconds.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalDateTime.now().getSecond()));

        hours.setEditable(true);
        minutes.setEditable(true);
        seconds.setEditable(true);

        datePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate date) {
                return (date != null) ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty())
                        ? LocalDate.parse(string, dateFormatter) : null;
            }
        });
    }

    private void loadTree() {
        try {
            passengersTree = DataLoader.loadPassengersFromJson("src/main/resources/ucr/project/passengers.json");
        } catch (Exception e) {
            e.printStackTrace();
            showInfoMessage("Error", "Error cargando pasajeros: " + e.getMessage());
        }
    }

    @FXML
    public void assignPassengersAction(ActionEvent actionEvent) {
        Flight selectedFlight = tvFlights.getSelectionModel().getSelectedItem();

        if (selectedFlight != null) {
            assignRandomPassengersToFlights(selectedFlight);
        } else {
            util.FXUtility.showErrorAlert("Error", "No flight has been selected");
        }
    }

    @FXML
    public void createFlight(ActionEvent actionEvent) {
        if (validateFields()) {
            if (util.Utility.isNumber(capacityField.getText())) {
                int capacity = Integer.parseInt(capacityField.getText());
                LocalDateTime dateTime = updateDateTime();
                String origin = String.valueOf(originBox.getValue());
                String destination = String.valueOf(destinationBox.getValue());

                if (util.Utility.compare(origin, destination) == 0) {
                    showInfoMessage("Error", "Origin and destination are the same");
                } else {
                    Flight newFlight = new Flight(counterID++, origin, destination, dateTime, capacity);
                    addFlightToJson(newFlight);
                    refreshFlightList();
                    clearFields();
                }
            }
        } else {
            util.FXUtility.showErrorAlert("Error", "Please fill all the fields");
        }
    }

    private void refreshFlightList() {
        flightsList = new CircularDoublyLinkedList();
        loadFlightsFromJSON();
        tvFlights.getItems().clear();
        tvFlights.setItems(convertToObservableList(flightsList));
    }

    private void clearFields() {
        capacityField.clear();
        originBox.getSelectionModel().clearSelection();
        destinationBox.getSelectionModel().clearSelection();
        datePicker.setValue(LocalDate.now());
        hours.getValueFactory().setValue(0);
        minutes.getValueFactory().setValue(0);
        seconds.getValueFactory().setValue(0);
    }

    public void assignRandomPassengersToFlights(Flight flight) {
        try {
            assignPassengersToFlight(flight);
            showInfoMessage("Success", "✅ Passengers assigned to flight successfully");
        } catch (Exception e) {
            showInfoMessage("Error", "❌ Error assigning passengers to flight: " + e.getMessage());
        }
    }

    private void assignPassengersToFlight(Flight flight) {
        int currentOccupancy = flight.getOccupancy();

        for (int i = 0; i < currentOccupancy; i++) {
            int passengerId = 10000 + util.Utility.random(0, 9999);
            Passenger passenger = passengersTree.search(passengerId);

            if (passenger != null) {
                passenger.addFlightToHistory(flight);
            }
        }
    }

    public void loadFlightsFromJSON() {
        try (FileReader reader = new FileReader("src/main/resources/ucr/project/flights.json")) {
            GsonBuilder gsonBuilder = new GsonBuilder();

            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                        throws JsonParseException {
                    return LocalDateTime.parse(json.getAsString());
                }
            });

            Gson gson = gsonBuilder.create();
            List<Flight> flights = gson.fromJson(reader, new TypeToken<List<Flight>>() {}.getType());

            if (flightsList == null) {
                flightsList = new CircularDoublyLinkedList();
            } else {
                flightsList.clear();
            }

            if (flights != null) {
                for (Flight flight : flights) {
                    flightsList.add(flight);
                }
            }

            System.out.println("Vuelos cargados: " + flightsList.size());

        } catch (Exception e) {
            System.err.println("Error al cargar vuelos: " + e.getMessage());
            // Crear vuelos por defecto si no se pueden cargar
            flightsList = DataLoader.loadFlightsFromJson("src/main/resources/ucr/project/flights.json");
        }
    }

    public ObservableList<Flight> convertToObservableList(CircularDoublyLinkedList list) {
        ObservableList<Flight> flights = FXCollections.observableArrayList();

        if (list.isEmpty()) {
            return flights;
        }

        try {
            domain.list.Node aux = list.first;
            do {
                Flight current = (Flight) aux.data;
                flights.add(current);
                aux = aux.next;
            } while (aux != list.first);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flights;
    }

    private void addFlightToJson(Flight newFlight) {
        try {
            List<Flight> flights;
            try (FileReader reader = new FileReader("src/main/resources/ucr/project/flights.json")) {
                Gson gson = new Gson();
                flights = gson.fromJson(reader, new TypeToken<List<Flight>>(){}.getType());
            } catch (Exception e) {
                flights = new ArrayList<>();
            }

            if (flights == null) {
                flights = new ArrayList<>();
            }

            flights.add(newFlight);

            try (FileWriter writer = new FileWriter("src/main/resources/ucr/project/flights.json")) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(flights, writer);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showInfoMessage("Error", "Error saving flight: " + e.getMessage());
        }
    }

    private void loadAirports() {
        List<String> airportNames = Arrays.asList(
                "Aeropuerto Internacional Juan Santamaría",
                "John F. Kennedy International Airport",
                "London Heathrow Airport",
                "Charles de Gaulle Airport",
                "Tokyo Haneda Airport",
                "São Paulo–Guarulhos International Airport",
                "Sydney Kingsford Smith Airport",
                "Frankfurt am Main Airport",
                "Dubai International Airport",
                "Toronto Pearson International Airport",
                "Changi Airport",
                "Incheon International Airport",
                "Amsterdam Schiphol Airport",
                "Madrid-Barajas Adolfo Suárez Airport",
                "Beijing Capital International Airport",
                "O. R. Tambo International Airport",
                "Los Angeles International Airport",
                "Munich Airport",
                "Zurich Airport",
                "Benito Juárez International Airport"
        );

        Collections.sort(airportNames);

        originBox.getItems().clear();
        originBox.getItems().addAll(airportNames);

        destinationBox.getItems().clear();
        destinationBox.getItems().addAll(airportNames);
    }

    public boolean validateFields() {
        String origin = String.valueOf(originBox.getValue());
        String destination = String.valueOf(destinationBox.getValue());
        String capacity = capacityField.getText();

        if (capacity == null || capacity.trim().isEmpty()) {
            util.FXUtility.showErrorAlert("Error", "Capacity is empty");
            return false;
        }
        if (datePicker.getValue() == null) {
            util.FXUtility.showErrorAlert("Error", "Date is empty");
            return false;
        }
        if (origin == null || origin.trim().isEmpty() || originBox.getValue() == null) {
            util.FXUtility.showErrorAlert("Error", "No origin selected");
            return false;
        }
        if (destination == null || destination.trim().isEmpty() || destinationBox.getValue() == null) {
            util.FXUtility.showErrorAlert("Error", "No destination selected");
            return false;
        }

        return true;
    }

    private LocalDateTime updateDateTime() {
        LocalDate date = datePicker.getValue();
        if (date == null) {
            date = LocalDate.now();
        }

        int hour = hours.getValue() != null ? hours.getValue() : 0;
        int minute = minutes.getValue() != null ? minutes.getValue() : 0;
        int second = seconds.getValue() != null ? seconds.getValue() : 0;

        return LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(),
                hour, minute, second);
    }

    private void showInfoMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}