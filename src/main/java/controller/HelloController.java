package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import ucr.project.HelloApplication;

import java.io.IOException;

public class HelloController {
    @FXML
    private Text txtMessage;
    @FXML
    private BorderPane bp;
    @FXML
    private AnchorPane ap;

    @FXML
    public void initialize() {
        txtMessage.setText("Laboratory No. 11");
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
        this.txtMessage.setText("Laboratory No. 11");
    }

    @FXML
    public void matrixGraphView(ActionEvent actionEvent) {
        try {
            load("matrixGraph-view.fxml");
            txtMessage.setText("Matriz de Adyacencia - Visualización");
        } catch (Exception e) {
            showCompactError("Error", "Error cargando Matrix Graph: " + e.getMessage());
        }
    }

    @FXML
    public void listGraph(ActionEvent actionEvent) {
        try {
            load("listGraph-view.fxml");
            txtMessage.setText("Lista de Adyacencia - Visualización");
        } catch (Exception e) {
            showCompactError("Error", "Error cargando List Graph: " + e.getMessage());
        }
    }

    @FXML
    public void linkedGraph(ActionEvent actionEvent) {
        try {
            load("linkedGraph-view.fxml");
            txtMessage.setText("Lista Enlazada - Visualización");
        } catch (Exception e) {
            showCompactError("Error", "Error cargando Linked Graph: " + e.getMessage());
        }
    }

    @FXML
    public void matrixOperations(ActionEvent actionEvent) {
        try {
            load("matrixOperations-view.fxml");
            txtMessage.setText("Operaciones con Matriz de Adyacencia");
        } catch (Exception e) {
            showCompactError("Error", "Error cargando Matrix Operations: " + e.getMessage());
        }
    }

    @FXML
    public void listOperations(ActionEvent actionEvent) {
        try {
            load("listOperations-view.fxml");
            txtMessage.setText("Operaciones con Lista de Adyacencia");
        } catch (Exception e) {
            showCompactError("Error", "Error cargando List Operations: " + e.getMessage());
        }
    }

    @FXML
    public void linkedOperations(ActionEvent actionEvent) {
        try {
            load("linkedOperations-view.fxml");
            txtMessage.setText("Operaciones con Lista Enlazada");
        } catch (Exception e) {
            showCompactError("Error", "Error cargando Linked Operations: " + e.getMessage());
        }
    }

    @FXML
    public void exit(ActionEvent actionEvent) {
        System.exit(0);
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