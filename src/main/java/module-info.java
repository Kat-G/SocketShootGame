module com.example.javashooter {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;

    opens com.example.socketshootgame to javafx.fxml;
    exports com.example.socketshootgame;
    exports com.example.socketshootgame.connect;
    exports com.example.socketshootgame.objects;
    opens com.example.socketshootgame.connect to javafx.fxml, com.google.gson;
    exports com.example.socketshootgame.resp;
    opens com.example.socketshootgame.resp to com.google.gson, javafx.fxml;
    opens com.example.socketshootgame.objects to com.google.gson;

}