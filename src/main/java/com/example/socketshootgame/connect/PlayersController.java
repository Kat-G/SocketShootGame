package com.example.socketshootgame.connect;

import java.util.ArrayList;

public class PlayersController {
    private ArrayList<Player> players = new ArrayList<>(); //массив клентов

    public PlayersController() { }

    public void addPlayer(Player clientData){
        players.add(clientData);
    }
    public Player findPlayer(String name){
        var player = players.stream()
                .filter(clientData -> clientData.getPlayerName().equals(name))
                .findFirst()
                .orElse(null); //получаем клиента
        assert player != null;
        return player;
    }

    public int getReadySize(String name){
        int ready = 0;
        var player = findPlayer(name);
        player.setReady();
        for (Player p : players )
        {
            if (p.isReady()){
                ready++;
            }
        }
        return ready;
    }

    public int getPauseSize(String name){
        int pause = players.size();
        var player = findPlayer(name);
        player.setOnPause();
        for (Player p : players )
        {
            if (!p.isOnPause()){
                pause--;
            }
        }
        return pause;
    }

    public void reset(){
        players.forEach(Player::reset);
    }

    public void shoot(String name){
        var player = findPlayer(name);
        player.setShooting();
    }

    public int getSize(){
        return players.size();
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }
}
