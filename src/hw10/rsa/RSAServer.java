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
    public static final int threadPoolSize = 10;

    private ExecutorService service;
    boolean listening = true;

    private KeyPair myKP;

    public RSAServer() {
        String name = "server";
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        kpg.initialize(512);
        myKP = kpg.generateKeyPair();

        try {
            FileOutputStream out = new FileOutputStream("private" + name);
            out.write(myKP.getPrivate().getEncoded());
            out.close();
            FileOutputStream out2 = new FileOutputStream("public" + name);
            out2.write(myKP.getPublic().getEncoded());
            out2.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get public key of other instance
        Path path = Paths.get("public" + "client");
        try {
            byte[] encodedKey = Files.readAllBytes(path);
            PublicKey publicKey =
                    KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encodedKey));
            System.out.println(publicKey.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

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

    public static PublicKey get(String filename)
            throws Exception {

        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int)f.length()];
        dis.readFully(keyBytes);
        dis.close();

        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }





    class RequestHandler implements Runnable {
        private final Socket socket;

        RequestHandler(Socket s){
            socket = s;

        }
        public void run() {
            System.out.println("New Connection");
            String inputLine;
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); ) {
                while( (inputLine = in.readLine()) != null) {
                    System.out.println("Received: " + inputLine);
                    String encryptedtext = inputLine;

                    // decode message
                    try {
                        Cipher cipher = Cipher.getInstance("RSA");
                        cipher.init(Cipher.DECRYPT_MODE, myKP.getPrivate());
                        byte[] ciphertextBytes = Base64.getDecoder().decode(encryptedtext.getBytes());
                        byte[] decryptedBytes = cipher.doFinal(ciphertextBytes);
                        String decryptedString = new String(decryptedBytes);
                        System.out.println("decrypted (plaintext) = " + decryptedString);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
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

    }

}

