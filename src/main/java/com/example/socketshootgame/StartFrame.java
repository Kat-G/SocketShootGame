package com.example.socketshootgame;

import com.example.socketshootgame.connect.Model;
import com.example.socketshootgame.connect.ModelBuilder;
import com.example.socketshootgame.resp.Request;
import com.example.socketshootgame.resp.Sender;
import com.example.socketshootgame.resp.ServReactions;
import com.example.socketshootgame.resp.Response;
import com.google.gson.Gson;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class StartFrame {
    Socket socket;
    int port = 3124;
    InetAddress ip = null;

    InputStream is;
    OutputStream os;
    DataInputStream dis;
    DataOutputStream dos;
    Gson gson = new Gson();
    @FXML
    TextField nameField;

    Model m = ModelBuilder.build();
    public void onConnect(MouseEvent mouseEvent) {
        try {
            ip = InetAddress.getLocalHost();
            socket = new Socket(ip, port);

            Sender sender = new Sender(socket);
            sender.sendRequest(new Request(nameField.getText()));
            Request msg = sender.getRequest();

            if (msg.getServReactions() == ServReactions.Accept){
                new Thread(
                        ()->
                        {
                            try {
                                while (true) {
                                    Response ra = sender.getResp();
                                    m.setTargets(ra.targets);
                                    m.setClients(ra.clients);
                                    m.setArrows(ra.arrows);
                                    m.setWinner(ra.winner);
                                    m.update();
                                }

                            } catch (IOException ex) {

                            }

                        }
                ).start();
                openGamePage(mouseEvent);
            } else {
                alertError(msg.getServReactions());
                nameField.setText("");
            }

        } catch (IOException ignored) {

        }

    }

    private void alertError(ServReactions reaction){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка");
        switch (reaction)
        {
            case MaxConnectError:
            {
                alert.setContentText("Превышено допустимое количество игроков");
                break;
            }
            case DuplicateNameError:
            {
                alert.setContentText("Данное имя уже занято");
                break;
            }
        }
        alert.showAndWait();
    }

    private void openGamePage(Event event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ClientFrame.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Scene scene = new Scene(root1, 900, 550);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            Stage stage = new Stage();
            stage.setResizable(true);
            stage.setTitle("SocketShootGame");
            stage.setScene(scene);
            stage.show();
            ((Node)(event.getSource())).getScene().getWindow().hide();

            GameFrame clientFrame = fxmlLoader.getController();
            clientFrame.dataInit(socket, nameField.getText());
            m.update();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
