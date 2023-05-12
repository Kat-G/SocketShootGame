package com.example.socketshootgame.connect;

//информация о клиенте
public class Player {
    private String playerName;
    private int  arrowsShoot = 0;
    private int pointsEarned = 0;
    private boolean isReady = false;
    private boolean onPause = false;
    private boolean shooting = false;

    public Player(String playerName) {
        this.playerName = playerName;
    }

    public void increaseArrowsShoot(int a) {
        this.arrowsShoot += a;
    }

    public void reset() {
        arrowsShoot = 0;
        pointsEarned = 0;
        isReady = false;
        onPause = false;
        shooting = false;
    }

    public void increasePointsEarned(int a) {
        this.pointsEarned += a;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getShoot() {
        return arrowsShoot;
    }

    public int getPoints() {
        return pointsEarned;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady() {
        isReady = !isReady;
    }

    public boolean isOnPause() {
        return onPause;
    }

    public void setOnPause() {
        onPause = !onPause;
    }

    public boolean isShooting() {
        return shooting;
    }

    public void setShooting() {
        shooting = !shooting;
    }

    public void setArrowsShoot(int arrowsShoot) {
        this.arrowsShoot = arrowsShoot;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
