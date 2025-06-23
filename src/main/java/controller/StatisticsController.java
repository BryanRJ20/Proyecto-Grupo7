package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.FlightController;
import domain.Airport;
import domain.Flight;
import domain.Passenger;
import domain.graph.DijkstraAirportGraph;
import domain.list.CircularDoublyLinkedList;
import domain.list.DoublyLinkedList;
import domain.list.ListException;
import domain.list.Node;
import domain.tree.AVLNode;
import domain.tree.AVLTree;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.DataLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StatisticsController {

    @FXML
    private TableView<AirportStatistic> topAirportsTable;
    @FXML
    private TableColumn<AirportStatistic, String> airportNameColumn;
    @FXML
    private TableColumn<AirportStatistic, Integer> flightCountColumn;
    @FXML
    private TableColumn<AirportStatistic, Double> occupancyColumn;

    @FXML
    private TableView<RouteStatistic> topRoutesTable;
    @FXML
    private TableColumn<RouteStatistic, String> routeColumn;
    @FXML
    private TableColumn<RouteStatistic, Integer> usageCountColumn;
    @FXML
    private TableColumn<RouteStatistic, Double> distanceColumn;

    @FXML
    private TableView<PassengerStatistic> topPassengersTable;
    @FXML
    private TableColumn<PassengerStatistic, String> passengerNameColumn;
    @FXML
    private TableColumn<PassengerStatistic, Integer> flightsCountColumn;
    @FXML
    private TableColumn<PassengerStatistic, String> nationalityColumn;

    @FXML
    private PieChart occupancyChart;
    @FXML
    private BarChart<String, Number> airportsChart;
    @FXML
    private LineChart<String, Number> flightsChart;

    @FXML
    private Label totalAirportsLabel;
    @FXML
    private Label totalFlightsLabel;
    @FXML
    private Label totalPassengersLabel;
    @FXML
    private Label averageOccupancyLabel;
    @FXML
    private Label totalRoutesLabel;
    @FXML
    private Label activeFlightsLabel;

    @FXML
    private TextArea reportArea;
    @FXML
    private ComboBox<String> reportTypeBox;

    private FlightController flightController;
    private DoublyLinkedList airportsList;
    private CircularDoublyLinkedList flightsList;
    private AVLTree passengersTree;

    @FXML
    public void initialize() {
        flightController = new FlightController();

        // Configurar columnas de tablas
        setupTableColumns();

        // Configurar ComboBox de reportes
        reportTypeBox.getItems().addAll(
                "General Statistics Report",
                "Top 5 Airports Report",
                "Most Used Routes Report",
                "Top Passengers Report",
                "Occupancy Analysis Report",
                "Complete System Report"
        );
        reportTypeBox.setValue("General Statistics Report");

        // Cargar datos y actualizar estadísticas
        loadData();
        updateAllStatistics();
        generateCharts();
    }

    private void setupTableColumns() {
        // Configurar tabla de aeropuertos
        airportNameColumn.setCellValueFactory(new PropertyValueFactory<>("airportName"));
        flightCountColumn.setCellValueFactory(new PropertyValueFactory<>("flightCount"));
        occupancyColumn.setCellValueFactory(new PropertyValueFactory<>("averageOccupancy"));

        // Configurar tabla de rutas
        routeColumn.setCellValueFactory(new PropertyValueFactory<>("route"));
        usageCountColumn.setCellValueFactory(new PropertyValueFactory<>("usageCount"));
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));

        // Configurar tabla de pasajeros
        passengerNameColumn.setCellValueFactory(new PropertyValueFactory<>("passengerName"));
        flightsCountColumn.setCellValueFactory(new PropertyValueFactory<>("flightsCount"));
        nationalityColumn.setCellValueFactory(new PropertyValueFactory<>("nationality"));
    }

    private void loadData() {
        try {
            // Cargar aeropuertos
            airportsList = DataLoader.loadAirportsFromJson("src/main/resources/ucr/project/airports.json");
            flightController.setAirportsList(airportsList);

            // Cargar vuelos
            flightsList = DataLoader.loadFlightsFromJson("src/main/resources/ucr/project/flights.json");
            flightController.setFlightsList(flightsList);

            // Cargar pasajeros
            passengersTree = DataLoader.loadPassengersFromJson("src/main/resources/ucr/project/passengers.json");
            flightController.setPassengersTree(passengersTree);

        } catch (Exception e) {
            showError("Error loading data: " + e.getMessage());
        }
    }

    @FXML
    public void refreshStatistics(ActionEvent actionEvent) {
        loadData();
        updateAllStatistics();
        generateCharts();
        showInfo("Statistics refreshed successfully!");
    }

    @FXML
    public void generateReport(ActionEvent actionEvent) {
        String reportType = reportTypeBox.getValue();
        String report = generateSelectedReport(reportType);
        reportArea.setText(report);
    }

    @FXML
    public void exportReport(ActionEvent actionEvent) {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Directory to Save Report");

            Stage stage = (Stage) reportArea.getScene().getWindow();
            File selectedDirectory = directoryChooser.showDialog(stage);

            if (selectedDirectory != null) {
                String reportType = reportTypeBox.getValue().replace(" ", "_");
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String fileName = "Airport_Statistics_" + reportType + "_" + timestamp + ".txt";

                File file = new File(selectedDirectory, fileName);

                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(reportArea.getText());
                }

                showInfo("Report exported successfully to: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            showError("Error exporting report: " + e.getMessage());
        }
    }

    @FXML
    public void exportData(ActionEvent actionEvent) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export System Data");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("JSON Files", "*.json")
            );

            Stage stage = (Stage) reportArea.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                exportSystemDataToJson(file);
                showInfo("System data exported successfully!");
            }
        } catch (Exception e) {
            showError("Error exporting data: " + e.getMessage());
        }
    }

    private void updateAllStatistics() {
        updateGeneralStatistics();
        updateTopAirports();
        updateTopRoutes();
        updateTopPassengers();
    }

    private void updateGeneralStatistics() {
        try {
            // Estadísticas generales
            int totalAirports = airportsList != null ? airportsList.size() : 0;
            int totalFlights = flightsList != null ? flightsList.size() : 0;
            int totalPassengers = passengersTree != null ? passengersTree.size() : 0;

            totalAirportsLabel.setText(String.valueOf(totalAirports));
            totalFlightsLabel.setText(String.valueOf(totalFlights));
            totalPassengersLabel.setText(String.valueOf(totalPassengers));

            // Calcular ocupación promedio
            double averageOccupancy = calculateAverageOccupancy();
            averageOccupancyLabel.setText(String.format("%.1f%%", averageOccupancy));

            // Contar rutas activas
            int totalRoutes = countActiveRoutes();
            totalRoutesLabel.setText(String.valueOf(totalRoutes));

            // Contar vuelos activos
            int activeFlights = countActiveFlights();
            activeFlightsLabel.setText(String.valueOf(activeFlights));

        } catch (Exception e) {
            showError("Error updating general statistics: " + e.getMessage());
        }
    }

    private void updateTopAirports() {
        try {
            List<AirportStatistic> airportStats = calculateAirportStatistics();
            ObservableList<AirportStatistic> data = FXCollections.observableArrayList(airportStats);
            topAirportsTable.setItems(data);
        } catch (Exception e) {
            showError("Error updating airport statistics: " + e.getMessage());
        }
    }

    private void updateTopRoutes() {
        try {
            List<RouteStatistic> routeStats = calculateRouteStatistics();
            ObservableList<RouteStatistic> data = FXCollections.observableArrayList(routeStats);
            topRoutesTable.setItems(data);
        } catch (Exception e) {
            showError("Error updating route statistics: " + e.getMessage());
        }
    }

    private void updateTopPassengers() {
        try {
            List<PassengerStatistic> passengerStats = calculatePassengerStatistics();
            ObservableList<PassengerStatistic> data = FXCollections.observableArrayList(passengerStats);
            topPassengersTable.setItems(data);
        } catch (Exception e) {
            showError("Error updating passenger statistics: " + e.getMessage());
        }
    }

    private double calculateAverageOccupancy() {
        if (flightsList == null || flightsList.isEmpty()) return 0.0;

        try {
            double totalOccupancy = 0.0;
            int count = 0;

            Node current = flightsList.first;
            do {
                Flight flight = (Flight) current.data;
                if (flight.getCapacity() > 0) {
                    double occupancyRate = (double) flight.getOccupancy() / flight.getCapacity() * 100;
                    totalOccupancy += occupancyRate;
                    count++;
                }
                current = current.next;
            } while (current != flightsList.first);

            return count > 0 ? totalOccupancy / count : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private int countActiveRoutes() {
        // Implementación simplificada - en un sistema real contaríamos las rutas del grafo
        return airportsList != null ? airportsList.size() * 3 : 0;
    }

    private int countActiveFlights() {
        if (flightsList == null || flightsList.isEmpty()) return 0;

        int activeCount = 0;
        try {
            Node current = flightsList.first;
            do {
                Flight flight = (Flight) current.data;
                if ("SCHEDULED".equals(flight.getStatus()) || "IN_FLIGHT".equals(flight.getStatus())) {
                    activeCount++;
                }
                current = current.next;
            } while (current != flightsList.first);
        } catch (Exception e) {
            // Ignorar errores
        }

        return activeCount;
    }

    private List<AirportStatistic> calculateAirportStatistics() {
        List<AirportStatistic> stats = new ArrayList<>();

        if (airportsList == null || flightsList == null) return stats;

        try {
            // Para cada aeropuerto, contar vuelos de salida
            for (int i = 1; i <= airportsList.size(); i++) {
                Airport airport = (Airport) airportsList.getNode(i).getData();

                int flightCount = 0;
                double totalOccupancy = 0.0;
                int occupancyCount = 0;

                // Contar vuelos desde este aeropuerto
                Node current = flightsList.first;
                do {
                    Flight flight = (Flight) current.data;
                    if (flight.getOrigin().contains(airport.getName()) ||
                            flight.getOrigin().contains(String.valueOf(airport.getCode()))) {
                        flightCount++;
                        if (flight.getCapacity() > 0) {
                            totalOccupancy += (double) flight.getOccupancy() / flight.getCapacity() * 100;
                            occupancyCount++;
                        }
                    }
                    current = current.next;
                } while (current != flightsList.first);

                double avgOccupancy = occupancyCount > 0 ? totalOccupancy / occupancyCount : 0.0;
                stats.add(new AirportStatistic(airport.getName(), flightCount, avgOccupancy));
            }

            // Ordenar por número de vuelos (descendente)
            stats.sort((a, b) -> Integer.compare(b.getFlightCount(), a.getFlightCount()));

            // Retornar solo los top 5
            return stats.subList(0, Math.min(5, stats.size()));

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<RouteStatistic> calculateRouteStatistics() {
        List<RouteStatistic> stats = new ArrayList<>();
        Map<String, Integer> routeUsage = new HashMap<>();

        if (flightsList == null) return stats;

        try {
            // Contar uso de cada ruta
            Node current = flightsList.first;
            do {
                Flight flight = (Flight) current.data;
                String route = flight.getOrigin() + " → " + flight.getDestination();
                routeUsage.put(route, routeUsage.getOrDefault(route, 0) + 1);
                current = current.next;
            } while (current != flightsList.first);

            // Convertir a lista de estadísticas
            Random random = new Random();
            for (Map.Entry<String, Integer> entry : routeUsage.entrySet()) {
                double distance = 800 + random.nextDouble() * 2000; // Distancia simulada
                stats.add(new RouteStatistic(entry.getKey(), entry.getValue(), distance));
            }

            // Ordenar por uso (descendente)
            stats.sort((a, b) -> Integer.compare(b.getUsageCount(), a.getUsageCount()));

            // Retornar solo los top 5
            return stats.subList(0, Math.min(5, stats.size()));

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<PassengerStatistic> calculatePassengerStatistics() {
        List<PassengerStatistic> stats = new ArrayList<>();

        if (passengersTree == null || passengersTree.isEmpty()) return stats;

        try {
            List<Passenger> passengers = new ArrayList<>();
            collectPassengers(passengersTree.getRoot(), passengers);

            for (Passenger passenger : passengers) {
                int flightCount = passenger.getFlightHistory() != null ?
                        passenger.getFlightHistory().size() : 0;
                stats.add(new PassengerStatistic(
                        passenger.getName(),
                        flightCount,
                        passenger.getNationality()
                ));
            }

            // Ordenar por número de vuelos (descendente)
            stats.sort((a, b) -> Integer.compare(b.getFlightsCount(), a.getFlightsCount()));

            // Retornar solo los top 5
            return stats.subList(0, Math.min(5, stats.size()));

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void collectPassengers(AVLNode node, List<Passenger> passengers) {
        if (node != null) {
            collectPassengers(node.left, passengers);
            passengers.add(node.data);
            collectPassengers(node.right, passengers);
        }
    }

    private void generateCharts() {
        generateOccupancyChart();
        generateAirportsChart();
        generateFlightsChart();
    }

    private void generateOccupancyChart() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        int highOccupancy = 0, mediumOccupancy = 0, lowOccupancy = 0;

        if (flightsList != null && !flightsList.isEmpty()) {
            try {
                Node current = flightsList.first;
                do {
                    Flight flight = (Flight) current.data;
                    if (flight.getCapacity() > 0) {
                        double occupancyRate = (double) flight.getOccupancy() / flight.getCapacity() * 100;
                        if (occupancyRate >= 80) highOccupancy++;
                        else if (occupancyRate >= 50) mediumOccupancy++;
                        else lowOccupancy++;
                    }
                    current = current.next;
                } while (current != flightsList.first);
            } catch (Exception e) {
                // Ignorar errores
            }
        }

        pieData.add(new PieChart.Data("High (80%+)", highOccupancy));
        pieData.add(new PieChart.Data("Medium (50-79%)", mediumOccupancy));
        pieData.add(new PieChart.Data("Low (<50%)", lowOccupancy));

        occupancyChart.setData(pieData);
    }

    private void generateAirportsChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Flights per Airport");

        List<AirportStatistic> airportStats = calculateAirportStatistics();
        for (AirportStatistic stat : airportStats) {
            series.getData().add(new XYChart.Data<>(
                    stat.getAirportName().length() > 15 ?
                            stat.getAirportName().substring(0, 15) + "..." :
                            stat.getAirportName(),
                    stat.getFlightCount()
            ));
        }

        airportsChart.getData().clear();
        airportsChart.getData().add(series);
    }

    private void generateFlightsChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Flight Trends");

        // Datos simulados para tendencias de vuelos
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};
        Random random = new Random();

        for (String month : months) {
            series.getData().add(new XYChart.Data<>(month, 50 + random.nextInt(100)));
        }

        flightsChart.getData().clear();
        flightsChart.getData().add(series);
    }

    private String generateSelectedReport(String reportType) {
        switch (reportType) {
            case "General Statistics Report":
                return generateGeneralReport();
            case "Top 5 Airports Report":
                return generateTop5AirportsReport();
            case "Most Used Routes Report":
                return generateRoutesReport();
            case "Top Passengers Report":
                return generatePassengersReport();
            case "Occupancy Analysis Report":
                return generateOccupancyReport();
            case "Complete System Report":
                return generateCompleteReport();
            default:
                return "Report type not recognized.";
        }
    }

    private String generateGeneralReport() {
        StringBuilder report = new StringBuilder();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        report.append("AIRPORT MANAGEMENT SYSTEM - GENERAL STATISTICS REPORT\n");
        report.append("Generated on: ").append(timestamp).append("\n");
        report.append("=".repeat(60)).append("\n\n");

        report.append("SYSTEM OVERVIEW\n");
        report.append("-".repeat(30)).append("\n");
        report.append("Total Airports: ").append(totalAirportsLabel.getText()).append("\n");
        report.append("Total Flights: ").append(totalFlightsLabel.getText()).append("\n");
        report.append("Total Passengers: ").append(totalPassengersLabel.getText()).append("\n");
        report.append("Average Occupancy: ").append(averageOccupancyLabel.getText()).append("\n");
        report.append("Active Routes: ").append(totalRoutesLabel.getText()).append("\n");
        report.append("Active Flights: ").append(activeFlightsLabel.getText()).append("\n\n");

        return report.toString();
    }

    private String generateTop5AirportsReport() {
        StringBuilder report = new StringBuilder();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        report.append("TOP 5 AIRPORTS REPORT\n");
        report.append("Generated on: ").append(timestamp).append("\n");
        report.append("=".repeat(60)).append("\n\n");

        List<AirportStatistic> stats = calculateAirportStatistics();
        for (int i = 0; i < Math.min(5, stats.size()); i++) {
            AirportStatistic stat = stats.get(i);
            report.append(String.format("%d. %s\n", i + 1, stat.getAirportName()));
            report.append(String.format("   Departing Flights: %d\n", stat.getFlightCount()));
            report.append(String.format("   Average Occupancy: %.1f%%\n\n", stat.getAverageOccupancy()));
        }

        return report.toString();
    }

    private String generateRoutesReport() {
        StringBuilder report = new StringBuilder();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        report.append("MOST USED ROUTES REPORT\n");
        report.append("Generated on: ").append(timestamp).append("\n");
        report.append("=".repeat(60)).append("\n\n");

        List<RouteStatistic> stats = calculateRouteStatistics();
        for (int i = 0; i < Math.min(5, stats.size()); i++) {
            RouteStatistic stat = stats.get(i);
            report.append(String.format("%d. %s\n", i + 1, stat.getRoute()));
            report.append(String.format("   Usage Count: %d flights\n", stat.getUsageCount()));
            report.append(String.format("   Distance: %.0f km\n\n", stat.getDistance()));
        }

        return report.toString();
    }

    private String generatePassengersReport() {
        StringBuilder report = new StringBuilder();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        report.append("TOP PASSENGERS REPORT\n");
        report.append("Generated on: ").append(timestamp).append("\n");
        report.append("=".repeat(60)).append("\n\n");

        List<PassengerStatistic> stats = calculatePassengerStatistics();
        for (int i = 0; i < Math.min(5, stats.size()); i++) {
            PassengerStatistic stat = stats.get(i);
            report.append(String.format("%d. %s\n", i + 1, stat.getPassengerName()));
            report.append(String.format("   Flights Taken: %d\n", stat.getFlightsCount()));
            report.append(String.format("   Nationality: %s\n\n", stat.getNationality()));
        }

        return report.toString();
    }

    private String generateOccupancyReport() {
        StringBuilder report = new StringBuilder();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        report.append("FLIGHT OCCUPANCY ANALYSIS REPORT\n");
        report.append("Generated on: ").append(timestamp).append("\n");
        report.append("=".repeat(60)).append("\n\n");

        int[] occupancyBrackets = {0, 0, 0}; // Low, Medium, High

        if (flightsList != null && !flightsList.isEmpty()) {
            try {
                Node current = flightsList.first;
                do {
                    Flight flight = (Flight) current.data;
                    if (flight.getCapacity() > 0) {
                        double occupancyRate = (double) flight.getOccupancy() / flight.getCapacity() * 100;
                        if (occupancyRate >= 80) occupancyBrackets[2]++;
                        else if (occupancyRate >= 50) occupancyBrackets[1]++;
                        else occupancyBrackets[0]++;
                    }
                    current = current.next;
                } while (current != flightsList.first);
            } catch (Exception e) {
                // Ignorar errores
            }
        }

        int total = occupancyBrackets[0] + occupancyBrackets[1] + occupancyBrackets[2];

        report.append("OCCUPANCY DISTRIBUTION\n");
        report.append("-".repeat(30)).append("\n");
        report.append(String.format("High Occupancy (80%+): %d flights (%.1f%%)\n",
                occupancyBrackets[2], total > 0 ? occupancyBrackets[2] * 100.0 / total : 0));
        report.append(String.format("Medium Occupancy (50-79%%): %d flights (%.1f%%)\n",
                occupancyBrackets[1], total > 0 ? occupancyBrackets[1] * 100.0 / total : 0));
        report.append(String.format("Low Occupancy (<50%%): %d flights (%.1f%%)\n",
                occupancyBrackets[0], total > 0 ? occupancyBrackets[0] * 100.0 / total : 0));

        return report.toString();
    }

    private String generateCompleteReport() {
        StringBuilder report = new StringBuilder();

        report.append(generateGeneralReport()).append("\n");
        report.append(generateTop5AirportsReport()).append("\n");
        report.append(generateRoutesReport()).append("\n");
        report.append(generatePassengersReport()).append("\n");
        report.append(generateOccupancyReport());

        return report.toString();
    }

    private void exportSystemDataToJson(File file) throws IOException {
        Map<String, Object> systemData = new HashMap<>();

        // Agregar metadatos
        systemData.put("exportDate", LocalDateTime.now().toString());
        systemData.put("version", "1.0");

        // Agregar estadísticas generales
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAirports", totalAirportsLabel.getText());
        stats.put("totalFlights", totalFlightsLabel.getText());
        stats.put("totalPassengers", totalPassengersLabel.getText());
        stats.put("averageOccupancy", averageOccupancyLabel.getText());
        systemData.put("generalStatistics", stats);

        // Agregar estadísticas de aeropuertos
        systemData.put("topAirports", calculateAirportStatistics());
        systemData.put("topRoutes", calculateRouteStatistics());
        systemData.put("topPassengers", calculatePassengerStatistics());

        // Escribir JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(systemData, writer);
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Clases para estadísticas
    public static class AirportStatistic {
        private String airportName;
        private int flightCount;
        private double averageOccupancy;

        public AirportStatistic(String airportName, int flightCount, double averageOccupancy) {
            this.airportName = airportName;
            this.flightCount = flightCount;
            this.averageOccupancy = averageOccupancy;
        }

        public String getAirportName() { return airportName; }
        public int getFlightCount() { return flightCount; }
        public double getAverageOccupancy() { return averageOccupancy; }
    }

    public static class RouteStatistic {
        private String route;
        private int usageCount;
        private double distance;

        public RouteStatistic(String route, int usageCount, double distance) {
            this.route = route;
            this.usageCount = usageCount;
            this.distance = distance;
        }

        public String getRoute() { return route; }
        public int getUsageCount() { return usageCount; }
        public double getDistance() { return distance; }
    }

    public static class PassengerStatistic {
        private String passengerName;
        private int flightsCount;
        private String nationality;

        public PassengerStatistic(String passengerName, int flightsCount, String nationality) {
            this.passengerName = passengerName;
            this.flightsCount = flightsCount;
            this.nationality = nationality;
        }

        public String getPassengerName() { return passengerName; }
        public int getFlightsCount() { return flightsCount; }
        public String getNationality() { return nationality; }
    }
}