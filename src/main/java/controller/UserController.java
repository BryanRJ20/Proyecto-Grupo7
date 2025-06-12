package controller;

import domain.security.AuthenticationService;
import domain.security.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ucr.project.HelloApplication;
import util.FXUtility;

import java.io.IOException;

/**
 * Controlador para la vista de usuarios regulares
 */
public class UserController {

    @FXML
    private BorderPane bp;

    @FXML
    private AnchorPane ap;

    @FXML
    private Label lblWelcome;

    private AuthenticationService authService;

    @FXML
    public void initialize() {
        authService = AuthenticationService.getInstance();

        // Configurar el mensaje de bienvenida
        if (authService.isAuthenticated()) {
            User currentUser = authService.getCurrentUser();
            lblWelcome.setText("¡Bienvenido " + currentUser.getUsername() + "!");
        }
    }

    @FXML
    public void Home(ActionEvent actionEvent) {
        this.bp.setCenter(ap);
    }

    @FXML
    public void reservations(ActionEvent actionEvent) {
        FXUtility.showMessage("Reservas", "Funcionalidad de reservas en desarrollo...");
        // Aquí se cargará la vista de reservas cuando esté implementada
    }

    @FXML
    public void flights(ActionEvent actionEvent) {
        FXUtility.showMessage("Vuelos", "Funcionalidad de consulta de vuelos en desarrollo...");
        // Aquí se cargará la vista de vuelos cuando esté implementada
    }

    @FXML
    public void history(ActionEvent actionEvent) {
        FXUtility.showMessage("Historial", "Funcionalidad de historial en desarrollo...");
        // Aquí se cargará la vista de historial cuando esté implementada
    }

    @FXML
    public void logout(ActionEvent actionEvent) {
        // Confirmar logout
        authService.logout();

        FXUtility.showMessage("Sesión Cerrada", "Has cerrado sesión exitosamente");

        // Volver a la pantalla de login
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
            FXUtility.showErrorAlert("Error",
                    "No se pudo cargar la ventana de login: " + e.getMessage());
        }
    }
}