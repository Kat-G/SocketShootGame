package com.example.socketshootgame.connect.controllers;

import com.example.socketshootgame.objects.Point;

import java.util.ArrayList;

public class TargetsController {
    private ArrayList<Point> targets = new ArrayList<>(); //массив мишеней
    //Point big, small;
    private int big_speed = 5;
    private int small_speed = 10;

    public TargetsController(){   }

    public void init(){
        targets.add(new Point(400,280, 50));
        targets.add(new Point(500,280, 25));
    }
    public ArrayList<Point> getTargets() {
        return targets;
    }

    public void setTargets(ArrayList<Point> targetArrayList){
        this.targets = targetArrayList;
    }

    public void move(){
        Point big = targets.get(0);
        Point small = targets.get(1);

        if (small.getY() <= small.getR() || 450 - small.getY()  <= small.getR()) {
            small_speed = -1 * small_speed;
        }
        small.setY(small.getY() + small_speed);
        if (big.getY() <= big.getR() || 450 - big.getY()  <= big.getR()) {
            big_speed = -1 * big_speed;
        }
        big.setY(big.getY() + big_speed);
    }

    public void reset(){
        targets.clear();
    }

    public boolean containsBig(double x, double y) {
        return (Math.sqrt(Math.pow((x - targets.get(0).getX()), 2) + Math.pow((y -targets.get(0).getY()), 2)) < targets.get(0).getR()) ;
    }
    public boolean containsSmall(double x, double y) {
        return (Math.sqrt(Math.pow((x - targets.get(1).getX()), 2) + Math.pow((y -targets.get(1).getY()), 2)) < targets.get(1).getR()) ;
    }

}
