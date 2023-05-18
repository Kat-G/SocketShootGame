package com.example.socketshootgame.connect.model;

import com.example.socketshootgame.connect.IObserver;
import com.example.socketshootgame.connect.Player;
import com.example.socketshootgame.connect.Server;
import com.example.socketshootgame.connect.controllers.ArrowController;
import com.example.socketshootgame.connect.controllers.PlayersController;
import com.example.socketshootgame.connect.controllers.TargetsController;
import com.example.socketshootgame.connect.controllers.WinnersController;
import com.example.socketshootgame.connect.database.jdbs.DataBase;
import com.example.socketshootgame.objects.Point;

import java.util.ArrayList;

public class Model {

    private final ArrayList<IObserver> observers = new ArrayList<>(); //массив обозревателей
    private final PlayersController players = new PlayersController();
    private final TargetsController targets = new TargetsController();
    private final ArrowController arrows = new ArrowController();
    private final WinnersController winners = new WinnersController();
    //private ArrayList<Player> entitiesList = new ArrayList<>();
    int ready;
    int pause;
    private String winner = null;
    private boolean Reset = true;
    private Server s;

    DataBase db;

    public void update() //обновление наблюдателей
    {
        for (IObserver o : observers) {
            o.update();
        }
    }

    public void updateScoreTable() {
        //entitiesList = db.getAllPlayers();
        winners.setWinners(db.getAllPlayers());
        s.bcast();
    }

    // Начальная инициализация
    public void init(Server s, DataBase dataBase) {
        this.db = dataBase;
        targets.init();
        arrowsCountUpdate();
        this.s = s;
    }

    public void init() {
        targets.init();
        arrowsCountUpdate();
    }

    // Добавление клиентам стрел
    private synchronized void arrowsCountUpdate() {
        arrows.reset();

        int clientsCount = players.getSize();
        for (int i = 1; i <= clientsCount; i++) {
            int step = 450 / (clientsCount + 1);
            arrows.addArrow(new Point(0, step * i, 100));
        }
    }

    public void ready(Server s, String name) {
        ready = players.getReadySize(name);

        if (ready == players.getSize()) {
            Reset = false;
            start(s);
        }
    }

    // Запрос на паузу
    public void pause(String name) {
        pause = players.getPauseSize(name);

        if (pause == 0){ //если список пуст
            synchronized(this) {
                notifyAll();         //пробуждаем потоки
            }
        }
    }

    // Запрос на выстрел
    public void shoot(String name) {
        players.shoot(name);
    }

    //запуск игры
    public void start(Server s) {
        new Thread(
                ()->
                {
                    while (true) {
                        if (Reset) {
                            winner = null;
                            break;
                        }
                        if (pause != 0) {  //если список паузы не пуст
                            synchronized(this) {
                                try {
                                    wait();             //ожидаем
                                } catch(InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        for (Player player: players.getPlayers()) {
                            if (player.isShooting()){
                                int index = players.getPlayers().indexOf(player);
                                arrows.tryShoot(index,player,targets);
                                checkWinner();
                            }
                        }
                        //передвижение мишеней
                        targets.move();

                        s.bcast(); //отправка данных с сервера на клиенты

                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
        ).start(); //запуск потока

    }

    private void restart() {
        Reset = true;;
        targets.reset();
        arrows.reset();
        players.reset();
        //this.init();
        this.init(s,db);
    }

    private synchronized void checkWinner() {
        for (Player player : players.getPlayers() ){
            int points_to_win = 2;
            if (player.getPoints() >= points_to_win){
                this.winner = player.getPlayerName();
                restart();
                /*
                Player p = entitiesList.stream()
                        .filter(entity -> entity.getPlayerName().equals(winner))
                        .findFirst()
                        .orElse(null);
                assert p != null;*/
                Player p = winners.findWinner(winner);
                p.setWins(p.getWins() + 1);

                //db.addPlayer(new PlayersTable(player.getPlayerName(),player.getWins()));

                db.setPlayerWins(p);
            }
        }
    }


    public String getWinner() {
        return winner;
    }
    public void setWinner(String winner) {
        this.winner = winner;
    }
    public void addClient(Player player) {
        db.addPlayer(player);
        player.setWins(db.getPlayerWins(player));
        winners.addWinner(player);
        //entitiesList.add(player);
        players.addPlayer(player);
        this.arrowsCountUpdate();
    }
    public  void addObserver(IObserver o)
    {
        observers.add(o);
    }

    public ArrayList<Player> getClients() {
        return players.getPlayers();
    }

    public void setClients(ArrayList<Player> clientArrayList) {
        this.players.setPlayers(clientArrayList);
    }


    public ArrayList<Point> getTargets() {
        return targets.getTargets();
    }

    public void setTargets(ArrayList<Point> targetArrayList) {
        this.targets.setTargets(targetArrayList);
    }

    public ArrayList<Point> getArrows() {
        return arrows.getArrows();
    }

    public void setArrows(ArrayList<Point> arrowArrayList) {
        this.arrows.setArrows(arrowArrayList);
    }

    public ArrayList<Player> getWinners() {
        //return entitiesList;
        return winners.getWinners();
    }

    public void setEntitiesList(ArrayList<Player> entitiesList) {
        winners.setWinners(entitiesList);
        //this.entitiesList = entitiesList;
    }

}
