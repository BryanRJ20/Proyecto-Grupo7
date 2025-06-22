package controller;

import domain.security.AuthenticationService;
import domain.security.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import ucr.project.HelloApplication;

import java.io.IOException;

/**
 * Controlador del menú - DEPRECADO
 * Se mantiene por compatibilidad, pero no se usa
 */
@Deprecated
public class MenuController {

    @javafx.fxml.FXML
    private BorderPane bp;
    @javafx.fxml.FXML
    private AnchorPane ap;
    @javafx.fxml.FXML
    private Label welcomeLabel;
    AuthenticationService authService;

    @FXML
    private Pane MPain;


    @FXML
    public void initialize() {
        authService = AuthenticationService.getInstance();

        // Configurar el mensaje de bienvenida
        if (authService.isAuthenticated()) {
            User currentUser = authService.getCurrentUser();
            welcomeLabel.setText("Welcome " + currentUser.getUsername() + "!");
        }
    }

    @javafx.fxml.FXML
    public void exit(ActionEvent actionEvent) {
        System.exit(0);
    }

    @javafx.fxml.FXML
    public void passengers(ActionEvent actionEvent) {
        try {
            load("passengers-view.fxml");
        } catch (Exception e) {showCompactError("Error", "Error cargando pasajeros: " + e.getMessage());}

    }

    @javafx.fxml.FXML
    public void simulation(ActionEvent actionEvent) {
        // Implementación pendiente
    }

    @javafx.fxml.FXML
    public void flights(ActionEvent actionEvent) {
        // Implementación pendiente
    }

    @javafx.fxml.FXML
    public void Home(ActionEvent actionEvent) {
        this.bp.setCenter(ap);
    }

    @javafx.fxml.FXML
    public void statistics(ActionEvent actionEvent) {
        // Implementación pendiente
    }

    @javafx.fxml.FXML
    public void airports(ActionEvent actionEvent) {
        try {
            load("airports-view.fxml");
        } catch (Exception e) {showCompactError("Error", "Error cargando aeropuertos: " + e.getMessage());}
    }



    public void load(String form) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(form));
            this.bp.setCenter(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            showCompactError("Error", "No se pudo cargar la vista: " + form +
                    "\nError: " + e.getMessage());
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