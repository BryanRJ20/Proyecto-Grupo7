package controller;

import domain.Flight;
import domain.Passenger;
import domain.security.AuthenticationService;
import domain.security.User;
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

public class UserReservationsController {

    @FXML
    private TableView<Flight> availableFlightsTable;
    @FXML
    private TableView<Flight> myReservationsTable;
    @FXML
    private ComboBox<String> originComboBox;
    @FXML
    private ComboBox<String> destinationComboBox;
    @FXML
    private DatePicker departureDatePicker;
    @FXML
    private Button searchFlightsBtn;
    @FXML
    private Button makeReservationBtn;
    @FXML
    private Button cancelReservationBtn;
    @FXML
    private Label statusLabel;

    // Columnas para vuelos disponibles
    @FXML
    private TableColumn<Flight, Integer> availableFlightNumberColumn;
    @FXML
    private TableColumn<Flight, String> availableOriginColumn;
    @FXML
    private TableColumn<Flight, String> availableDestinationColumn;
    @FXML
    private TableColumn<Flight, LocalDateTime> availableDepartureColumn;
    @FXML
    private TableColumn<Flight, Integer> availableSeatsColumn;
    @FXML
    private TableColumn<Flight, String> availablePriceColumn;

    // Columnas para mis reservaciones
    @FXML
    private TableColumn<Flight, Integer> myFlightNumberColumn;
    @FXML
    private TableColumn<Flight, String> myOriginColumn;
    @FXML
    private TableColumn<Flight, String> myDestinationColumn;
    @FXML
    private TableColumn<Flight, LocalDateTime> myDepartureColumn;
    @FXML
    private TableColumn<Flight, String> myStatusColumn;

    private AuthenticationService authService;
    private ObservableList<Flight> availableFlights;
    private ObservableList<Flight> myReservations;

    @FXML
    public void initialize() {
        authService = AuthenticationService.getInstance();
        setupTables();
        loadAirports();
        loadInitialData();

        statusLabel.setText("Bienvenido al sistema de reservaciones");
    }

    private void setupTables() {
        // Configurar tabla de vuelos disponibles
        availableFlightNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        availableOriginColumn.setCellValueFactory(new PropertyValueFactory<>("origin"));
        availableDestinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        availableDepartureColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        availableSeatsColumn.setCellValueFactory(cellData -> {
            Flight flight = cellData.getValue();
            return new javafx.beans.property.SimpleIntegerProperty(flight.getAvailableSeats()).asObject();
        });
        availablePriceColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty("$" + (200 + cellData.getValue().getNumber() % 500))
        );

        // Configurar tabla de mis reservaciones
        myFlightNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        myOriginColumn.setCellValueFactory(new PropertyValueFactory<>("origin"));
        myDestinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        myDepartureColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        myStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Formatear columnas de fecha
        availableDepartureColumn.setCellFactory(column -> {
            TableCell<Flight, LocalDateTime> cell = new TableCell<Flight, LocalDateTime>() {
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
            return cell;
        });

        myDepartureColumn.setCellFactory(column -> {
            TableCell<Flight, LocalDateTime> cell = new TableCell<Flight, LocalDateTime>() {
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
            return cell;
        });

        availableFlights = FXCollections.observableArrayList();
        myReservations = FXCollections.observableArrayList();

        availableFlightsTable.setItems(availableFlights);
        myReservationsTable.setItems(myReservations);
    }

    private void loadAirports() {
        List<String> airports = Arrays.asList(
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

        Collections.sort(airports);

        originComboBox.getItems().clear();
        originComboBox.getItems().addAll(airports);

        destinationComboBox.getItems().clear();
        destinationComboBox.getItems().addAll(airports);
    }

    private void loadInitialData() {
        try {
            var flightsList = DataLoader.loadFlightsFromJson("src/main/resources/ucr/project/flights.json");

            // Convertir a lista observable
            availableFlights.clear();
            if (!flightsList.isEmpty()) {
                for (int i = 1; i <= flightsList.size(); i++) {
                    Flight flight = (Flight) flightsList.getNode(i).getData();
                    if (flight.hasAvailableSeats()) {
                        availableFlights.add(flight);
                    }
                }
            }

            // Cargar mis reservaciones (simuladas)
            loadMyReservations();

        } catch (Exception e) {
            showError("Error cargando datos: " + e.getMessage());
        }
    }

    private void loadMyReservations() {
        // Por simplicidad, vamos a simular algunas reservaciones para el usuario actual
        myReservations.clear();

        if (availableFlights.size() > 0) {
            // Agregar algunas reservaciones ficticias
            Flight reservation1 = new Flight(9001, "Juan Santamaría", "JFK",
                    LocalDateTime.now().plusDays(5), 180);
            reservation1.setStatus("CONFIRMED");

            Flight reservation2 = new Flight(9002, "Heathrow", "Charles de Gaulle",
                    LocalDateTime.now().plusDays(15), 160);
            reservation2.setStatus("CONFIRMED");

            myReservations.addAll(Arrays.asList(reservation1, reservation2));
        }
    }

    @FXML
    public void searchFlights(ActionEvent actionEvent) {
        String origin = originComboBox.getValue();
        String destination = destinationComboBox.getValue();

        if (origin == null || destination == null) {
            showError("Por favor selecciona origen y destino");
            return;
        }

        if (origin.equals(destination)) {
            showError("El origen y destino no pueden ser iguales");
            return;
        }

        // Filtrar vuelos
        availableFlights.clear();
        try {
            var allFlights = DataLoader.loadFlightsFromJson("src/main/resources/ucr/project/flights.json");

            for (int i = 1; i <= allFlights.size(); i++) {
                Flight flight = (Flight) allFlights.getNode(i).getData();

                if (flight.getOrigin().contains(origin.split(" ")[0]) &&
                        flight.getDestination().contains(destination.split(" ")[0]) &&
                        flight.hasAvailableSeats() &&
                        flight.getDepartureTime().isAfter(LocalDateTime.now())) {
                    availableFlights.add(flight);
                }
            }

            statusLabel.setText("Se encontraron " + availableFlights.size() + " vuelos disponibles");

        } catch (Exception e) {
            showError("Error buscando vuelos: " + e.getMessage());
        }
    }

    @FXML
    public void makeReservation(ActionEvent actionEvent) {
        Flight selectedFlight = availableFlightsTable.getSelectionModel().getSelectedItem();

        if (selectedFlight == null) {
            showError("Por favor selecciona un vuelo");
            return;
        }

        if (!selectedFlight.hasAvailableSeats()) {
            showError("Este vuelo no tiene asientos disponibles");
            return;
        }

        // Simular reservación
        selectedFlight.addPassenger(); // Reducir asientos disponibles

        // Crear copia para mis reservaciones
        Flight reservation = new Flight(selectedFlight.getNumber(),
                selectedFlight.getOrigin(),
                selectedFlight.getDestination(),
                selectedFlight.getDepartureTime(),
                selectedFlight.getCapacity());
        reservation.setStatus("CONFIRMED");

        myReservations.add(reservation);

        // Actualizar tabla de vuelos disponibles
        availableFlightsTable.refresh();

        showInfo("¡Reservación confirmada! Tu vuelo " + selectedFlight.getNumber() + " ha sido reservado.");
        statusLabel.setText("Reservación realizada exitosamente");
    }

    @FXML
    public void cancelReservation(ActionEvent actionEvent) {
        Flight selectedReservation = myReservationsTable.getSelectionModel().getSelectedItem();

        if (selectedReservation == null) {
            showError("Por favor selecciona una reservación para cancelar");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar Cancelación");
        confirmation.setHeaderText("¿Estás seguro de que quieres cancelar esta reservación?");
        confirmation.setContentText("Vuelo " + selectedReservation.getNumber() + " - " +
                selectedReservation.getOrigin() + " → " + selectedReservation.getDestination());

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            myReservations.remove(selectedReservation);
            showInfo("Reservación cancelada exitosamente");
            statusLabel.setText("Reservación cancelada");
        }
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