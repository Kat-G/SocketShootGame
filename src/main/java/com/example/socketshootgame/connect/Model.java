package com.example.socketshootgame.connect;

//import com.example.socketshootgame.hibernate.IDataBase;
//import com.example.socketshootgame.hibernate.PlayersTable;
import com.example.socketshootgame.objects.Point;

import java.util.ArrayList;

public class Model {

    private final ArrayList<IObserver> observers = new ArrayList<>(); //массив обозревателей
    private ArrayList<Player> players = new ArrayList<>(); //массив клентов
    //private ArrayList<Point> targets = new ArrayList<>(); //массив мишеней
    private TargetsController targets = new TargetsController();
    private ArrayList<Point> arrows = new ArrayList<>(); //массив стрел
    int ready;
    int pause;
    private String winner = null;
    private static int points_to_win = 2;
    private boolean Reset = true;

    //IDataBase db;

    public void update() //обновление наблюдателей
    {
        for (IObserver o : observers) {
            o.update();
        }
    }

    // Начальная инициализация
    /*
    public void init(IDataBase dataBase) {
        //this.db = dataBase;
        //targets.add(new Point(400,280, 50));
        //targets.add(new Point(500,280, 25));
        targets = new TargetsController();
        arrowsCountUpdate();
    }
    */

    public void init() {
        //targets.add(new Point(400,280, 50));
        //targets.add(new Point(500,280, 25));

        targets.init();
        arrowsCountUpdate();
    }

    // Добавление клиентам стрел
    private synchronized void arrowsCountUpdate() {
        arrows.clear();
        int clientsCount = players.size();
        for (int i = 1; i <= clientsCount; i++) {
            int step = 450 / (clientsCount + 1);
            arrows.add(new Point(0, step * i, 100));
        }
    }

    public Player findPlayer(String name){
        var player = players.stream()
                .filter(clientData -> clientData.getPlayerName().equals(name))
                .findFirst()
                .orElse(null); //получаем клиента
        assert player != null;
        return player;
    }

    public void ready(Server s, String name) {
        ready = 0;
        var player = findPlayer(name);
        player.setReady();
        for (Player p : players )
        {
            if (p.isReady()){
                ready++;
            }
        }
        if (ready == players.size()) {
            Reset = false;
            start(s);
        }
    }

    // Запрос на паузу
    public void pause(String name) {
        pause = players.size();
        var player = findPlayer(name);
        player.setOnPause();
        for (Player p : players )
        {
            if (!p.isOnPause()){
                pause--;
            }
        }
        if (pause == 0){ //если список пуст
            synchronized(this) {
                notifyAll();         //пробуждаем потоки
            }
        }
    }

    // Запрос на выстрел
    public void shoot(String name) {
        var player = findPlayer(name);
        player.setShooting();
    }

    //запуск игры
    public void start(Server s) {
        new Thread(
                ()->
                {
                    int big_speed = 5;
                    int small_speed = 10;
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
                        for (Player player: players) {
                            if (player.isShooting()){
                                int index = players.indexOf(player);
                                Point p = arrows.get(index); //получаем координату стрелы
                                p.setX(p.getX() + arr_speed); //изменяем координату Х стрелы
                                takeShoot(p, player); //проверка на выстрел
                            }
                        }
                        //передвижение мишеней
                        /*
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
                        */
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
        Reset = true;
        //targets.clear();
        targets.restart();

        arrows.clear();
        players.forEach(Player::reset);
        this.init();
        //this.init(db);
    }

    //проверка на выстрел
    private synchronized void takeShoot(Point p, Player player) {
        ShootState shootState = checkHit(p); //проверка на попадание
        switch (shootState) {
            case BIG_SHOT: { player.increasePoints(1); break;}
            case SMALL_SHOT: { player.increasePoints(2); break; }
            case FLY: { return; }
        }
        p.setX(0); //возврат стрелы
        //очистка списка
        player.setShooting();
        checkWinner();
    }

    //проверка на попадание
    private synchronized ShootState checkHit(Point p) {

        //if (contains(targets.get(0), p.getX() + p.getR(), p.getY())) {
        if (targets.containsBig(p.getX() + p.getR(), p.getY())){
            return ShootState.BIG_SHOT;
        }
        //if (contains(targets.get(1), p.getX() + p.getR(), p.getY())) {
        if (targets.containsSmall(p.getX() + p.getR(), p.getY())){
            return ShootState.SMALL_SHOT;
        }
        if (p.getX() > 450) {
            return ShootState.MISSED;
        }
        return ShootState.FLY;
    }

    //проверка на пересечение
    /*
    private boolean contains(Point c, double x, double y) {
        return (Math.sqrt(Math.pow((x -c.getX()), 2) + Math.pow((y -c.getY()), 2)) < c.getR()) ;
    }*/

    private synchronized void checkWinner() {
        /*
        players.forEach(clientDataManager -> {
            if (clientDataManager.getPoints() >= points_to_win) {
                this.winner = clientDataManager.getPlayerName();
                restart();
            }
        });*/
        for (Player p : players){
            if (p.getPoints() >= points_to_win){
                this.winner = p.getPlayerName();
                restart();
                p.setWins(p.getWins() + 1);

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
        players.add(clientData);
        this.arrowsCountUpdate();
    }
    public  void addObserver(IObserver o)
    {
        observers.add(o);
    }

    public ArrayList<Player> getClients() {
        return players;
    }

    public void setClients(ArrayList<Player> clientArrayList) {
        this.players = clientArrayList;
    }


    public ArrayList<Point> getTargets() {
        return targets.getTargets();
    }

    public void setTargets(ArrayList<Point> targetArrayList) {
        this.targets.setTargets(targetArrayList);
    }

    public ArrayList<Point> getArrows() {
        return arrows;
    }

    public void setArrows(ArrayList<Point> arrowArrayList) {
        this.arrows = arrowArrayList;
    }


}
