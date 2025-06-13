package controller;

import controller.FlightController;
import domain.security.AuthenticationService;
import domain.security.User;
import simulation.SimulationRunner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ucr.project.HelloApplication;
import util.FXUtility;

import java.io.IOException;

public class HelloController {
    @FXML
    private BorderPane bp;
    @FXML
    private AnchorPane ap;

    private AuthenticationService authService;
    private FlightController flightController;
    private SimulationRunner simulationRunner;

    @FXML
    public void initialize() {
        authService = AuthenticationService.getInstance();
        flightController = new FlightController();
        simulationRunner = new SimulationRunner();

        // Verificar si hay un usuario autenticado
        if (authService.isAuthenticated()) {
            User currentUser = authService.getCurrentUser();
            System.out.println("Usuario autenticado: " + currentUser.getUsername() +
                    " - Rol: " + currentUser.getRole().getDisplayName());
        }
    }

    private void load(String form) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(form));
            this.bp.setCenter(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            showCompactError("Error", "No se pudo cargar la vista: " + form +
                    "\nError: " + e.getMessage());
        }
    }

    @FXML
    public void Home(ActionEvent actionEvent) {
        this.bp.setCenter(ap);
    }

    @FXML
    public void airports(ActionEvent actionEvent) {
        try {
            load("airports-view.fxml");
        } catch (Exception e) {
            showCompactError("Error", "Error cargando aeropuertos: " + e.getMessage());
        }
    }

    @FXML
    public void passengers(ActionEvent actionEvent) {
        try {
            // Mostrar información sobre pasajeros
            String passengerInfo = "Gestión de Pasajeros\n\n";
            passengerInfo += "Total de pasajeros registrados: " + flightController.getPassengersTree().size() + "\n";
            passengerInfo += "Los pasajeros están organizados en un Árbol AVL para búsqueda eficiente.\n\n";
            passengerInfo += "Funcionalidades disponibles:\n";
            passengerInfo += "• Búsqueda por ID\n";
            passengerInfo += "• Historial de vuelos\n";
            passengerInfo += "• Registro de nuevos pasajeros\n\n";
            passengerInfo += "Nota: Funcionalidad completa estará disponible en el segundo entregable.";

            FXUtility.showMessage("Pasajeros", passengerInfo);
        } catch (Exception e) {
            FXUtility.showMessage("Pasajeros", "Funcionalidad de pasajeros en desarrollo...");
        }
    }

    @FXML
    public void flights(ActionEvent actionEvent) {
        try {
            // Mostrar información sobre vuelos
            String flightInfo = "Gestión de Vuelos\n\n";
            flightInfo += "Total de vuelos: " + flightController.getFlightsList().size() + "\n";
            flightInfo += "Los vuelos están organizados en una Lista Circular Doblemente Enlazada.\n\n";
            flightInfo += "Funcionalidades implementadas:\n";
            flightInfo += "• Generación automática de vuelos\n";
            flightInfo += "• Asignación de pasajeros\n";
            flightInfo += "• Cálculo de rutas con Dijkstra\n";
            flightInfo += "• Estadísticas de ocupación\n\n";
            flightInfo += "Nota: Interfaz gráfica completa estará disponible en el segundo entregable.";

            FXUtility.showMessage("Vuelos", flightInfo);
        } catch (Exception e) {
            FXUtility.showMessage("Vuelos", "Funcionalidad de vuelos en desarrollo...");
        }
    }

    @FXML
    public void simulation(ActionEvent actionEvent) {
        try {
            // Ejecutar simulación por consola
            FXUtility.showMessage("Simulación",
                    "Iniciando simulación de red aérea por consola...\n\n" +
                            "Revise la consola para ver el proceso completo:\n" +
                            "1. Carga de aeropuertos desde JSON\n" +
                            "2. Generación de rutas aleatorias\n" +
                            "3. Validación del algoritmo de Dijkstra\n" +
                            "4. Generación de pasajeros\n" +
                            "5. Creación de vuelos\n" +
                            "6. Simulación de red aérea\n\n" +
                            "La simulación se ejecutará en un hilo separado.");

            // Ejecutar simulación en hilo separado para no bloquear la interfaz
            Thread simulationThread = new Thread(() -> {
                try {
                    simulationRunner.runSimulation();
                } catch (Exception e) {
                    System.err.println("Error en simulación: " + e.getMessage());
                }
            });

            simulationThread.setDaemon(true);
            simulationThread.start();

        } catch (Exception e) {
            FXUtility.showErrorAlert("Error", "Error iniciando simulación: " + e.getMessage());
        }
    }

    @FXML
    public void statistics(ActionEvent actionEvent) {
        try {
            // Mostrar estadísticas en consola
            System.out.println("\n=== MOSTRANDO ESTADÍSTICAS ===");
            flightController.showFlightStatistics();
            flightController.showAirportNetwork();

            // Mostrar mensaje en interfaz
            String statsInfo = "Estadísticas del Sistema\n\n";
            statsInfo += "Las estadísticas se han mostrado en la consola.\n\n";
            statsInfo += "Información disponible:\n";
            statsInfo += "• Total de aeropuertos\n";
            statsInfo += "• Total de vuelos\n";
            statsInfo += "• Ocupación promedio\n";
            statsInfo += "• Aeropuertos con más conexiones\n";
            statsInfo += "• Red de rutas\n\n";
            statsInfo += "Revise la consola para ver los detalles completos.";

            FXUtility.showMessage("Estadísticas", statsInfo);
        } catch (Exception e) {
            FXUtility.showMessage("Estadísticas", "Funcionalidad de estadísticas en desarrollo...");
        }
    }

    @FXML
    public void exit(ActionEvent actionEvent) {
        // Cerrar sesión y volver al login
        authService.logout();
        FXUtility.showMessage("Sesión Cerrada", "Has cerrado sesión exitosamente");
        loadLoginWindow();
    }

    private void loadLoginWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Obtener el stage actual
            Stage currentStage = (Stage) bp.getScene().getWindow();

            // Configurar la nueva escena
            currentStage.setTitle("Sistema de Gestión de Aeropuertos - Login");
            currentStage.setScene(scene);
            currentStage.setResizable(false);
            currentStage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showCompactError("Error",
                    "No se pudo cargar la ventana de login: " + e.getMessage());
        }
    }

    private void showCompactError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setPrefSize(400, 250);
        alert.getDialogPane().setMaxWidth(400);
        alert.getDialogPane().setMaxHeight(250);
        alert.showAndWait();
    }
}