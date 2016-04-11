package hw3;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class offers a method to locate services via a UDP-broadcast
 */
public class ServiceLocator {
    private final int threadPoolSize = 10;
    private int port;
    private int timeout;
    private ConcurrentHashMap<String, List<InetSocketAddress>> servers = new ConcurrentHashMap<>();
    private ExecutorService service;


    /**
     * creates a new ServiceLocator that is able to send a broadcast to the given port and waites timeout milliseconds for responses
     * @param port
     * @param timeout
     */
    public ServiceLocator(int port, int timeout) {
        this.port = port;
        this.timeout = timeout;
    }

    /**
     * Sends a broadcast and puts all responses into a Hashmap with the server name as key and a list of all addresses as value
     * @return number of responses
     */
    public int locate() {
        service = Executors.newFixedThreadPool(threadPoolSize);
        try {
            System.out.println("Client is started!");

            // send discover_service message as broadcast
            byte[] message = "Discover_Service".getBytes();
            System.out.println("Sending Broadcast: " + new String(message));
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);  // is this necessary?
            InetAddress internetAdress = InetAddress.getByName("255.255.255.255");  // broadcast address
            DatagramPacket packet = new DatagramPacket(message, message.length, internetAdress, port);
            socket.send(packet);

            // wait for response
            socket.setSoTimeout(timeout);
            System.out.println("waiting " + timeout + " milliseconds " + "for responses");
            byte[] buffer = new byte[64];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            while(true) {
                try {
                    socket.receive(responsePacket);
                    service.execute(new ResponseHandler(responsePacket.getData()));
                } catch (IOException e) {
                System.out.println("ServiceLocator Timeout");
                socket.close();
                service.shutdown();
                break;
            }

            }
        } catch (final IOException e) {
			e.printStackTrace();
        }

        System.out.println("got " + servers.size() + " responses.");
        System.out.println(servers.toString());
       return servers.size();
    }

    public Enumeration<String> getAllServers() {
        return servers.keys();
    }

    public List<InetSocketAddress> getAddresses(String server) {
        return servers.get(server);
    }


    private class ResponseHandler implements Runnable {

        private final byte[] data;

        ResponseHandler(byte[] data){
            this.data=data;
        }

        /**
         * parse the data and put it into the Hashmap
         */
        @Override
        public void run() {
            try {
                String response = new String(data).trim();
                System.out.println("Response: " + response);

                String[] content = response.split(":");
                String name = content[0];
                InetAddress addr = InetAddress.getByName(content[1].split("/")[0]);
                int port = Integer.parseInt(content[2]);
                InetSocketAddress address = new InetSocketAddress(addr, port);

                if(servers.containsKey(name)){
                    servers.get(name).add(address);
                }else{
                    List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
                    addresses.add(address);
                    servers.put(name, addresses);
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

        }
    }
}

