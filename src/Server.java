import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(Protocol.SERVER_PORT);
            System.out.println("server is ready");

            while(true) {

                Socket server = serverSocket.accept();
                System.out.println("accepted client " + server.getRemoteSocketAddress());

                int result = Protocol.processRequest(server);
                System.out.println("processed result: " + result);

                Protocol.sendResult(server, result);
                System.out.println("sent result to client " + server.getRemoteSocketAddress());

                server.close();
                System.out.println("server closed connection");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
