package com.example.socketshootgame.resp;


import com.example.socketshootgame.connect.Player;
import com.example.socketshootgame.objects.Point;


import java.util.ArrayList;

public class Response {
    public ArrayList<Player> clients;
    public ArrayList<Point> arrows;
    public ArrayList<Point> targets;

    public String winner;
}
