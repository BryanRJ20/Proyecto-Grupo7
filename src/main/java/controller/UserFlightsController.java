package controller;

import domain.Flight;
import domain.list.CircularDoublyLinkedList;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import util.DataLoader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UserFlightsController {

    @FXML
    private TableView<Flight> availableFlightsTable;
    @FXML
    private ComboBox<String> originComboBox;
    @FXML
    private ComboBox<String> destinationComboBox;
    @FXML
    private DatePicker departureDatePicker;
    @FXML
    private Button searchFlightsBtn;
    @FXML
    private Button refreshBtn;
    @FXML
    private Label statusLabel;
    @FXML
    private TextField capacityFilterField;
    @FXML
    private ComboBox<String> statusFilterBox;

    // Columnas de la tabla
    @FXML
    private TableColumn<Flight, Integer> flightNumberColumn;
    @FXML
    private TableColumn<Flight, String> originColumn;
    @FXML
    private TableColumn<Flight, String> destinationColumn;
    @FXML
    private TableColumn<Flight, LocalDateTime> departureColumn;
    @FXML
    private TableColumn<Flight, Integer> capacityColumn;
    @FXML
    private TableColumn<Flight, Integer> occupancyColumn;
    @FXML
    private TableColumn<Flight, String> availabilityColumn;
    @FXML
    private TableColumn<Flight, String> statusColumn;

    private ObservableList<Flight> allFlights;

    @FXML
    public void initialize() {
        setupTable();
        loadAirports();
        loadFlights();
        setupFilters();

        statusLabel.setText("Consulta de vuelos - Seleccione filtros y busque");
    }

    private void setupTable() {
        flightNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        originColumn.setCellValueFactory(new PropertyValueFactory<>("origin"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        departureColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        occupancyColumn.setCellValueFactory(new PropertyValueFactory<>("occupancy"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Columna de disponibilidad personalizada
        availabilityColumn.setCellValueFactory(cellData -> {
            Flight flight = cellData.getValue();
            int available = flight.getAvailableSeats();
            String availability;

            if (available == 0) {
                availability = "Completo";
            } else if (available <= 10) {
                availability = "Pocas plazas (" + available + ")";
            } else {
                availability = available + " disponibles";
            }

            return new SimpleStringProperty(availability);
        });

        // Formatear columna de fecha
        departureColumn.setCellFactory(column -> {
            return new TableCell<Flight, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                    }
                }
            };
        });

        allFlights = FXCollections.observableArrayList();
        availableFlightsTable.setItems(allFlights);
    }

    private void setupFilters() {
        statusFilterBox.getItems().addAll(
                "Todos",
                "SCHEDULED",
                "IN_FLIGHT",
                "COMPLETED",
                "CANCELLED"
        );
        statusFilterBox.setValue("Todos");

        capacityFilterField.setPromptText("Capacidad mínima");
    }

    private void loadAirports() {
        List<String> airports = Arrays.asList(
                "Todos los aeropuertos",
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
                "Madrid-Barajas Airport",
                "Rome Fiumicino Airport",
                "Amsterdam Airport Schiphol",
                "Singapore Changi Airport",
                "Los Angeles International Airport"
        );

        originComboBox.getItems().clear();
        originComboBox.getItems().addAll(airports);
        originComboBox.setValue("Todos los aeropuertos");

        destinationComboBox.getItems().clear();
        destinationComboBox.getItems().addAll(airports);
        destinationComboBox.setValue("Todos los aeropuertos");
    }

    private void loadFlights() {
        try {
            CircularDoublyLinkedList flightsList = DataLoader.loadFlightsFromJson("src/main/resources/ucr/project/flights.json");

            allFlights.clear();
            if (!flightsList.isEmpty()) {
                domain.list.Node current = flightsList.first;
                do {
                    Flight flight = (Flight) current.data;
                    // Solo mostrar vuelos futuros o actuales
                    if (flight.getDepartureTime().isAfter(LocalDateTime.now().minusHours(2))) {
                        allFlights.add(flight);
                    }
                    current = current.next;
                } while (current != flightsList.first);
            }

            statusLabel.setText("Cargados " + allFlights.size() + " vuelos disponibles");

        } catch (Exception e) {
            showError("Error cargando vuelos: " + e.getMessage());
            statusLabel.setText("Error cargando vuelos");
        }
    }

    @FXML
    public void searchFlights(ActionEvent actionEvent) {
        String origin = originComboBox.getValue();
        String destination = destinationComboBox.getValue();
        String statusFilter = statusFilterBox.getValue();

        ObservableList<Flight> filteredFlights = FXCollections.observableArrayList();

        for (Flight flight : allFlights) {
            boolean matches = true;

            // Filtro por origen
            if (origin != null && !origin.equals("Todos los aeropuertos")) {
                if (!flight.getOrigin().toLowerCase().contains(origin.toLowerCase().split(" ")[0])) {
                    matches = false;
                }
            }

            // Filtro por destino
            if (destination != null && !destination.equals("Todos los aeropuertos")) {
                if (!flight.getDestination().toLowerCase().contains(destination.toLowerCase().split(" ")[0])) {
                    matches = false;
                }
            }

            // Filtro por fecha
            if (departureDatePicker.getValue() != null) {
                if (!flight.getDepartureTime().toLocalDate().equals(departureDatePicker.getValue())) {
                    matches = false;
                }
            }

            // Filtro por estado
            if (statusFilter != null && !statusFilter.equals("Todos")) {
                if (!flight.getStatus().equals(statusFilter)) {
                    matches = false;
                }
            }

            // Filtro por capacidad
            if (!capacityFilterField.getText().isEmpty()) {
                try {
                    int minCapacity = Integer.parseInt(capacityFilterField.getText());
                    if (flight.getCapacity() < minCapacity) {
                        matches = false;
                    }
                } catch (NumberFormatException e) {
                    // Ignorar filtro si no es un número válido
                }
            }

            if (matches) {
                filteredFlights.add(flight);
            }
        }

        availableFlightsTable.setItems(filteredFlights);
        statusLabel.setText("Encontrados " + filteredFlights.size() + " vuelos que coinciden con los filtros");
    }

    @FXML
    public void refreshFlights(ActionEvent actionEvent) {
        loadFlights();
        availableFlightsTable.setItems(allFlights);
        statusLabel.setText("Lista de vuelos actualizada");
    }

    @FXML
    public void clearFilters(ActionEvent actionEvent) {
        originComboBox.setValue("Todos los aeropuertos");
        destinationComboBox.setValue("Todos los aeropuertos");
        departureDatePicker.setValue(null);
        statusFilterBox.setValue("Todos");
        capacityFilterField.clear();

        availableFlightsTable.setItems(allFlights);
        statusLabel.setText("Filtros limpiados - Mostrando todos los vuelos");
    }

    @FXML
    public void viewFlightDetails(ActionEvent actionEvent) {
        Flight selectedFlight = availableFlightsTable.getSelectionModel().getSelectedItem();

        if (selectedFlight == null) {
            showError("Por favor seleccione un vuelo para ver detalles");
            return;
        }

        showFlightDetails(selectedFlight);
    }

    private void showFlightDetails(Flight flight) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles del Vuelo");
        alert.setHeaderText("Vuelo " + flight.getNumber());

        StringBuilder details = new StringBuilder();
        details.append("Origen: ").append(flight.getOrigin()).append("\n");
        details.append("Destino: ").append(flight.getDestination()).append("\n");
        details.append("Salida: ").append(flight.getFormattedDepartureTime()).append("\n");
        details.append("Capacidad: ").append(flight.getCapacity()).append(" pasajeros\n");
        details.append("Ocupación: ").append(flight.getOccupancy()).append(" pasajeros\n");
        details.append("Asientos disponibles: ").append(flight.getAvailableSeats()).append("\n");
        details.append("Porcentaje ocupación: ").append(String.format("%.1f%%", flight.getOccupancyPercentage())).append("\n");
        details.append("Estado: ").append(flight.getStatus());

        alert.setContentText(details.toString());
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}