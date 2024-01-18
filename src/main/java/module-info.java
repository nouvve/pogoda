module com.example.weather {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens com.example.weather to javafx.fxml;
    exports com.example.weather;
}