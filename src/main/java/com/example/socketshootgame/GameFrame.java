package com.example.socketshootgame;

import com.example.socketshootgame.connect.PlayerInfo;
import com.example.socketshootgame.connect.Model;
import com.example.socketshootgame.connect.ModelBuilder;
import com.example.socketshootgame.resp.ClientActions;
import com.example.socketshootgame.resp.ClientReqToServer;
import com.example.socketshootgame.connect.IObserver;
import com.example.socketshootgame.objects.Arrow;
import com.example.socketshootgame.objects.Point;
import com.example.socketshootgame.objects.Info;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class GameFrame implements IObserver {
    @FXML
    private VBox infoBox;
    @FXML
    private Pane gamePane;
    @FXML
    private VBox playersBox;
    ArrayList<Button> players = new ArrayList<>();
    ArrayList<VBox> playersInfo = new ArrayList<>();
    ArrayList<Arrow> arrows = new ArrayList<>();
    ArrayList<Circle> targets = new ArrayList<>();

    private String playerName;
    private Gson gson = new Gson();
    private Socket socket;
    private DataOutputStream dos;
    private OutputStream os;

    private Model m = ModelBuilder.build();

    public void initialize() {
        m.addObserver(this);
    }

    private void sendRequest(ClientReqToServer msg)
    {
        try {
            String s_msg = gson.toJson(msg);
            dos.writeUTF(s_msg);
        } catch (IOException ignored) { }
    }
    public void dataInit(Socket socket, String playersName) {
        this.socket = socket;
        this.playerName = playersName;
        try {
            os = socket.getOutputStream();
            dos = new DataOutputStream(os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onReady(MouseEvent mouseEvent) {
        sendRequest(new ClientReqToServer(ClientActions.READY));
    }

    public void onPause(MouseEvent mouseEvent) {
        sendRequest(new ClientReqToServer(ClientActions.STOP));
    }

    public void onShoot(MouseEvent mouseEvent) {
        sendRequest(new ClientReqToServer(ClientActions.SHOOT));
    }

    @Override
    public void update() {
        checkWinner();
        updateTargets(m.getTargets());
        updatePlayersInfo(m.getClients());
        updatePlayers(m.getClients());
        updateArrows(m.getArrows());
    }

    private void checkWinner() {
        if (m.getWinner() != null) {
            Platform.runLater(() -> {
                alertWinner();
            });

        }
    }

    private void alertWinner(){
        double x = gamePane.getScene().getWindow().getX();
        double y = gamePane.getScene().getWindow().getY();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setX(x);
        alert.setY(y);
        alert.setTitle("Игра окончена");
        alert.setHeaderText("Победитель найден");
        alert.setContentText("Победитель : " + ((m.getWinner()).equals(this.playerName) ? "Вы" : m.getWinner()) + "!");
        alert.showAndWait();
    }

    private void updateTargets(ArrayList<Point> a) {
        if (a == null || a.size() == 0) return;
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                for (int i = 0; i < a.size(); i++) {
                    if (i >= targets.size()) {
                        Circle c = new Circle(a.get(i).getX(), a.get(i).getY(), a.get(i).getR());
                        c.getStyleClass().add("targets");
                        targets.add(c);
                        gamePane.getChildren().add(c);
                    } else if (a.size() > targets.size()){
                        for (int j = 0; j < a.size() - targets.size(); j++) {
                            targets.remove(targets.size() - 1);
                            gamePane.getChildren().remove(
                                    gamePane.getChildren().size() - 1
                            );
                        }
                    }
                    else {
                        targets.get(i).setRadius(a.get(i).getR());
                        targets.get(i).setCenterX(a.get(i).getX());
                        targets.get(i).setCenterY(a.get(i).getY());
                    }
                }
            }
        });
    }
    private void updateArrows(ArrayList<Point> a) {
        if (a == null || a.size() == 0) return;
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                arrows.forEach(arrow -> gamePane.getChildren().remove(arrow));
                for (int i = 0; i < a.size(); i++) {

                    Arrow arr = new Arrow(a.get(i).getX(),a.get(i).getY(), a.get(i).getR());
                    arrows.add(arr);
                    gamePane.getChildren().add(arr);

                }
            }
        });

    }

    private void updatePlayersInfo(ArrayList<PlayerInfo> a) {
        if (a == null || a.size() == 0) return;
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                for (int i = 0; i < a.size(); i++) {
                    if (i >= players.size()) {
                        VBox vb = Info.createVbox(a.get(i));
                        playersInfo.add(vb);
                        infoBox.getChildren().add(vb);
                    } else {
                        Info.setPlayerName(playersInfo.get(i), a.get(i).getPlayerName());
                        Info.setShots(playersInfo.get(i), a.get(i).getShoot());
                        Info.setPoints(playersInfo.get(i), a.get(i).getPoints());
                    }
                }
            }
        });

    }

    private void updatePlayers(ArrayList<PlayerInfo> a) {
        if (a == null || a.size() == 0 || players.size() == a.size()) return;
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                for (int i = 0; i < a.size(); i++) {
                    if (i >= players.size()) {
                        Button b = new Button();
                        b.setPrefHeight(140);
                        b.setPrefWidth(140);

                        if (a.get(i).getPlayerName().equals(playerName)){
                            b.getStyleClass().add("player-client");
                            b.setText("Вы");
                        } else {
                            b.getStyleClass().add("player-connect");
                        }

                        players.add(b);
                        playersBox.getChildren().add(b);
                    }
                }
            }
        });


    }
}
