module com.example.socketshootgame {
    requires javafx.controls;
    requires javafx.fxml;

    requires javafx.graphics;
    requires javafx.base;
    requires com.google.gson;

    requires java.naming;
    requires java.sql;
    requires java.persistence;
    requires org.hibernate.orm.core;
    requires org.xerial.sqlitejdbc;
    requires jakarta.persistence;

    opens com.example.socketshootgame to javafx.fxml;
    exports com.example.socketshootgame;
    exports com.example.socketshootgame.connect;
    exports com.example.socketshootgame.objects;
    opens com.example.socketshootgame.connect to javafx.fxml, com.google.gson;
    exports com.example.socketshootgame.resp;
    opens com.example.socketshootgame.resp to com.google.gson, javafx.fxml;
    opens com.example.socketshootgame.objects to com.google.gson;
    exports com.example.socketshootgame.connect.controllers;
    opens com.example.socketshootgame.connect.controllers to com.google.gson, javafx.fxml;
    exports com.example.socketshootgame.connect.model;
    opens com.example.socketshootgame.connect.model to com.google.gson, javafx.fxml;

}