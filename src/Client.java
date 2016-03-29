import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


public class Client {

    public static void main(String[] args) {
        Client client = new Client();
        client.request(new InetSocketAddress("localhost", Protocol.SERVER_PORT), "pfurz", "+", 5, 5);
    }

    public int request(InetSocketAddress socketAddress, String username, String operation, int... operands) {
        int result = 0;

        try {
            Socket clientSocket = new Socket(socketAddress.getAddress(), socketAddress.getPort());
            System.out.println("connected client to server " + clientSocket.getRemoteSocketAddress());

            Protocol.request(clientSocket, username, operation, operands);
            System.out.println("sent request");

            result = Protocol.getResult(clientSocket);
            System.out.println("received result " + result);

            clientSocket.close();
            System.out.println("client closed connection");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
