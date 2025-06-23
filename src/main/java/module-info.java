module ucr.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;
    requires jdk.xml.dom;

    // Exports principales
    exports domain;
    exports domain.list;
    exports domain.tree;
    exports domain.graph;
    exports domain.security;
    exports domain.queue;
    exports domain.stack;
    exports ucr.project;
    exports controller;
    exports simulation;
    exports util;

    // Opens para Gson serialization
    opens domain to com.google.gson;
    opens domain.list to com.google.gson;
    opens domain.tree to com.google.gson;
    opens domain.security to com.google.gson;
    opens controller to com.google.gson;

    // Opens para JavaFX FXML
    opens ucr.project to javafx.fxml;
    opens controller to javafx.fxml;
}