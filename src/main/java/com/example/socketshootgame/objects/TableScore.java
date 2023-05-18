package com.example.socketshootgame.objects;

import com.example.socketshootgame.connect.Player;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableScore {
    TableView tableScore;
    public TableScore(){
        tableScore = new TableView<>();
    }

    public TableView create(){
        TableColumn<Player, String> nameColumn =
                new TableColumn<>("Имя");
        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("playerName"));
        nameColumn.setPrefWidth(150);

        TableColumn<Player, String> winsColumn =
                new TableColumn<>("Кол-во побед");
        winsColumn.setCellValueFactory(
                new PropertyValueFactory<>("wins"));
        winsColumn.setPrefWidth(150);

        tableScore.getColumns().add(nameColumn);
        tableScore.getColumns().add(winsColumn);

        return tableScore;
    }

}
