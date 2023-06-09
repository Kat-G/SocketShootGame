package com.example.socketshootgame;

import com.example.socketshootgame.connect.model.Model;
import com.example.socketshootgame.connect.model.ModelBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StartApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ClientFrame.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Scene scene = new Scene(root1, 900, 550);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setResizable(true);
        stage.setTitle("SocketShootGame");
        stage.setScene(scene);
        stage.show();

        GameFrame clientFrame = fxmlLoader.getController();

    }

    public static void main(String[] args) {
        launch();
    }
}