package com.example.socketshootgame.connect;

import com.example.socketshootgame.resp.SocketMsg;

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
    ArrayList<ClientAtServer> allClients = new ArrayList<>(); //массив клиентов
    SocketMsg socketMsg;

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
            model.init();

            while(true)
            {
                Socket cs;
                cs = ss.accept();
                socketMsg = new SocketMsg(cs);
                String respName = socketMsg.getMessage();

                if (tryAddClient(cs, respName)) { //попытка подключения клиента
                    System.out.println(respName + " Connected");
                } else {
                    cs.close(); //если нет, закрываем сокет
                }
            }

        } catch (IOException ignored) {}
    }

    private boolean tryAddClient(Socket sock, String name) {
         if (allClients.size() >= 4) {
             socketMsg.sendMessage("MaxConnectError");
             return false;
         }
         if (allClients.isEmpty() || //если список клиентов пуст или не содержит такого же имени
                 allClients.stream()
                .filter(clientAtServer -> clientAtServer.getPlayerName().equals(name))
                .findFirst()
                .orElse(null) == null) {
             socketMsg.sendMessage("ACCEPT"); //отправка сообщения об успехе
             ClientAtServer c = new ClientAtServer(sock, this, name); //создание нового клиента
             allClients.add(c); //добавление в лист клиентов
             service.submit(c); //сообщаем серверу о подключении клиента
             return true;
         }
        socketMsg.sendMessage("DuplicateNameError");
        return false;
    }



    public static void main(String[] args) {
        new Server().serverStart();
    }

}
