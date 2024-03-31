module com.example.ls {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.ls to javafx.fxml;
    exports com.example.ls;
}