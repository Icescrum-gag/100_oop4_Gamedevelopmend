package test;

import screen.Screen;
import server.Client;
import sql.Sqlsrcipt;

public class Clientosql {
// this is the client without the MYSQL database

    public static void main(String[] args) {
        String name = "";
        boolean online = true;
        Sqlsrcipt questions = new Sqlsrcipt();
        String ip = "";
        boolean first = true;
        Client spiel;

        while (true) {

            Screen frame = new Screen(first);
//        System.out.println(frame.isStart());

            frame.setName(name);
            frame.setIp(ip);
            frame.setOnline(online);

            while (!frame.isStart()) {
                name = frame.getName();
                online = frame.isOnline();
                ip = frame.getIp();
//            System.out.println(online + " | "+name);
            }

            System.out.println("Name: " + name);
            System.out.println("Online: " + online);
            System.out.println("IP: " + ip);

            frame.setVisible(false);
            frame.dispose();

            spiel = new Client(1000, 1540, online, name, ip);
            spiel.play();

//            System.out.println("Client has started");
            first = false;
        }
    }
}
