package hw2.multithreadedClientServer;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Server {

    private static List<String> knownUsers = new ArrayList<String>(){{
        add("hurz");
        add("iceman");
        add("cholibri");
    }};

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(Protocol.SERVER_PORT);
            System.out.println("server is ready");
            boolean listening = true;

            while(listening) {

                new MultiServerThread(serverSocket.accept()).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Server shutting down");
    }


}
