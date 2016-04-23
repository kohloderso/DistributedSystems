package hw2.multithreadedClientServer;

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

            // authenticate
            if(Protocol.authenticate(username, clientSocket)) {
                System.out.println("authentication successful");

                Protocol.request(clientSocket, operation, operands);
                System.out.println("sent request");

                result = Protocol.getResult(clientSocket);
                System.out.println("received result " + result);

            } else {
                System.out.println("authentication failure");
            }

            clientSocket.close();
            System.out.println("client closed connection");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
