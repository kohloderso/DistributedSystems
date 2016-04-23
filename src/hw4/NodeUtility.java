package hw4;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class NodeUtility {
    private AtomicInteger messageIDs = new AtomicInteger(0);
    private String myName;
    private InetSocketAddress myAddress;
    private NodeTable table;
    private boolean listening = true;
    private Thread requestThread = null;
    private Thread listenerThread = null;
    private AtomicBoolean lookupInProgress = new AtomicBoolean(false);
    private String lookupName = null;
    private InetSocketAddress lookupAddress = null;
    private ConcurrentHashMap<String, Set<Integer>> receivedMessages = new ConcurrentHashMap<>();

    public NodeUtility(int n, String name, InetSocketAddress myAddress, InetSocketAddress initAddress, String initName) {
        this.myName = name;
        this.myAddress = myAddress;

        table = new NodeTable(n, name, initName, initAddress);

        // listener thread accepts requests from other peers, like broadcasts, lookup requests, new node tables
        listenerThread = (new Thread(new ListenerThread()));
        listenerThread.start();

        // start request thread which updates the node table every 5 seconds
        requestThread = (new Thread(new RequestThread()));
        requestThread.start();
    }

    public void disconnect() {
        listening = false;
    }

    /**
     *
     * @param nodeName
     * @return address of the node with this name, null if it couldn't be found after 2 seconds
     */
    public InetSocketAddress performLookup(String nodeName) {
        // check if it's this node
        if(nodeName.equals(myName)) return myAddress;

        // look into the table of this node
        InetSocketAddress address = table.getAddress(nodeName);
        if(address != null) return address;

        // send the request on to all other nodes
        lookupName = nodeName;
        lookupInProgress.set(true);
        lookupAddress = null;

        JSONObject lookupObject = new JSONObject();
        lookupObject.put("LookupRequest", true);
        lookupObject.put("NodeName", nodeName);
        // put the information in who wants to find the node
        lookupObject.put("Port", myAddress.getPort());
        lookupObject.put("Host", myAddress.getHostName());
        sendBroadcast(lookupObject);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lookupInProgress.set(false);
        return lookupAddress;   // hopefully some node responded and the address is now in this global variable
    }


    /**
     * put message into JSONObject with qualifier "Broadcast" to every node in the table
     * @param message
     */
    public void sendBroadcast(String message) {
        System.out.println(myName + " sending broadcast: " + message);
        int id = messageIDs.getAndIncrement();
        for(Map.Entry<String, InetSocketAddress> entry: table.getEntries()) {
            try {
                Socket socket = new Socket(entry.getValue().getAddress(), entry.getValue().getPort());
                Protocol.sendBroadcastMessage(socket, message, myName, id);
                socket.close();
            } catch (IOException e) {
                System.out.println(myName + " deleting " + entry.getKey());
                table.delete(entry.getKey());
            }
        }
    }

    /**
     * send JSONObject on to every node in the table
     * @param broadcast
     */
    private void sendBroadcast(JSONObject broadcast) {
        for(Map.Entry<String, InetSocketAddress> entry: table.getEntries()) {
            try {
                Socket socket = new Socket(entry.getValue().getAddress(), entry.getValue().getPort());
                Protocol.sendJSON(broadcast, socket);
                socket.close();
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
                        if(!listening) break;
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
                    System.out.println(myName + " deleting " + randNode);
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
            } else if(message.containsKey("LookupRequest")) {
                handleLookupRequest(message);
            } else if(message.containsKey("LookupResponse")) {
                handleLookupResponse(message);
            } else {
                System.out.println("Don't know what to do with message: " + message);
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
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
            if(ids == null) {   // no messages from this sender in the table
                System.out.println(myName + " received new broadcast: " + messageString);
                // add message to collection
                ids = new ConcurrentSkipListSet<Integer>();
                ids.add(id);
                receivedMessages.put(sender, ids);
                sendBroadcast(messageObject);   // send message on to all nodes in the table
            } else if(!ids.contains(id)) {  // no entry with this id from this sender
                System.out.println(myName + " received new broadcast: " + messageString);
                ids.add(id);
                sendBroadcast(messageObject); // send message on to all nodes in the table
            } else if(ids.contains(id)) return; // this node has already received the message => ignore it

            // wait a long time, expect message to propagate through network
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // delete message, because it will no longer be needed
            ids.remove(id);
        }

        public void handleLookupRequest(JSONObject messageObject) {
            String nodeName = (String)messageObject.get("NodeName");
            InetSocketAddress address = table.getAddress(nodeName);

            // if this node has the address of the looked for node, it will send a response to the inquirer
            if(address != null) {
                String inquirerHost = (String)messageObject.get("Host");
                int inquirerPort = ((Long)messageObject.get("Port")).intValue();
                try {
                    Socket socket = new Socket(inquirerHost, inquirerPort);
                    JSONObject response = new JSONObject();
                    response.put("NodeName", nodeName);
                    response.put("LookupResponse", true);
                    response.put("Port", address.getPort());
                    response.put("Host", address.getHostName());
                    Protocol.sendJSON(response, socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {    // else send on the lookup message to all nodes in the table
                sendBroadcast(messageObject);
            }
        }

        public void handleLookupResponse(JSONObject messageObject) {
            String nodeName = (String)messageObject.get("NodeName");
            if(lookupInProgress.get() && lookupName.equals(nodeName)) {
                int port = ((Long)messageObject.get("Port")).intValue();
                String host = (String)messageObject.get("Host");
                lookupAddress = new InetSocketAddress(host, port);   // set global variable
            }

        }

    }

}
