package hw10;

import java.util.concurrent.*;
import java.net.*;
import java.io.*;
import java.math.*;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

class CryptServer {
    public static final int listenPort = 9192;
    public static final int threadPoolSize = 10;

    private ExecutorService service;
    boolean listening = true;

    public static void main(String[] args) {
        System.out.println("Server started!");
        CryptServer server = new CryptServer();
        server.listen();
        System.out.println("end of server");
    }
    
    public String getkey(){
    	System.out.println("Message received enter encryption key:");
        String key = null;
		try {
        BufferedReader ObjIn = new BufferedReader(new InputStreamReader(System.in));
        key = ObjIn.readLine();
        }catch (IOException e){
        System.out.println("Error entering Key");
        }
        return key;
    }

    private void listen() {
        service = Executors.newFixedThreadPool(threadPoolSize);

        try {
            ServerSocket serverSocket = new ServerSocket(listenPort);
            while(listening) {
                try {
                    service.execute(new RequestHandler(serverSocket.accept()));
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        } catch(Exception e) {
            System.err.println("Exception caught" + e);
        } finally {
            service.shutdown();
        }
    }
}

	class RequestHandler implements Runnable {
		private final Socket socket;
		private final OTP decode;

    RequestHandler(Socket s){
        socket = s;
        decode = new OTP();
        
    }
    public void run() {
        System.out.println("New Connection");
        String inputLine;
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); ) {
                while( (inputLine = in.readLine()) != null) {
                    System.out.println("Received: " + inputLine);
                    String message = "";
                    String encryptedtext = inputLine;
                            
                    System.out.println("Message received enter encryption key:");
                    String key = "";
            		try {
                    BufferedReader ObjIn = new BufferedReader(new InputStreamReader(System.in));
                    key = ObjIn.readLine();
                    }catch (IOException e){
                    System.out.println("Error entering Key");
                    }
                    
                    message = decode.decrypt(key, encryptedtext);
                    System.out.println("Decrypted and decoded: " + message);
                }
            } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Connection closed");
    }
}
