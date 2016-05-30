package hw10.rsa;

import sun.security.rsa.RSAKeyPairGenerator;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RSAClient {

	private Socket socket;
	private KeyPair myKP;
	private PublicKey partnerKey;

	public RSAClient() {
		myKP = RSA.generateKP("client");

		// get public key of other instance
		partnerKey = RSA.getPublicKeyFromFile("server");
		while(partnerKey == null) {
			System.out.println("waiting for public key to be published");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			partnerKey = RSA.getPublicKeyFromFile("client");
		}
		System.out.println("acquired public key");
	}

	public void start() {
		Scanner scan = new Scanner(System.in);

		try {
			socket = new Socket("localhost", RSAServer.port);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true) {
				System.out.println("Type in message: ");
				String message = scan.nextLine();
				sendMessage(message);

				String answer = in.readLine();
				RSA.decrypt(answer, myKP.getPrivate());

				if(message.equalsIgnoreCase("exit")) break;
			}

			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) {
		// ENCRYPT using the PUBLIC key
		String encrypted = RSA.encrypt(message, partnerKey);
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println(encrypted);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	
	public static void main(String[] args) throws UnsupportedEncodingException{

		RSAClient client = new RSAClient();
		// wait for the server to be ready
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		client.start();

    }

}
