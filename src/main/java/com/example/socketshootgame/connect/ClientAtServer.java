package com.example.socketshootgame.connect;

import com.example.socketshootgame.resp.ClientActions;
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
    PlayerInfo clientData;

    public ClientAtServer(Socket socket, Server server, String playerName)  {
        this.socket = socket;
        this.server = server;
        clientData = new PlayerInfo(playerName);
        sender = new Sender(socket);
    }
    public String getPlayerName() {
        return clientData.getPlayerName();
    }

    public void sendInfoToClient() {
        Response serverResp = new Response();
        serverResp.clients = model.getClients();
        serverResp.arrows = model.getArrows();
        serverResp.targets = model.getTargets();
        serverResp.winner = model.getWinner();
        sender.sendResp(serverResp);
    }

    @Override
    public void run() {
        try {
            System.out.println("Cilent thread " + clientData.getPlayerName() + " started");
            model.addClient(clientData);
            server.bcast();

            while(true)
            {
                Request msg = sender.getRequest();

                if(msg.getClientActions() == ClientActions.READY)
                {
                   model.ready(server, this.getPlayerName());
                }

                if(msg.getClientActions() == ClientActions.STOP)
                {
                    model.pause(getPlayerName());
                }
                if (msg.getClientActions() == ClientActions.SHOOT) {
                    model.shoot(getPlayerName());
                }


            }
        } catch (IOException ignored) {

        }
    }
    public PlayerInfo getClientData() {
        return clientData;
    }

}
