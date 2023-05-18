package com.example.socketshootgame.connect.controllers;

import com.example.socketshootgame.connect.Player;

import java.util.ArrayList;

public class WinnersController {
    private ArrayList<Player> winners = new ArrayList<Player>();

    public WinnersController() { }

    public ArrayList<Player> getWinners() {
        return winners;
    }

    public void setWinners(ArrayList<Player> winners) {
        this.winners = winners;
    }

    public void addWinner(Player player){
        winners.add(player);
    }
    public Player findWinner(String name){
        var player = winners.stream()
                .filter(clientData -> clientData.getPlayerName().equals(name))
                .findFirst()
                .orElse(null); //получаем клиента
        assert player != null;
        return player;
    }
}
