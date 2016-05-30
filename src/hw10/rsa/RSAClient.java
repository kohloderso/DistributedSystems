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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RSAClient {

	private Socket socket;
	private KeyPair myKP;
	private PublicKey partnerKey;



	public RSAClient() {
		String name = "client";
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
		Path path = Paths.get("public" + "server");
		try {
			byte[] encodedKey = Files.readAllBytes(path);
			partnerKey =
					KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encodedKey));
			System.out.println(partnerKey.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}

		try {
			socket = new Socket("localhost", RSAServer.port);

		} catch (IOException e) {
			e.printStackTrace();
		}

		sendMessage("hello world!");

		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) {
		// ENCRYPT using the PUBLIC key
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, partnerKey);
			byte[] encryptedBytes = cipher.doFinal(message.getBytes());
			String encrypted = new String(Base64.getEncoder().encode(encryptedBytes));
			System.out.println("encrypted: " + encrypted);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println(encrypted);
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	
	public static void main(String[] args) throws UnsupportedEncodingException{

		new RSAClient();


    }

}
