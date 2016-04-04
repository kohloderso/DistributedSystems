package hw2.multithreadedClientServer;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;


public class MultiServerThread extends Thread {
    private Socket server = null;

    private static List<String> knownUsers = new ArrayList<String>(){{
        add("hurz");
        add("iceman");
        add("cholibri");
    }};

    public MultiServerThread(Socket socket) {
        super("MultiServerThread");
        this.server = socket;
    }

    public void run() {
        SocketAddress clientAdd = server.getRemoteSocketAddress();
        System.out.println("accepted client " + clientAdd);

        boolean authenticated = false;
        JSONObject message = Protocol.receiveJSON(server);
        String username = (String) message.get("username");
        if (knownUsers.contains(username)) {
            authenticated = true;
        }
        System.out.println("authentication: " + authenticated);
        JSONObject answer = new JSONObject();
        answer.put("authenticated", authenticated);
        Protocol.sendJSON(answer, server);

        if (authenticated) {
            // get the request from the authenticated client
            int result = Protocol.processRequest(server);
            System.out.println("processed result: " + result);

            Protocol.sendResult(server, result);
            System.out.println("sent result to client " + server.getRemoteSocketAddress());
        }

        try {
            server.close();
            System.out.println("server closed connection to" + clientAdd);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
