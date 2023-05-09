package com.example.socketshootgame.resp;


import com.example.socketshootgame.connect.PlayerInfo;
import com.example.socketshootgame.objects.Point;


import java.util.ArrayList;

public class ServerRespToClient {
    public ArrayList<PlayerInfo> clients;
    public ArrayList<Point> arrows;
    public ArrayList<Point> targets;

    public String winner;
}
