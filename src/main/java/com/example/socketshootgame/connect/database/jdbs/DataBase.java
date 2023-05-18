package com.example.socketshootgame.connect.database.jdbs;

import com.example.socketshootgame.connect.Player;
import com.example.socketshootgame.connect.model.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * TO DO!
 * При добавлении игрока считывать его с бд, если он уже существует, обновлять его выигрыши
 * Посмотреть что не так в отображении таблицы выигрышей
 */

public class DataBase {
    Connection c;
    private final String url = "jdbc:postgresql://localhost:5432/score_table";
    private final String user = "postgres";
    private final String password = "1234";

    public DataBase() {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void addPlayer(Player entity) {
        try {
            PreparedStatement pst =
                    c.prepareStatement("INSERT INTO players(name, wins) VALUES (?,?) ON CONFLICT (name) DO NOTHING");
            pst.setString(1, entity.getPlayerName());
            pst.setInt(2, entity.getWins());
            pst.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void setPlayerWins(Player entity) {
        try {
            PreparedStatement pst =
                    c.prepareStatement("UPDATE players SET wins = ? WHERE name = ?");
            pst.setInt(1, entity.getWins());
            pst.setString(2, entity.getPlayerName());
            pst.executeUpdate();


        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public int getPlayerWins(Player entity) {
        try {
            PreparedStatement pst = c.prepareStatement("select * from players WHERE name = ?");
            pst.setString(1, entity.getPlayerName());

            ResultSet r = pst.executeQuery();

            r.next();
            return (r.getInt("wins"));

        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public ArrayList<Player> getAllPlayers() {
        ArrayList<Player> res = new ArrayList<>();

        try {
            Statement st = c.createStatement();
            ResultSet r = st.executeQuery("select * from players ORDER BY wins DESC");

            while(r.next())
            {
                var client = new Player(r.getString("name"));
                client.setWins(r.getInt("wins"));
                res.add(client);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

        return res;
    }
}
