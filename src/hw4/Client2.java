package hw4;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;


/**
 * initalize more nodes
 */
public class Client2 {

    public static void main(String[] args) {
        NodeUtility[] nodes = new NodeUtility[6];
        int n = 6;
        for(int i = 1; i < n; i++) {
            try {
                Thread.sleep(100);
                nodes[i] = new NodeUtility(3, "myClient" + i, new InetSocketAddress(InetAddress.getLocalHost(), hw4.Protocol.initPort + i), new InetSocketAddress(InetAddress.getLocalHost(),hw4.Protocol.initPort), "myClient");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // disconnect a node
        nodes[3].disconnect();
        nodes[4].disconnect();
    }

}
