package com.example.socketshootgame.connect;

//import com.example.socketshootgame.connect.database.hibernate.DataBaseHibernate;
//import com.example.socketshootgame.connect.database.hibernate.IDataBase;
import com.example.socketshootgame.connect.database.hibernate.DataBaseHibernate;
import com.example.socketshootgame.connect.database.jdbs.DataBase;
import com.example.socketshootgame.connect.model.Model;
import com.example.socketshootgame.connect.model.ModelBuilder;
import com.example.socketshootgame.resp.Request;
import com.example.socketshootgame.resp.Sender;
import com.example.socketshootgame.resp.ServReactions;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.ServerSocket;

public class Server {
    int port = 3124;
    InetAddress ip = null;
    ExecutorService service = Executors.newFixedThreadPool(4); //ограничиваем кол-во клиентких потоков 4
    ArrayList<ClientAtServer> allClients = new ArrayList<>();

    Sender sender;
    Model model = ModelBuilder.build();



    public void bcast(){ //отправка данных на клиенты
        allClients.forEach(ClientAtServer::sendInfoToClient);
    }
    public void serverStart(){
        ServerSocket ss;
        try {
            ip = InetAddress.getLocalHost();
            ss = new ServerSocket(port, 2, ip);
            System.out.println("Server start\n");

            DataBase dataBase = new DataBase();
            model.init(this, dataBase);

            while(true)
            {
                Socket cs;
                cs = ss.accept();
                sender = new Sender(cs);
                Request msg = sender.getRequest();
                String respName = msg.getPlayerName();
                if (tryAddClient(cs, respName)) {
                    System.out.println(respName + " Connected");
                } else {
                    cs.close();
                }
            }

        } catch (IOException ignored) {}
    }

    private boolean tryAddClient(Socket sock, String name) {
         if (allClients.size() >= 4) {
             sender.sendRequest(new Request(ServReactions.MaxConnectError));
             return false;
         }
         if (allClients.isEmpty() ||
                 allClients.stream()
                .filter(clientAtServer -> clientAtServer.getPlayerName().equals(name))
                .findFirst()
                .orElse(null) == null) {
             sender.sendRequest(new Request(ServReactions.Accept));
             ClientAtServer c = new ClientAtServer(sock, this, name);
             allClients.add(c);
             service.submit(c);
             return true;
         }
        sender.sendRequest(new Request(ServReactions.DuplicateNameError));
        return false;
    }



    public static void main(String[] args) {
        new Server().serverStart();
    }

}
