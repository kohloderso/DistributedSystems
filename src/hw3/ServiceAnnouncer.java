package hw3;

import java.io.IOException;
import java.net.*;

/**
 * creates a new ServiceAnnouncer, that listens per default on port 8888 for requests.
 * When it gets a "discover_service" message, it responds with the name, address and port number
 * of the service that instructed it to announce it's service.
 */
public class ServiceAnnouncer implements Runnable {
    private int announcerPort = 8888;
    private int timeout = 10000;
    private int port;
    private String serviceName;
    private InetAddress address;
    private boolean running = true;

    /**
     * creates a new ServiceAnnouncer for the service with this name, address and portnumber
     * @param port
     * @param serviceName
     * @param address
     */
    public ServiceAnnouncer(int port, String serviceName, InetAddress address) {
        this.port = port;
        this.serviceName = serviceName;
        this.address = address;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[32];
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(announcerPort);
            socket.setSoTimeout(timeout);

            System.out.println("Announcer is listening on " + socket.getLocalPort());

            while (running) {

                final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(packet);
                    final String data = new String(packet.getData()).trim();

                    if (data.equalsIgnoreCase("Discover_Service")) {
                        System.out.println("Broadcast sent from client : "
                                + packet.getAddress().getHostAddress() + ":"
                                + packet.getPort());
                        byte[] message = (serviceName + ":" + address + ":" + port).getBytes();
                        final DatagramPacket response = new DatagramPacket(message, message.length,
                                packet.getAddress(), packet.getPort());
                        socket.send(response);
                    } else {
                        System.out.println("Invalid message: " + data);
                    }
                } catch(SocketTimeoutException e) { // if the socket times out before it receives a packet, check if it should stop running
                    if(!running) break;
                }
            }
            System.out.println("Announcer shutting down");
            socket.close();
        } catch (final SocketException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        running = false;
    }

    public int getAnnouncerPort() {
        return announcerPort;
    }
}