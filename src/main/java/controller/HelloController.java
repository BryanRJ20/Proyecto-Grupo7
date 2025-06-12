package controller;

import domain.security.AuthenticationService;
import domain.security.User;
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

    @FXML
    public void initialize() {
        authService = AuthenticationService.getInstance();

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
        FXUtility.showMessage("Aeropuertos", "Funcionalidad de aeropuertos en desarrollo...");
        // load("airports-view.fxml");
    }

    @FXML
    public void passengers(ActionEvent actionEvent) {
        FXUtility.showMessage("Pasajeros", "Funcionalidad de pasajeros en desarrollo...");
        // load("passengers-view.fxml");
    }

    @FXML
    public void flights(ActionEvent actionEvent) {
        FXUtility.showMessage("Vuelos", "Funcionalidad de vuelos en desarrollo...");
        // load("flights-view.fxml");
    }

    @FXML
    public void simulation(ActionEvent actionEvent) {
        FXUtility.showMessage("Simulación", "Funcionalidad de simulación en desarrollo...");
        // load("simulation-view.fxml");
    }

    @FXML
    public void statistics(ActionEvent actionEvent) {
        FXUtility.showMessage("Estadísticas", "Funcionalidad de estadísticas en desarrollo...");
        // load("statistics-view.fxml");
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