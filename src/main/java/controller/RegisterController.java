package controller;

import domain.security.AuthenticationService;
import domain.security.UserRole;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import util.FXUtility;

/**
 * Controlador para la ventana de registro de usuarios
 */
public class RegisterController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private ComboBox<UserRole> cmbRole;

    @FXML
    private Label lblError;

    @FXML
    private Button btnRegister;

    @FXML
    private Button btnCancel;

    private AuthenticationService authService;

    @FXML
    public void initialize() {
        authService = AuthenticationService.getInstance();

        // Configurar ComboBox de roles
        cmbRole.getItems().addAll(UserRole.USER, UserRole.ADMINISTRATOR);
        cmbRole.setValue(UserRole.USER); // Valor por defecto

        // Configurar eventos
        txtUsername.textProperty().addListener((observable, oldValue, newValue) -> hideError());
        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> hideError());
        txtConfirmPassword.textProperty().addListener((observable, oldValue, newValue) -> hideError());
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        UserRole selectedRole = cmbRole.getValue();

        // Intentar registrar usuario
        AuthenticationService.RegistrationResult result =
                authService.registerUserWithValidation(username, password, confirmPassword, selectedRole);

        if (result.isSuccess()) {
            FXUtility.showMessage("Registro Exitoso",
                    "Usuario '" + username + "' registrado correctamente.\n" +
                            "Rol: " + selectedRole.getDisplayName());
            closeWindow();
        } else {
            showError(result.getMessage());
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }

    private void hideError() {
        lblError.setVisible(false);
    }

    private void closeWindow() {
        Stage stage = (Stage) btnRegister.getScene().getWindow();
        stage.close();
    }
}