package hw4;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by christina on 14.04.16.
 */
public class NodeUtility {
    private AtomicInteger messageIDs = new AtomicInteger(0);
    private String myName;
    private InetSocketAddress myAddress;
    private NodeTable table;
    private boolean listening = true;
    private Thread requestThread = null;
    private Thread listenerThread = null;
    private ConcurrentHashMap<String, Set<Integer>> receivedMessages = new ConcurrentHashMap<>();

    public NodeUtility(int n, String name, InetSocketAddress myAddress, InetSocketAddress initAddress, String initName) {
        this.myName = name;
        this.myAddress = myAddress;

        table = new NodeTable(n, name, initName, initAddress);

        /*listener thread accepts requests from other peers*/
        listenerThread = (new Thread(new ListenerThread()));
        listenerThread.start();

        /*start request thread which regularly (every 5 seconds) updates the node table */
        requestThread = (new Thread(new RequestThread()));
        requestThread.start();
    }

    public void sendBroadcast(String message) {
        System.out.println(myName + " sending broadcast: " + message);
        int id = messageIDs.getAndIncrement();
        for(Map.Entry<String, InetSocketAddress> entry: table.getEntries()) {
            try {
                Socket socket = new Socket(entry.getValue().getAddress(), entry.getValue().getPort());
                Protocol.sendBroadcastMessage(socket, message, myName, id);
            } catch (IOException e) {
                System.out.println(myName + " deleting " + entry.getKey());
                table.delete(entry.getKey());
            }
        }
    }

    private void sendBroadcast(JSONObject broadcast) {
        for(Map.Entry<String, InetSocketAddress> entry: table.getEntries()) {
            try {
                Socket socket = new Socket(entry.getValue().getAddress(), entry.getValue().getPort());
                Protocol.sendJSON(broadcast, socket);
            } catch (IOException e) {
                System.out.println(myName + " deleting " + entry.getKey());
                table.delete(entry.getKey());
            }
        }
    }


    /**
     * uses an ExecutorService to handle all incoming messages
     */
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


    /**
     * sends the current table to a random node every 5 seconds
     */
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
                    JSONObject packet = Protocol.makeJSONObject(table, myAddress, myName);
                    Protocol.sendJSON(packet, connectionSocket);

                    // wait for response TODO timeout
                    JSONObject message = Protocol.receiveJSON(connectionSocket);
                    if(message.containsKey("Addresses")) {
                        HashMap<String, InetSocketAddress> newEntries = Protocol.parseAddresses(message);
                        //System.out.println("received: " + newEntries);

                        table.merge(newEntries);
                    }

                    Thread.sleep(5000);

                } catch (IOException e) {
                    System.out.println(myName + " deleting " + randAddress);
                    table.delete(randNode);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }


    }


    /**
     * Handles all incoming messages like address tables and broadcasts
     */
    class RequestHandler implements Runnable {

        private Socket clientSocket = null;

        public RequestHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            JSONObject message = Protocol.receiveJSON(clientSocket);
            if(message.containsKey("Addresses")) {
               handleAddresses(message);
            } else if(message.containsKey("Broadcast")) {
                handleBroadcast(message);
            } else {
                System.out.println("Don't know what to do with message: " + message);
            }
        }

        public void handleAddresses(JSONObject addressObject) {
            // send own table back
            JSONObject packet = Protocol.makeJSONObject(table, myAddress, myName);
            Protocol.sendJSON(packet, clientSocket);

            // merge new entries with existing table
            HashMap<String, InetSocketAddress> newEntries = Protocol.parseAddresses(addressObject);
            table.merge(newEntries);
        }

        public void handleBroadcast(JSONObject messageObject) {
            //System.out.println(messageObject);
            int id = ((Long)messageObject.get("ID")).intValue();
            String sender = (String)messageObject.get("Sender");

            // if the original sender is this node itself immediately return
            if(sender.equals(myName)) return;

            String messageString = (String)messageObject.get("Broadcast");
            Set<Integer> ids = receivedMessages.get(sender);
            if(ids == null) {
                System.out.println(myName + " received new broadcast: " + messageString);
                // add message to collection
                ids = new ConcurrentSkipListSet<Integer>();
                ids.add(id);
                receivedMessages.put(sender, ids);
                sendBroadcast(messageObject);
            } else if(!ids.contains(id)) {
                System.out.println(myName + " received new broadcast: " + messageString);
                ids.add(id);
                sendBroadcast(messageObject);
            } else if(ids.contains(id)) return;
            // wait a long time, expect message to propagate through network
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // delete message, because it will no longer be needed
            ids.remove(id);
        }

    }

}
