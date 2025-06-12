package ucr.project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Cambiar para que inicie con la ventana de login
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        String css = Objects.requireNonNull(HelloApplication.class.getResource("combined-styles.css")).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("Sistema de Gestión de Aeropuertos - Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}