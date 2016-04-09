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

    public SocketAddress locate() {
        try {
            System.out.println("Client is started!");
			/*
			 * Create a packet to the broadcast Address 255.255.255.255
			 * with the content "Ping".
			 */
            byte[] message = "Ping".getBytes();
            DatagramSocket socket = new DatagramSocket();
            InetAddress internetAdress = InetAddress.getByName("255.255.255.255");  // broadcast address
            DatagramPacket packet = new DatagramPacket(message, message.length, internetAdress, port);
            socket.send(packet);
            socket.setSoTimeout(timeout);
            socket.receive(packet);

            // TODO parse message get Serveraddress and return it

        } catch (final IOException e) {
			e.printStackTrace();
        }
        return null;
    }


}
