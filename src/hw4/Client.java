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

        NodeUtility utility = null;
        try {
            utility = new NodeUtility(3, "myClient", new InetSocketAddress(InetAddress.getLocalHost(), hw4.Protocol.initPort), null, null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        };
        int n = 9;
        for(int i = 1; i < n; i++) {
            try {
                Thread.sleep(100);
                new NodeUtility(3, "myClient" + i, new InetSocketAddress(InetAddress.getLocalHost(), hw4.Protocol.initPort + i), new InetSocketAddress(InetAddress.getLocalHost(),hw4.Protocol.initPort), "myClient");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        utility.sendBroadcast("1");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        utility.sendBroadcast("2");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        utility.sendBroadcast("3");

        InetSocketAddress test = utility.performLookup("myClient3");
        System.out.println("result of lookup: " + test);
    }

}
