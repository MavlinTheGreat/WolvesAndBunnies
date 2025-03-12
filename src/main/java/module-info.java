module com.example.predatorsandpreys {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;
    requires javafx.media;

    opens com.example.predatorsandpreys to javafx.fxml;
    exports com.example.predatorsandpreys;
}