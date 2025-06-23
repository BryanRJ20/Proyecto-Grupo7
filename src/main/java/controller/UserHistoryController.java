package controller;

import domain.security.AuthenticationService;
import domain.security.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class UserHistoryController {

    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private ComboBox<String> statusFilterBox;
    @FXML
    private TableView<FlightHistoryRecord> historyTable;
    @FXML
    private TableColumn<FlightHistoryRecord, Integer> flightNumberColumn;
    @FXML
    private TableColumn<FlightHistoryRecord, String> originColumn;
    @FXML
    private TableColumn<FlightHistoryRecord, String> destinationColumn;
    @FXML
    private TableColumn<FlightHistoryRecord, LocalDateTime> departureDateColumn;
    @FXML
    private TableColumn<FlightHistoryRecord, String> statusColumn;
    @FXML
    private TableColumn<FlightHistoryRecord, LocalDateTime> reservationDateColumn;
    @FXML
    private TableColumn<FlightHistoryRecord, String> priceColumn;
    @FXML
    private TableColumn<FlightHistoryRecord, String> seatColumn;
    @FXML
    private Label statusLabel;
    @FXML
    private Label totalFlightsLabel;
    @FXML
    private Label totalDestinationsLabel;
    @FXML
    private Label totalMilesLabel;
    @FXML
    private Label favoriteDestinationLabel;
    @FXML
    private Label totalSpentLabel;
    @FXML
    private Label averagePriceLabel;
    @FXML
    private Label thisYearFlightsLabel;
    @FXML
    private Label frequentFlyerStatusLabel;

    private AuthenticationService authService;
    private ObservableList<FlightHistoryRecord> allRecords;
    private Random random = new Random();

    @FXML
    public void initialize() {
        authService = AuthenticationService.getInstance();
        setupTableColumns();
        setupFilters();
        loadFlightHistory();
        updateStatistics();

        statusLabel.setText("Flight history loaded successfully");
    }

    private void setupTableColumns() {
        flightNumberColumn.setCellValueFactory(new PropertyValueFactory<>("flightNumber"));
        originColumn.setCellValueFactory(new PropertyValueFactory<>("origin"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        departureDateColumn.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        reservationDateColumn.setCellValueFactory(new PropertyValueFactory<>("reservationDate"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        seatColumn.setCellValueFactory(new PropertyValueFactory<>("seat"));

        // Format date columns
        departureDateColumn.setCellFactory(column -> {
            return new TableCell<FlightHistoryRecord, LocalDateTime>() {
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

        reservationDateColumn.setCellFactory(column -> {
            return new TableCell<FlightHistoryRecord, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    }
                }
            };
        });
    }

    private void setupFilters() {
        statusFilterBox.getItems().addAll(
                "All Status",
                "COMPLETED",
                "CONFIRMED",
                "CANCELLED",
                "IN_FLIGHT",
                "SCHEDULED"
        );
        statusFilterBox.setValue("All Status");

        // Set default date range (last 12 months)
        toDatePicker.setValue(LocalDate.now());
        fromDatePicker.setValue(LocalDate.now().minusMonths(12));
    }

    private void loadFlightHistory() {
        // Generate sample flight history for the current user
        allRecords = FXCollections.observableArrayList();

        if (authService.isAuthenticated()) {
            User currentUser = authService.getCurrentUser();
            generateSampleHistory(currentUser.getUsername());
        }

        historyTable.setItems(allRecords);
    }

    private void generateSampleHistory(String username) {
        String[] origins = {"San José (SJO)", "New York (JFK)", "London (LHR)", "Paris (CDG)", "Madrid (MAD)"};
        String[] destinations = {"Miami (MIA)", "Los Angeles (LAX)", "Tokyo (NRT)", "Frankfurt (FRA)", "Rome (FCO)"};
        String[] statuses = {"COMPLETED", "CONFIRMED", "CANCELLED"};

        // Generate 15-25 sample flights
        int numFlights = 15 + random.nextInt(11);

        for (int i = 0; i < numFlights; i++) {
            FlightHistoryRecord record = new FlightHistoryRecord();
            record.setFlightNumber(1000 + random.nextInt(9000));
            record.setOrigin(origins[random.nextInt(origins.length)]);
            record.setDestination(destinations[random.nextInt(destinations.length)]);

            // Random departure date in the last 12 months
            LocalDateTime departureDate = LocalDateTime.now()
                    .minusDays(random.nextInt(365))
                    .withHour(random.nextInt(24))
                    .withMinute(random.nextInt(60));
            record.setDepartureDate(departureDate);

            // Reservation date should be before departure
            LocalDateTime reservationDate = departureDate.minusDays(random.nextInt(60) + 1);
            record.setReservationDate(reservationDate);

            record.setStatus(statuses[random.nextInt(statuses.length)]);
            record.setPrice("$" + (200 + random.nextInt(800)));
            record.setSeat((1 + random.nextInt(30)) + String.valueOf((char)('A' + random.nextInt(6))));

            allRecords.add(record);
        }

        // Sort by departure date (newest first)
        allRecords.sort((a, b) -> b.getDepartureDate().compareTo(a.getDepartureDate()));
    }

    @FXML
    public void applyFilters(ActionEvent actionEvent) {
        ObservableList<FlightHistoryRecord> filteredRecords = FXCollections.observableArrayList();

        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        String statusFilter = statusFilterBox.getValue();

        for (FlightHistoryRecord record : allRecords) {
            boolean matches = true;

            // Date filter
            if (fromDate != null && record.getDepartureDate().toLocalDate().isBefore(fromDate)) {
                matches = false;
            }
            if (toDate != null && record.getDepartureDate().toLocalDate().isAfter(toDate)) {
                matches = false;
            }

            // Status filter
            if (statusFilter != null && !statusFilter.equals("All Status")) {
                if (!record.getStatus().equals(statusFilter)) {
                    matches = false;
                }
            }

            if (matches) {
                filteredRecords.add(record);
            }
        }

        historyTable.setItems(filteredRecords);
        statusLabel.setText("Showing " + filteredRecords.size() + " flights matching filters");
    }

    @FXML
    public void clearFilters(ActionEvent actionEvent) {
        fromDatePicker.setValue(LocalDate.now().minusMonths(12));
        toDatePicker.setValue(LocalDate.now());
        statusFilterBox.setValue("All Status");
        historyTable.setItems(allRecords);
        statusLabel.setText("All filters cleared");
    }

    @FXML
    public void exportHistory(ActionEvent actionEvent) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Flight History");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            fileChooser.setInitialFileName("flight_history_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv");

            Stage stage = (Stage) historyTable.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                exportToCsv(file);
                showInfo("Export Successful", "Flight history exported to: " + file.getName());
            }
        } catch (Exception e) {
            showError("Export Error", "Error exporting flight history: " + e.getMessage());
        }
    }

    private void exportToCsv(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            // Header
            writer.write("Flight Number,Origin,Destination,Departure Date,Status,Reservation Date,Price,Seat\n");

            // Data
            for (FlightHistoryRecord record : historyTable.getItems()) {
                writer.write(String.format("%d,%s,%s,%s,%s,%s,%s,%s\n",
                        record.getFlightNumber(),
                        record.getOrigin(),
                        record.getDestination(),
                        record.getDepartureDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        record.getStatus(),
                        record.getReservationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        record.getPrice(),
                        record.getSeat()
                ));
            }
        }
    }

    private void updateStatistics() {
        if (allRecords.isEmpty()) {
            return;
        }

        // Total flights
        totalFlightsLabel.setText(String.valueOf(allRecords.size()));

        // Unique destinations
        long uniqueDestinations = allRecords.stream()
                .map(FlightHistoryRecord::getDestination)
                .distinct()
                .count();
        totalDestinationsLabel.setText(String.valueOf(uniqueDestinations));

        // Estimate total miles (random calculation for demo)
        int totalMiles = allRecords.size() * (500 + random.nextInt(2000));
        totalMilesLabel.setText(String.format("%,d", totalMiles));

        // Favorite destination
        String favoriteDestination = allRecords.stream()
                .collect(Collectors.groupingBy(FlightHistoryRecord::getDestination, Collectors.counting()))
                .entrySet().stream()
                .max((entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()))
                .map(entry -> entry.getKey().split(" ")[0]) // Get just the city name
                .orElse("N/A");
        favoriteDestinationLabel.setText(favoriteDestination);

        // Financial statistics
        double totalSpent = allRecords.size() * (250 + random.nextInt(400));
        totalSpentLabel.setText(String.format("$%,.0f", totalSpent));

        double averagePrice = totalSpent / allRecords.size();
        averagePriceLabel.setText(String.format("$%.0f", averagePrice));

        // This year flights
        long thisYearFlights = allRecords.stream()
                .filter(record -> record.getDepartureDate().getYear() == LocalDate.now().getYear())
                .count();
        thisYearFlightsLabel.setText(String.valueOf(thisYearFlights));

        // Frequent flyer status
        String status;
        if (allRecords.size() >= 20) {
            status = "Gold";
        } else if (allRecords.size() >= 10) {
            status = "Silver";
        } else {
            status = "Bronze";
        }
        frequentFlyerStatusLabel.setText(status);
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for flight history records
    public static class FlightHistoryRecord {
        private int flightNumber;
        private String origin;
        private String destination;
        private LocalDateTime departureDate;
        private String status;
        private LocalDateTime reservationDate;
        private String price;
        private String seat;

        // Constructors
        public FlightHistoryRecord() {}

        public FlightHistoryRecord(int flightNumber, String origin, String destination,
                                   LocalDateTime departureDate, String status,
                                   LocalDateTime reservationDate, String price, String seat) {
            this.flightNumber = flightNumber;
            this.origin = origin;
            this.destination = destination;
            this.departureDate = departureDate;
            this.status = status;
            this.reservationDate = reservationDate;
            this.price = price;
            this.seat = seat;
        }

        // Getters and setters
        public int getFlightNumber() { return flightNumber; }
        public void setFlightNumber(int flightNumber) { this.flightNumber = flightNumber; }

        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }

        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }

        public LocalDateTime getDepartureDate() { return departureDate; }
        public void setDepartureDate(LocalDateTime departureDate) { this.departureDate = departureDate; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public LocalDateTime getReservationDate() { return reservationDate; }
        public void setReservationDate(LocalDateTime reservationDate) { this.reservationDate = reservationDate; }

        public String getPrice() { return price; }
        public void setPrice(String price) { this.price = price; }

        public String getSeat() { return seat; }
        public void setSeat(String seat) { this.seat = seat; }
    }
}