package hw10.otp;


import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

class OTP {
	
		
	
	 String decrypt(String key, String encryptedtext) throws UnsupportedEncodingException {
		
		
		byte [] encrybin = new byte[encryptedtext.length()];
		byte [] keybin = new byte [key.length()];
		encrybin = encryptedtext.getBytes("UTF-8");
		keybin = key.getBytes("UTF-8");
		byte[] messagebin = new byte [encryptedtext.length()];
		messagebin = xor(keybin, encrybin);
		String message = new String(messagebin,StandardCharsets.UTF_8); 
		return message;	
	}

	 String[] encrypt(String message) throws UnsupportedEncodingException {
		Random randomgen = new Random();
		String key = "";
		for(int i = 1; i <= message.length(); i++){
			int randomInt = randomgen.nextInt(26);
			key = key + (char)(65 + randomInt);
		}
		byte [] keybin = new byte [key.length()];
		keybin = key.getBytes("UTF-8");
		byte[] messagebin = new byte [message.length()];
		messagebin = message.getBytes("UTF-8");
		byte [] encrybin = new byte[message.length()];
		encrybin = xor(keybin, messagebin);
		String encryptedtext = new String(encrybin,StandardCharsets.UTF_8); 
		String [] myarray = new String [2];
		myarray[0] = encryptedtext;
		myarray[1] = key;
		return myarray;
	}	

	 byte[] xor(byte[] a, byte[] b) {
	        byte[] result = new byte[Math.max(a.length, b.length)]; //we want to keep the "too long" bytes of the longer of the two arrays
	        System.arraycopy(a, 0, result, 0, a.length);
	        System.arraycopy(b, 0, result, 0, b.length);
	        
	        for(int i = 0; i < Math.min(a.length, b.length); ++i) {
	            result[i] = (byte)(a[i] ^ b[i]);
	        }
	        return result;
	    }
	 
}

