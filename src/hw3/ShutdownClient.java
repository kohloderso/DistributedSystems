package hw3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


public class ShutdownClient {

    public static void main(String[] args) {
        ShutdownClient client = new ShutdownClient();

        ServiceLocator locator = new ServiceLocator(8888, 5000);
        locator.locate();
        InetSocketAddress address = locator.getAddresses("myService").get(0);

        try {
            Socket clientSocket = new Socket(address.getAddress(), address.getPort());
            System.out.println("connected client to server " + clientSocket.getRemoteSocketAddress());

            Protocol.sendShutdownRequest(clientSocket);
            System.out.println("sent shutdown request");

            clientSocket.close();
            System.out.println("client closed connection");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
