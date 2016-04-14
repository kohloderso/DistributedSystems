package hw4;

import hw3.Protocol;
import hw3.ServiceLocator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {

    public static void main(String[] args) {
        Client client = new Client();
        try {
            NodeUtility utility = new NodeUtility(3, "myClient", new InetSocketAddress(InetAddress.getLocalHost(), hw4.Protocol.initPort), null, null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        };
    }

}
