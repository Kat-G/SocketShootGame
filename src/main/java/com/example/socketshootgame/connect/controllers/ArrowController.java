package com.example.socketshootgame.connect.controllers;

import com.example.socketshootgame.connect.Player;
import com.example.socketshootgame.connect.ShootState;
import com.example.socketshootgame.objects.Point;

import java.util.ArrayList;

public class ArrowController {
    private ArrayList<Point> arrows = new ArrayList<>(); //массив стрел
    private int arr_speed = 7;

    public ArrowController() { }

    public void tryShoot(int index, Player player, TargetsController targets){
        Point p = arrows.get(index); //получаем координату стрелы
        p.setX(p.getX() + arr_speed); //изменяем координату Х стрелы
        //takeShoot(p, player); //проверка на выстрел

        ShootState shootState = checkHit(p, targets); //проверка на попадание
        switch (shootState) {
            case BIG_SHOT: { player.addPoints(1); break;}
            case SMALL_SHOT: { player.addPoints(2); break; }
            case FLY: { return; }
        }
        p.setX(0); //возврат стрелы
        //очистка списка
        player.setShooting();
    }

    private synchronized ShootState checkHit(Point p, TargetsController targets) {

        if (targets.containsBig(p.getX() + p.getR(), p.getY())){
            return ShootState.BIG_SHOT;
        }

        if (targets.containsSmall(p.getX() + p.getR(), p.getY())){
            return ShootState.SMALL_SHOT;
        }
        if (p.getX() > 450) {
            return ShootState.MISSED;
        }
        return ShootState.FLY;
    }

    public void addArrow(Point p){
        arrows.add(p);
    }
    public void reset(){
        arrows.clear();
    }
    public ArrayList<Point> getArrows() {
        return arrows;
    }

    public void setArrows(ArrayList<Point> arrows) {
        this.arrows = arrows;
    }
}
