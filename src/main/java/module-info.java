module ucr.project {
    requires javafx.controls;
    requires javafx.fxml;



    opens ucr.project to javafx.fxml;
    exports ucr.project;
    exports controller;
    opens controller to javafx.fxml;
}