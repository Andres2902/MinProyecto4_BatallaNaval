module com.example.batalla_naval {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.batalla_naval to javafx.fxml;
    opens com.example.batalla_naval.view to javafx.fxml;
    opens com.example.batalla_naval.controller to javafx.fxml;
    opens com.example.batalla_naval.model to javafx.base;

    exports com.example.batalla_naval;
    exports com.example.batalla_naval.view;
    exports com.example.batalla_naval.controller;
}