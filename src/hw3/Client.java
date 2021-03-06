package hw3;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class Client {

    public static void main(String[] args) {
        Client client = new Client();

        ServiceLocator locator = new ServiceLocator(8888, 5000);
        locator.locate();
        InetSocketAddress address = locator.getAddresses("myService").get(0);
        client.send(address, "hello there");
    }

    public void send(InetSocketAddress socketAddress, String message) {

        try {
            Socket clientSocket = new Socket(socketAddress.getAddress(), socketAddress.getPort());
            System.out.println("connected client to server " + clientSocket.getRemoteSocketAddress());

            Protocol.sendSimpleMessage(clientSocket, message);
            System.out.println("sent message: " + message);

            clientSocket.close();
            System.out.println("client closed connection");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
