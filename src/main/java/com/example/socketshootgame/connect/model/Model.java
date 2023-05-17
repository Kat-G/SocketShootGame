package com.example.socketshootgame.connect.model;

//import com.example.socketshootgame.connect.database.hibernate.IDataBase;
//import com.example.socketshootgame.connect.database.hibernate.PlayersTable;
import com.example.socketshootgame.connect.IObserver;
import com.example.socketshootgame.connect.Player;
import com.example.socketshootgame.connect.Server;
import com.example.socketshootgame.connect.ShootState;
import com.example.socketshootgame.connect.controllers.ArrowController;
import com.example.socketshootgame.connect.controllers.PlayersController;
import com.example.socketshootgame.connect.controllers.TargetsController;
import com.example.socketshootgame.connect.database.hibernate.IDataBase;
import com.example.socketshootgame.connect.database.hibernate.PlayersTable;
import com.example.socketshootgame.connect.database.jdbs.DataBase;
import com.example.socketshootgame.objects.Point;

import java.util.ArrayList;

public class Model {

    private final ArrayList<IObserver> observers = new ArrayList<>(); //массив обозревателей
    private PlayersController players = new PlayersController();
    private TargetsController targets = new TargetsController();
    private ArrowController arrows = new ArrowController();

    private ArrayList<Player> entitiesList = new ArrayList<>();
    int ready;
    int pause;
    private String winner = null;
    private static int points_to_win = 2;
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
        entitiesList = db.getAllPlayers();
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
                    int arr_speed = 7;
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
        for (Player p : players.getPlayers() ){
            if (p.getPoints() >= points_to_win){
                this.winner = p.getPlayerName();
                restart();
                p.setWins(p.getWins() + 1);
                db.setPlayerWins(p);

                /*
                PlayersTable pt = new PlayersTable();
                pt.setPlayerName(p.getPlayerName());
                pt.setWins(pt.getWins());
                db.setPlayerWins(pt);*/
            }
        }
    }


    public String getWinner() {
        return winner;
    }
    public void setWinner(String winner) {
        this.winner = winner;
    }
    public void addClient(Player clientData) {
        players.addPlayer(clientData);
        db.addPlayer(clientData);
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

    public ArrayList<Player> getEntitiesList() {
        return entitiesList;
    }

    public void setEntitiesList(ArrayList<Player> entitiesList) {
        this.entitiesList = entitiesList;
    }

}
