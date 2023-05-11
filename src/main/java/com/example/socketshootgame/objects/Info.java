package com.example.socketshootgame.objects;

import com.example.socketshootgame.connect.Player;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class Info {

    public static VBox createVbox(Player cd) {
        VBox vb = new VBox();
        vb.setAlignment(Pos.TOP_CENTER);
        vb.prefWidth(250);
        Label l; Text t;

        l = new Label("Имя игрока:"); l.getStyleClass().add("text-label");
        vb.getChildren().add(l);
        t = new Text(cd.getPlayerName()); t.getStyleClass().add("text");
        vb.getChildren().add(t);

        l = new Label("Выстрелы:"); l.getStyleClass().add("text-label");
        vb.getChildren().add(l);
        t = new Text(Integer.toString(cd.getShoot())); t.getStyleClass().add("text");
        vb.getChildren().add(t);

        l = new Label("Очки:"); l.getStyleClass().add("text-label");
        vb.getChildren().add(l);
        t = new Text(Integer.toString(cd.getPoints())); t.getStyleClass().add("text");
        vb.getChildren().add(t);

        return vb;
    }

    public static void setPlayerName (VBox vb, String s) {
        Text text = (Text) (((VBox) vb).getChildren().get(1));
        text.setText(s);
    }

    public static void setShots (VBox vb, int a) {
        Text text = (Text) ((vb).getChildren().get(3));
        text.setText(Integer.toString(a));
    }

    public static void setPoints (VBox vb, int a) {
        Text text = (Text) ((vb).getChildren().get(5));
        text.setText(Integer.toString(a));
    }


}
