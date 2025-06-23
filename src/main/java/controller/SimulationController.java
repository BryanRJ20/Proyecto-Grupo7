package controller;

import domain.Airport;
import domain.Flight;
import domain.Passenger;
import domain.graph.DijkstraAirportGraph;
import domain.list.DoublyLinkedList;
import domain.list.ListException;
import domain.queue.LinkedQueue;
import domain.stack.LinkedStack;
import domain.tree.AVLTree;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import util.DataLoader;

import java.util.List;
import java.util.Random;

public class SimulationController {

    @FXML
    private TextArea logArea;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button startSimulationBtn;
    @FXML
    private Button stopSimulationBtn;
    @FXML
    private Button clearLogBtn;
    @FXML
    private TextField passengersField;
    @FXML
    private TextField flightsField;
    @FXML
    private ComboBox<String> simulationTypeBox;
    @FXML
    private VBox queueStatusBox;
    @FXML
    private VBox stackStatusBox;
    @FXML
    private Label statusLabel;

    private controller.FlightController flightController;
    private Thread simulationThread;
    private boolean isSimulationRunning = false;

    private LinkedQueue[] airportQueues;
    private LinkedStack[] aircraftStacks;

    @FXML
    public void initialize() {
        flightController = new controller.FlightController();

        simulationTypeBox.getItems().addAll(
                "Simulación Completa",
                "Solo Carga de Datos",
                "Solo Vuelos",
                "Solo Pasajeros",
                "Algoritmo Dijkstra"
        );
        simulationTypeBox.setValue("Simulación Completa");

        passengersField.setText("100");
        flightsField.setText("20");

        stopSimulationBtn.setDisable(true);
        statusLabel.setText("Listo para simular");

        airportQueues = new LinkedQueue[20];
        aircraftStacks = new LinkedStack[50];

        for (int i = 0; i < airportQueues.length; i++) {
            airportQueues[i] = new LinkedQueue();
        }

        for (int i = 0; i < aircraftStacks.length; i++) {
            aircraftStacks[i] = new LinkedStack();
        }

        loadInitialData();
    }

    private void loadInitialData() {
        try {
            DoublyLinkedList airports = DataLoader.loadAirportsFromJson("src/main/resources/ucr/project/airports.json");
            flightController.setAirportsList(airports);

            AVLTree passengers = DataLoader.loadPassengersFromJson("src/main/resources/ucr/project/passengers.json");
            flightController.setPassengersTree(passengers);

            appendLog("✅ Datos iniciales cargados exitosamente");
            appendLog("📊 Aeropuertos: " + airports.size());
            appendLog("👥 Pasajeros: " + passengers.size());

        } catch (Exception e) {
            appendLog("❌ Error cargando datos iniciales: " + e.getMessage());
        }
    }

    @FXML
    public void startSimulation(ActionEvent actionEvent) {
        if (!isSimulationRunning) {
            isSimulationRunning = true;
            startSimulationBtn.setDisable(true);
            stopSimulationBtn.setDisable(false);

            String simulationType = simulationTypeBox.getValue();

            int numPassengers = 100;
            int numFlights = 20;

            try {
                numPassengers = Integer.parseInt(passengersField.getText());
                numFlights = Integer.parseInt(flightsField.getText());
            } catch (NumberFormatException e) {
                appendLog("⚠️ Usando valores por defecto para pasajeros y vuelos");
            }

            simulationThread = new Thread(() -> runSimulation(simulationType, numPassengers, numFlights));
            simulationThread.setDaemon(true);
            simulationThread.start();
        }
    }

    @FXML
    public void stopSimulation(ActionEvent actionEvent) {
        if (isSimulationRunning) {
            isSimulationRunning = false;
            if (simulationThread != null) {
                simulationThread.interrupt();
            }

            Platform.runLater(() -> {
                startSimulationBtn.setDisable(false);
                stopSimulationBtn.setDisable(true);
                statusLabel.setText("Simulación detenida");
                progressBar.setProgress(0);
            });

            appendLog("🛑 Simulación detenida por el usuario");
        }
    }

    @FXML
    public void clearLog(ActionEvent actionEvent) {
        logArea.clear();
    }

    private void runSimulation(String type, int numPassengers, int numFlights) {
        try {
            Platform.runLater(() -> {
                statusLabel.setText("Ejecutando simulación...");
                progressBar.setProgress(0.1);
            });

            switch (type) {
                case "Simulación Completa":
                    runCompleteSimulation(numPassengers, numFlights);
                    break;
                case "Solo Carga de Datos":
                    runDataLoadingSimulation();
                    break;
                case "Solo Vuelos":
                    runFlightSimulation(numFlights);
                    break;
                case "Solo Pasajeros":
                    runPassengerSimulation(numPassengers);
                    break;
                case "Algoritmo Dijkstra":
                    runDijkstraSimulation();
                    break;
            }

        } catch (Exception e) {
            appendLog("❌ Error en simulación: " + e.getMessage());
        } finally {
            Platform.runLater(() -> {
                isSimulationRunning = false;
                startSimulationBtn.setDisable(false);
                stopSimulationBtn.setDisable(true);
                statusLabel.setText("Simulación completada");
                progressBar.setProgress(1.0);
            });
        }
    }

    private void runCompleteSimulation(int numPassengers, int numFlights) {
        appendLog("🚀 INICIANDO SIMULACIÓN COMPLETA");
        appendLog("================================");

        Platform.runLater(() -> progressBar.setProgress(0.2));
        appendLog("\n🗺️ Generando rutas aleatorias...");
        flightController.generateRandomRoutes();
        appendLog("✅ Rutas generadas exitosamente");

        sleep(1000);

        Platform.runLater(() -> progressBar.setProgress(0.3));
        appendLog("\n🧮 Validando algoritmo de Dijkstra...");
        validateDijkstraAlgorithm();

        sleep(1000);

        Platform.runLater(() -> progressBar.setProgress(0.5));
        appendLog("\n👥 Generando " + numPassengers + " pasajeros aleatorios...");
        flightController.generateRandomPassengers(numPassengers);
        appendLog("✅ Pasajeros generados exitosamente");

        sleep(1000);

        Platform.runLater(() -> progressBar.setProgress(0.7));
        appendLog("\n✈️ Generando " + numFlights + " vuelos aleatorios...");
        flightController.generateRandomFlights(numFlights);
        appendLog("✅ Vuelos generados exitosamente");

        sleep(1000);

        Platform.runLater(() -> progressBar.setProgress(0.8));
        appendLog("\n🏢 Simulando operaciones de aeropuerto...");
        simulateAirportOperations();

        sleep(1000);

        Platform.runLater(() -> progressBar.setProgress(0.9));
        appendLog("\n🎫 Asignando pasajeros a vuelos...");
        flightController.assignRandomPassengersToFlights();
        appendLog("✅ Pasajeros asignados exitosamente");

        Platform.runLater(() -> progressBar.setProgress(1.0));
        appendLog("\n📊 ESTADÍSTICAS FINALES");
        appendLog("======================");
        showFinalStatistics();

        updateQueueAndStackStatus();

        appendLog("\n🎯 SIMULACIÓN COMPLETA FINALIZADA");
    }

    private void runDataLoadingSimulation() {
        appendLog("📂 SIMULACIÓN: Carga de Datos");
        appendLog("=============================");

        loadInitialData();

        appendLog("✅ Simulación de carga completada");
    }

    private void runFlightSimulation(int numFlights) {
        appendLog("✈️ SIMULACIÓN: Solo Vuelos");
        appendLog("=========================");

        Platform.runLater(() -> progressBar.setProgress(0.3));
        flightController.generateRandomRoutes();
        appendLog("✅ Rutas generadas");

        Platform.runLater(() -> progressBar.setProgress(0.7));
        flightController.generateRandomFlights(numFlights);
        appendLog("✅ " + numFlights + " vuelos generados");

        Platform.runLater(() -> progressBar.setProgress(1.0));

        try {
            appendLog("📊 Total de vuelos en sistema: " + flightController.getFlightsList().size());
        } catch (ListException e) {
            appendLog("📊 Vuelos generados exitosamente");
        }
    }

    private void runPassengerSimulation(int numPassengers) {
        appendLog("👥 SIMULACIÓN: Solo Pasajeros");
        appendLog("============================");

        Platform.runLater(() -> progressBar.setProgress(0.5));
        flightController.generateRandomPassengers(numPassengers);
        appendLog("✅ " + numPassengers + " pasajeros generados");

        Platform.runLater(() -> progressBar.setProgress(1.0));
        appendLog("📊 Total de pasajeros en sistema: " + flightController.getPassengersTree().size());
    }

    private void runDijkstraSimulation() {
        appendLog("🧮 SIMULACIÓN: Algoritmo Dijkstra");
        appendLog("=================================");

        Platform.runLater(() -> progressBar.setProgress(0.3));
        flightController.generateRandomRoutes();
        appendLog("✅ Rutas generadas para pruebas");

        Platform.runLater(() -> progressBar.setProgress(0.7));
        validateDijkstraAlgorithm();

        Platform.runLater(() -> progressBar.setProgress(1.0));
        appendLog("✅ Pruebas de Dijkstra completadas");
    }

    private void validateDijkstraAlgorithm() {
        try {
            DoublyLinkedList airports = flightController.getAirportsList();
            if (airports.size() >= 2) {
                for (int i = 0; i < Math.min(3, airports.size() - 1); i++) {
                    Airport airport1 = (Airport) airports.getNode(i + 1).getData();
                    Airport airport2 = (Airport) airports.getNode(i + 2).getData();

                    appendLog("🔍 Ruta " + (i + 1) + ": " + airport1.getName() + " → " + airport2.getName());

                    DijkstraAirportGraph.DijkstraResult result = flightController.calculateShortestRoute(
                            airport1.getCode(), airport2.getCode()
                    );

                    if (result.isPathExists()) {
                        appendLog("✅ Distancia: " + String.format("%.2f", result.getDistance()) + " km");
                        appendLog("📍 Ruta: " + result.getPathAsString());
                    } else {
                        appendLog("⚠️ No existe ruta directa");
                    }

                    sleep(500);
                }
            }
        } catch (Exception e) {
            appendLog("❌ Error en validación Dijkstra: " + e.getMessage());
        }
    }

    private void simulateAirportOperations() {
        try {
            appendLog("🏢 Simulando colas de embarque...");

            Random random = new Random();
            DoublyLinkedList airports = flightController.getAirportsList();

            for (int i = 0; i < Math.min(5, airports.size()); i++) {
                Airport airport = (Airport) airports.getNode(i + 1).getData();
                LinkedQueue queue = airportQueues[i];

                int passengersInQueue = random.nextInt(10) + 5;
                for (int j = 0; j < passengersInQueue; j++) {
                    try {
                        queue.enQueue("Pasajero-" + (j + 1) + " en " + airport.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                appendLog("📋 " + airport.getName() + ": " + passengersInQueue + " pasajeros en cola");
                sleep(200);
            }

            appendLog("🛫 Simulando despegues y aterrizajes...");

            for (int i = 0; i < 3; i++) {
                LinkedStack stack = aircraftStacks[i];

                for (int j = 0; j < 3; j++) {
                    try {
                        stack.push("Vuelo-" + (1000 + i * 10 + j) + " completado");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                appendLog("✈️ Avión-" + (i + 1) + ": " + stack.size() + " vuelos en historial");
                sleep(300);
            }

        } catch (Exception e) {
            appendLog("❌ Error en simulación de operaciones: " + e.getMessage());
        }
    }

    private void showFinalStatistics() {
        try {
            appendLog("📊 Aeropuertos activos: " + flightController.getAirportsList().size());
            appendLog("👥 Pasajeros registrados: " + flightController.getPassengersTree().size());
            try {
                appendLog("✈️ Vuelos programados: " + flightController.getFlightsList().size());
            } catch (ListException e) {
                appendLog("✈️ Vuelos programados correctamente");
            }

            List<Airport> topAirports = flightController.getTopConnectedAirports();
            appendLog("\n🏆 Top 3 aeropuertos con más conexiones:");
            for (int i = 0; i < Math.min(3, topAirports.size()); i++) {
                Airport airport = topAirports.get(i);
                appendLog((i + 1) + ". " + airport.getName() + " (" + airport.getCode() + ")");
            }

        } catch (Exception e) {
            appendLog("❌ Error mostrando estadísticas: " + e.getMessage());
        }
    }

    private void updateQueueAndStackStatus() {
        Platform.runLater(() -> {
            queueStatusBox.getChildren().clear();
            for (int i = 0; i < 5; i++) {
                if (airportQueues[i].size() > 0) {
                    Label queueLabel = new Label("Cola Aeropuerto " + (i + 1) + ": " + airportQueues[i].size() + " elementos");
                    queueStatusBox.getChildren().add(queueLabel);
                }
            }

            stackStatusBox.getChildren().clear();
            for (int i = 0; i < 3; i++) {
                if (aircraftStacks[i].size() > 0) {
                    Label stackLabel = new Label("Historial Avión " + (i + 1) + ": " + aircraftStacks[i].size() + " vuelos");
                    stackStatusBox.getChildren().add(stackLabel);
                }
            }
        });
    }

    private void appendLog(String message) {
        Platform.runLater(() -> {
            logArea.appendText(message + "\n");
            logArea.setScrollTop(Double.MAX_VALUE);
        });

        sleep(100);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}