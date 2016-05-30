package hw10;

import java.net.*;
import java.io.*;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;

public class CryptClient {
	public final OTP encoding;
	
	public CryptClient (){
		encoding = new OTP();
	}

		
	public double request(String hostname, String message) throws UnsupportedEncodingException{
		int prtNumber = CryptServer.listenPort;
		String [] secret = encoding.encrypt(message);
		String encryptedtext = secret[0];
		String key = secret[1];
		System.out.println(encryptedtext);
		System.out.println("Key:" + key);
		try {
			Socket cryptsocket = new Socket(hostname, prtNumber);
			PrintWriter out = new PrintWriter(cryptsocket.getOutputStream(), true);
			out.println(encryptedtext);
			cryptsocket.close();
			System.out.println("Connection closed");
		}catch (UnknownHostException e){
			System.err.println("Failed to resolve hostname " + hostname);
			System.exit(1);
		}catch (IOException e){
			System.err.println("Failed to connect to " + hostname);
			System.exit(1);
		}
		return 0;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException{
		System.out.println("Client online, encrytion started!");
        CryptClient c = new CryptClient();
        double r = c.request("localhost", args[0]);
        System.out.println("Client closed");
    }

}
