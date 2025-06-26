package controller;

import domain.security.AuthenticationService;
import domain.security.User;
import domain.security.UserRole;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ucr.project.HelloApplication;
import util.FXUtility;

import java.io.IOException;
import java.util.Objects;

/**
 * Controlador para la ventana de login
 */
public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnRegister;

    private AuthenticationService authService;

    @FXML
    public void initialize() {
        authService = AuthenticationService.getInstance();

        // Configurar evento Enter en los campos de texto
        txtUsername.setOnAction(this::handleLogin);
        txtPassword.setOnAction(this::handleLogin);

        // Limpiar mensaje de error cuando el usuario empiece a escribir
        txtUsername.textProperty().addListener((observable, oldValue, newValue) -> hideError());
        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> hideError());
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // Validar campos vacíos
        if (username == null || username.trim().isEmpty()) {
            showError("Por favor ingrese su usuario");
            txtUsername.requestFocus();
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            showError("Por favor ingrese su contraseña");
            txtPassword.requestFocus();
            return;
        }

        // Intentar autenticación
        AuthenticationService.AuthenticationResult result = authService.authenticate(username, password);

        if (result.isSuccess()) {
            User user = result.getUser();

            // Mostrar mensaje de bienvenida
            String welcomeMessage = String.format("¡Bienvenido %s!\nRol: %s",
                    user.getUsername(),
                    user.getRole().getDisplayName());

            FXUtility.showMessage("Login Exitoso", welcomeMessage);

            // Cargar la ventana principal
            loadMainWindow(user);

        } else {
            showError(result.getMessage());
            txtPassword.clear();
            txtUsername.requestFocus();
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        txtUsername.clear();
        txtPassword.clear();
        hideError();
        txtUsername.requestFocus();
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("register-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Crear nueva ventana para registro
            Stage registerStage = new Stage();
            registerStage.setTitle("Registro de Usuario");
            registerStage.setScene(scene);
            registerStage.setResizable(false);
            registerStage.centerOnScreen();

            // Hacer modal (bloquea la ventana principal)
            registerStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            registerStage.initOwner(btnLogin.getScene().getWindow());

            registerStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            FXUtility.showErrorAlert("Error", "No se pudo cargar la ventana de registro: " + e.getMessage());
        }
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }

    private void hideError() {
        lblError.setVisible(false);
    }

    private void loadMainWindow(User user) {
        try {
            // Determinar qué ventana cargar según el rol
            String fxmlFile = user.getRole() == UserRole.ADMINISTRATOR ?
                    "hello-view.fxml" : "user-view.fxml";

            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load());

            // Obtener el stage actual
            Stage currentStage = (Stage) btnLogin.getScene().getWindow();
            String css = Objects.requireNonNull(HelloApplication.class.getResource("combined-styles.css")).toExternalForm();
            scene.getStylesheets().add(css);

            // Configurar la nueva escena
            currentStage.setTitle("Sistema de Gestión de Aeropuertos - " + user.getRole().getDisplayName());
            currentStage.setScene(scene);
            currentStage.setResizable(true);
            currentStage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            FXUtility.showErrorAlert("Error",
                    "No se pudo cargar la ventana principal: " + e.getMessage());
        }
    }
}