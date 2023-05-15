package com.example.socketshootgame.connect;

//информация о клиенте
public class Player {
    private String playerName;
    private int shoots = 0;
    private int points = 0;
    private int wins = 0;
    private boolean isReady = false;
    private boolean onPause = false;
    private boolean shooting = false;

    public Player(String playerName) {
        this.playerName = playerName;
    }

    public void increaseArrowsShoot(int a) {
        this.shoots += a;
    }

    public void reset() {
        shoots = 0;
        points = 0;
        isReady = false;
        onPause = false;
        shooting = false;
    }

    public void increasePoints(int a) {
        this.points += a;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getShoot() {
        return shoots;
    }

    public int getPoints() {
        return points;
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
        if (!shooting)
        {
            shooting = true;
            increaseArrowsShoot(1);
        }
        else
            shooting = false;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setShoots(int shoots) {
        this.shoots = shoots;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
