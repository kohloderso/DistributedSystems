package hw3;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;


public class Server {

    public static void main(String[] args) {
        boolean running = true;
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("server is ready");

            // make this service announcable
            ServiceAnnouncer announcer = new ServiceAnnouncer(serverSocket.getLocalPort(), "myService", InetAddress.getLocalHost());
            new Thread(announcer).start();
            while(running) {

                Socket server = serverSocket.accept();
                SocketAddress clientAdd = server.getRemoteSocketAddress();
                System.out.println("accepted client " + clientAdd);


                // get the request
                JSONObject request = Protocol.receiveJSON(server);
                System.out.println("got message: " + request);

                if(request.containsKey("shutdown")) running = false;

                server.close();
                System.out.println("server closed connection to " + clientAdd);
            }
            System.out.println("Server shutting down");
            announcer.shutdown();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
