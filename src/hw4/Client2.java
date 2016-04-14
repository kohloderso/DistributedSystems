package hw4;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;


public class Client2 {

    public static void main(String[] args) {
        Client2 client = new Client2();
        try {
            NodeUtility utility = new NodeUtility(3, "myClient", new InetSocketAddress(InetAddress.getLocalHost(), hw4.Protocol.initPort+1), new InetSocketAddress(InetAddress.getLocalHost(), hw4.Protocol.initPort), "myClient");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        };
    }

}
