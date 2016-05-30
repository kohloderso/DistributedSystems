package hw10.rsa;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by christina on 30.05.16.
 */
public class RSAServer {
    public static final int port = 1234;
    public static final int threadPoolSize = 2;

    private ExecutorService service;
    boolean listening = true;

    private KeyPair myKP;

    public RSAServer() {
        myKP = RSA.generateKP("server");
    }

    public void start() {
        // open Serversocket and listen for messages
        try {
            service = Executors.newFixedThreadPool(threadPoolSize);
            ServerSocket serverSocket = new ServerSocket(port);
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


    class RequestHandler implements Runnable {
        private final Socket socket;

        RequestHandler(Socket s){
            socket = s;

        }
        public void run() {
            System.out.println("New Connection");
            PublicKey partnerKey = RSA.getPublicKeyFromFile("client");
            System.out.println("acquired public key");
            String inputLine;
            boolean exit = false;
            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true) ;
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                while(!exit) {
                    while( (inputLine = in.readLine()) != null) {
                        System.out.println("Received: " + inputLine);

                        // decode message
                        String message = RSA.decrypt(inputLine, myKP.getPrivate());
                        if(message.equalsIgnoreCase("exit")) exit = true;
                        String answer = RSA.encrypt("OK", partnerKey);
                        out.println(answer);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Connection closed");
        }
    }

    public static void main(String[] args) throws UnsupportedEncodingException{

        RSAServer server = new RSAServer();
        server.start();

    }

}

