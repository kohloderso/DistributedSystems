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

            while(true) {

                Socket server = serverSocket.accept();
                System.out.println("accepted client " + server.getRemoteSocketAddress());


                boolean authenticated = false;
                JSONObject message = Protocol.receiveJSON(server);
                String username = (String) message.get("username");
                if(knownUsers.contains(username)) {
                    authenticated = true;
                }
                System.out.println("authentication: " + authenticated);
                JSONObject answer = new JSONObject();
                answer.put("authenticated", authenticated);
                Protocol.sendJSON(answer, server);

                if(authenticated) {
                    // get the request from the authenticated client
                    int result = Protocol.processRequest(server);
                    System.out.println("processed result: " + result);

                    Protocol.sendResult(server, result);
                    System.out.println("sent result to client " + server.getRemoteSocketAddress());
                }

                server.close();
                System.out.println("server closed connection");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
