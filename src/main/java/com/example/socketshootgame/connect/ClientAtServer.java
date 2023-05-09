package com.example.socketshootgame.connect;

import com.example.socketshootgame.resp.ClientActions;
import com.example.socketshootgame.resp.ClientReqToServer;
import com.example.socketshootgame.resp.ServerRespToClient;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class ClientAtServer implements Runnable{
    Socket socket;
    Server server;
    InputStream inputStream;
    OutputStream outputStream;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    Gson gson = new Gson();
    Model model = ModelBuilder.build();
    PlayerInfo clientData;

    public ClientAtServer(Socket socket, Server server, String playerName)  {
        this.socket = socket;
        this.server = server;
        clientData = new PlayerInfo(playerName);
        try {
            outputStream = socket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
        } catch (IOException ignored) { }
    }
    public String getPlayerName() {
        return clientData.getPlayerName();
    }

    public void sendInfoToClient() {
        try {
            ServerRespToClient serverResp = new ServerRespToClient();
            serverResp.clients = model.getClients();
            serverResp.arrows = model.getArrows();
            serverResp.targets = model.getTargets();
            serverResp.winner = model.getWinner();

            String s = gson.toJson(serverResp);
            dataOutputStream.writeUTF(s);
        } catch (IOException ex) {
        }
    }



    @Override
    public void run() {
        try {
            inputStream = socket.getInputStream();
            dataInputStream = new DataInputStream(inputStream);

            System.out.println("Cilent thread " + clientData.getPlayerName() + " started");

            model.addClient(clientData);
            server.bcast();

            while(true)
            {
                String s = dataInputStream.readUTF();

                ClientReqToServer msg = gson.fromJson(s, ClientReqToServer.class);

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
