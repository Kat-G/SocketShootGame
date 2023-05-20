package com.example.socketshootgame.connect;

import com.example.socketshootgame.connect.model.Model;
import com.example.socketshootgame.connect.model.ModelBuilder;
import com.example.socketshootgame.resp.Request;
import com.example.socketshootgame.resp.Sender;
import com.example.socketshootgame.resp.Response;

import java.io.*;
import java.net.Socket;

public class ClientAtServer implements Runnable{
    Socket socket;
    Server server;
    Sender sender;
    Model model = ModelBuilder.build();
    Player player;

    public ClientAtServer(Socket socket, Server server, String playerName)  {
        this.socket = socket;
        this.server = server;
        player = new Player(playerName);
        sender = new Sender(socket);
    }
    public String getPlayerName() {
        return player.getPlayerName();
    }

    public void sendInfoToClient() {
        Response serverResp = new Response();
        serverResp.clients = model.getClients();
        serverResp.arrows = model.getArrows();
        serverResp.targets = model.getTargets();
        serverResp.winner = model.getWinner();
        serverResp.winners = model.getWinners();
        sender.sendResp(serverResp);
    }

    @Override
    public void run() {
        try {
            System.out.println("Cilent thread " + player.getPlayerName() + " started");
            model.addClient(player);
            server.bcast();

            while(true)
            {
                Request msg = sender.getRequest();

                switch (msg.getClientActions()){
                    case STOP: {  model.pause(this.getPlayerName()); break; }
                    case READY: { model.ready(server, this.getPlayerName()); break; }
                    case SHOOT: { model.shoot(getPlayerName()); break;}
                    case SCORE_TABLE: { model.updateScoreTable(); break; }

                }
            }
        } catch (IOException ignored) {

        }
    }
    public Player getPlayer() {
        return player;
    }

}
