package hw9.sockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


public class Client {

    public int request(InetSocketAddress socketAddress, String operation, int... operands) {
        int result = 0;

        try {
            Socket clientSocket = new Socket(socketAddress.getAddress(), socketAddress.getPort());
            //System.out.println("connected client to server " + clientSocket.getRemoteSocketAddress());

            Protocol.request(clientSocket, operation, operands);
            //System.out.println("sent request");

            result = Protocol.getResult(clientSocket);
            //System.out.println("received result " + result);

            clientSocket.close();
            //System.out.println("client closed connection");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
