package hw3;

import java.io.IOException;
import java.net.*;

/**
 * Created by christina on 09.04.16.
 */
public class ServiceLocator {
    private int port;
    private int timeout;


    public ServiceLocator(int port, int timeout) {
        this.port = port;
        this.timeout = timeout;
    }

    public InetAddress locate() {
        try {
            System.out.println("Client is started!");
			/*
			 * Create a packet to the broadcast Address 255.255.255.255
			 * with the content "Ping".
			 */
            byte[] message = "Discover_Server".getBytes();
            System.out.println("Sending Broadcast: " + new String(message));
            System.out.println("message length: " + message.length);
            DatagramSocket socket = new DatagramSocket();
            InetAddress internetAdress = InetAddress.getByName("255.255.255.255");  // broadcast address
            DatagramPacket packet = new DatagramPacket(message, message.length, internetAdress, port);
            socket.send(packet);
            socket.setSoTimeout(timeout);

            byte[] buffer = new byte[64];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(responsePacket);

            String response = new String(responsePacket.getData()).trim();
            System.out.println("Response: " + response);

            String addr = response.split(":")[1];
            InetAddress address = InetAddress.getByName(addr.split("/")[0]);
            System.out.println(address);
            return address;

        } catch (final IOException e) {
			e.printStackTrace();
        }
        return null;
    }


}
