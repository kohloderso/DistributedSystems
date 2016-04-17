package hw4;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by christina on 14.04.16.
 */
public class NodeUtility {
    private int n;
    private String nodeName;
    private InetSocketAddress myAddress;
    private NodeTable table;
    private boolean listening = true;
    private Thread requestThread = null;
    private Thread listenerThread = null;

    public NodeUtility(int n, String name, InetSocketAddress myAddress, InetSocketAddress initAddress, String initName) {
        this.n = n;
        this.nodeName = name;
        this.myAddress = myAddress;

        table = new NodeTable(n, name, initName, initAddress);

        /*listener thread accepts requests from other peers*/
        listenerThread = (new Thread(new ListenerThread()));
        listenerThread.start();

        /*start request thread which regularly (every 5 seconds) updates the node table */
        requestThread = (new Thread(new RequestThread()));
        requestThread.start();
    }


    private class ListenerThread implements Runnable {

        private ServerSocket p2pServerSocket = null;
        public final int threadPoolSize = 10;
        private ExecutorService service;

        @Override
        public void run() {

            service = Executors.newFixedThreadPool(threadPoolSize);

            try {
                p2pServerSocket = new ServerSocket(myAddress.getPort());
                p2pServerSocket.setSoTimeout(1000);
                while (listening) {
                    try {
                        service.execute(new RequestHandler(p2pServerSocket.accept()));
                    } catch (SocketTimeoutException e) {
                        //TODO check listening variable
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    p2pServerSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                service.shutdown();
            }

        }

    }


    private class RequestThread implements Runnable {

        private Socket connectionSocket = null;

        @Override
        public void run() {
            while(listening) {
                String randNode = table.getRandomName();
                if(randNode == null) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                InetSocketAddress randAddress = table.getAddress(randNode);
                try {
                    connectionSocket = new Socket(randAddress.getHostName(), randAddress.getPort());
                    JSONObject packet = Protocol.makeJSONObject(table, myAddress, nodeName);
                    Protocol.sendJSON(packet, connectionSocket);
                    System.out.println("sent: " + packet);

                    // wait for response TODO timeout
                    HashMap<String, InetSocketAddress> newEntries = Protocol.receiveAndParse(connectionSocket);
                    System.out.println("response: " + newEntries);

                    table.merge(newEntries);

                    Thread.sleep(5000);

                } catch (IOException e) {
                    System.out.println("Deleting " + randAddress);
                    table.delete(randNode);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            }


    }


    class RequestHandler implements Runnable {

        private Socket clientSocket = null;

        public RequestHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            HashMap<String, InetSocketAddress> newEntries = Protocol.receiveAndParse(clientSocket);
            System.out.println("response: " + newEntries);
            table.merge(newEntries);

            JSONObject packet = Protocol.makeJSONObject(table, myAddress, nodeName);
            Protocol.sendJSON(packet, clientSocket);
            System.out.println("sent: " + packet);


        }

    }
}
