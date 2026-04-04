module com.cs210.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // required for JDBC / MySQL integration

    opens com.cs210.project to javafx.fxml;
    exports com.cs210.project;
    
    // Required so TableView can reflectively access Model properties
    opens com.cs210.project.models to javafx.base;
    exports com.cs210.project.models;
}
