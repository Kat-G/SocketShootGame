package com.example.socketshootgame.resp;

public class ClientReqToServer {
    ClientActions clientActions;
    public ClientReqToServer(ClientActions clientActions) {
        this.clientActions = clientActions;
    }
    public ClientActions getClientActions() {
        return clientActions;
    }
}
