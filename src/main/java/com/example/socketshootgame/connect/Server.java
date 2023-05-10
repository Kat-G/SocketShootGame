package com.example.socketshootgame.connect;

import com.example.socketshootgame.resp.Request;
import com.example.socketshootgame.resp.ServReactions;
import com.google.gson.Gson;

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
    private Gson gson = new Gson();
    private DataOutputStream dos;
    private OutputStream os;
    InputStream is;
    DataInputStream dis;
    Model model = ModelBuilder.build();

    private void sendRequest(Request msg)
    {
        try {
            String s_msg = gson.toJson(msg);
            dos.writeUTF(s_msg);
        } catch (IOException ignored) { }
    }

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

                os = cs.getOutputStream();
                dos = new DataOutputStream(os);
                is = cs.getInputStream();
                dis = new DataInputStream(is);

                String s = dis.readUTF();
                Request msg = gson.fromJson(s, Request.class);
                String respName = msg.getPlayerName();
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
             sendRequest(new Request(ServReactions.MaxConnectError));
             return false;
         }
         if (allClients.isEmpty() || //если список клиентов пуст или не содержит такого же имени
                 allClients.stream()
                .filter(clientAtServer -> clientAtServer.getPlayerName().equals(name))
                .findFirst()
                .orElse(null) == null) {
             sendRequest(new Request(ServReactions.Accept));
             ClientAtServer c = new ClientAtServer(sock, this, name); //создание нового клиента
             allClients.add(c); //добавление в лист клиентов
             service.submit(c); //сообщаем серверу о подключении клиента
             return true;
         }
        sendRequest(new Request(ServReactions.DuplicateNameError));
        return false;
    }



    public static void main(String[] args) {
        new Server().serverStart();
    }

}
