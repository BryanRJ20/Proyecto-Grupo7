module ucr.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens domain.list to com.google.gson;
    opens domain to com.google.gson;

    exports domain;
    opens ucr.project to javafx.fxml;
    exports ucr.project;
    exports controller;
    opens controller to javafx.fxml;
}