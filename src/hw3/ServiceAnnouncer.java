package hw3;

import java.io.IOException;
import java.net.*;

/**
 * Created by christina on 09.04.16.
 */
public class ServiceAnnouncer implements Runnable {
    private int port;
    private String serviceName;
    private SocketAddress address;

    public ServiceAnnouncer(int port, String serviceName, SocketAddress address) {
        this.port = port;
        this.serviceName = serviceName;
        this.address = address;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[12];
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(port);

            while (true) {

                final DatagramPacket packet = new DatagramPacket(buffer,
                        buffer.length);

                socket.receive(packet);

                final String data = new String(packet.getData()).trim();

                if (data.equalsIgnoreCase("Ping")) {
                    System.out.println("Ping sent from client : "
                            + packet.getAddress().getHostAddress() + ":"
                            + packet.getPort());

                    final DatagramPacket response = new DatagramPacket(
                            InetAddress.getLocalHost().getAddress(),
                            InetAddress.getLocalHost().getAddress().length,
                            packet.getAddress(), packet.getPort());
                    socket.send(response);
                } else {
                    System.out.println("Invalid message: " + data);
                }

            }
        } catch (final SocketException e) {
            System.out.println("Port is already in use!");
            e.printStackTrace();
        } catch (final IOException e) {
            System.out.println("Communication error!");
            e.printStackTrace();
        }
    }
}