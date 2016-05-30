package hw10.rsa;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSA {


    /**
     * Generates a RSA KeyPair and writes the keys into files named by the given name.
     * @param name for the files containing the keys
     * @return the generated RSA KeyPair
     */
    public static KeyPair generateKP(String name) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(512);
            KeyPair myKP = kpg.generateKeyPair();
            FileOutputStream out = new FileOutputStream("private" + name);
            out.write(myKP.getPrivate().getEncoded());
            out.close();
            FileOutputStream out2 = new FileOutputStream("public" + name);
            out2.write(myKP.getPublic().getEncoded());
            out2.close();

            return myKP;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get a public key from a file.
     * @param name
     * @return the public key as decoded from the file
     */
    public static PublicKey getPublicKeyFromFile(String name) {
        if (new File("public" + name).isFile()) {
            try {
                Path p = Paths.get("public" + name);
                byte[] encodedKey = Files.readAllBytes(p);
                PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encodedKey));
                System.out.println("read key: " + publicKey.toString());
                return publicKey;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static String encrypt(String message, PublicKey key) {
        // ENCRYPT using the PUBLIC key
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());
            String encrypted = new String(Base64.getEncoder().encode(encryptedBytes));
            System.out.println("encrypted: " + encrypted);
            return encrypted;
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
        return null;
    }

    public static String decrypt(String encrypted, PrivateKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] ciphertextBytes = Base64.getDecoder().decode(encrypted.getBytes());
            byte[] decryptedBytes = cipher.doFinal(ciphertextBytes);
            String decryptedString = new String(decryptedBytes);
            System.out.println("decrypted: " + decryptedString);
            return decryptedString;
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
        return null;
    }



}
