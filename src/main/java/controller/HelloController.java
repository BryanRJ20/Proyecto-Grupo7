package controller;

import domain.security.AuthenticationService;
import domain.security.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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
    @FXML
    private Label welcomeLabel;

    private AuthenticationService authService;

    @FXML
    public void initialize() {
        authService = AuthenticationService.getInstance();

        if (authService.isAuthenticated()) {
            User currentUser = authService.getCurrentUser();
            if (welcomeLabel != null) {
                welcomeLabel.setText("Welcome " + currentUser.getUsername() + "!");
            }
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
            load("passengers-view.fxml");
        } catch (Exception e) {
            showCompactError("Error", "Error cargando pasajeros: " + e.getMessage());
        }
    }

    @FXML
    public void flights(ActionEvent actionEvent) {
        try {
            load("flights-view.fxml");
        } catch (Exception e) {
            showCompactError("Error", "Error cargando vuelos: " + e.getMessage());
        }
    }

    @FXML
    public void simulation(ActionEvent actionEvent) {
        try {
            load("simulation-view.fxml");
        } catch (Exception e) {
            showCompactError("Error", "Error cargando simulación: " + e.getMessage());
        }
    }

    @FXML
    public void statistics(ActionEvent actionEvent) {
        try {
            load("statistics-view.fxml");
        } catch (Exception e) {
            showCompactError("Error", "Error cargando estadísticas: " + e.getMessage());
        }
    }

    @FXML
    public void exit(ActionEvent actionEvent) {
        authService.logout();
        FXUtility.showMessage("Sesión Cerrada", "Has cerrado sesión exitosamente");
        loadLoginWindow();
    }

    private void loadLoginWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage currentStage = (Stage) bp.getScene().getWindow();

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