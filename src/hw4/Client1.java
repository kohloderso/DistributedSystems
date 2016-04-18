package hw4;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;


/**
 * initial node
 */
public class Client1 {

    public static void main(String[] args) {
        Client1 client = new Client1();
        try {
            NodeUtility utility = new NodeUtility(3, "myClient", new InetSocketAddress(InetAddress.getLocalHost(), Protocol.initPort), null, null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        };
    }

}
