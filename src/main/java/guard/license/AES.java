package guard.license;

import com.google.common.hash.Hashing;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class AES {
    private static SecretKeySpec secretKey;
    private static byte[] key;
    private static final String SALT = "ssshhhhhhhhhhh!!!!!";

    public static void setKey(final String myKey) {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private static IvParameterSpec iv_key;
    private static byte[] ivKey;

    public static void setIvKey(final String myKey) {
        MessageDigest sha = null;
        try {
            ivKey = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-256");
            ivKey = sha.digest(ivKey);
            ivKey = Arrays.copyOf(ivKey, 16);
            iv_key = new IvParameterSpec(ivKey);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String strToEncrypt, String enckey2, byte[] ivkey2) {
        try {
            IvParameterSpec ivspec = new IvParameterSpec(ivkey2);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(enckey2.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt, String enckey2, byte[] ivkey2) {
        try {
            IvParameterSpec ivspec = new IvParameterSpec(ivkey2);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(enckey2.toCharArray(), SALT.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(strToDecrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    public static String AESDecrypt(String text, String enckey34, byte[] ivkey34)
    {
        try
        {
            byte[] enckey1337 = enckey34.substring(0, 32).getBytes();
            IvParameterSpec ivspec = new IvParameterSpec(ivkey34);
            byte[] finalKey;
            finalKey = Arrays.copyOf(enckey1337, 16); // copying fist 16 bytes from hashed secret key to finaly key which to use in algo
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            SecretKeySpec secretKey = new SecretKeySpec(enckey1337, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(text.getBytes()));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "";

    }

    public static String AESEncrypt(String text, String enckey34, String ivkey34)
    {
        try
        {
            byte[] enckey1337 = sha256RealGoogle(enckey34).substring(0, 32).getBytes();
            byte[] ivkey1337 = Arrays.copyOf(sha256RealGoogle(ivkey34).getBytes(), 16);;
            IvParameterSpec ivspec = new IvParameterSpec(ivkey1337);
            byte[] finalKey;
            finalKey = enckey1337; // copying fist 16 bytes from hashed secret key to finaly key which to use in algo
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(finalKey, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return new String(Hex.encodeHex(cipher.doFinal(text.getBytes())));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "";

    } // CnWxG0pBjjpLNk8W19u4sQ==
    //   CnWxG0pBjjpLNk8W19u4sQ==  981abdd9ba91acb8e6716f39baacb09d

    public static String AESEncrypt2(String text, String enckey34, String ivkey34)
    {
        try
        {
            byte[] enckey1337 = sha256RealGoogle(enckey34).substring(0, 32).getBytes(StandardCharsets.UTF_8);
            byte[] ivkey1337 = sha256RealGoogle(ivkey34).substring(0, 16).getBytes(StandardCharsets.UTF_8);
            IvParameterSpec ivspec = new IvParameterSpec(ivkey1337);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(enckey1337, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return new String(Hex.encodeHex(cipher.doFinal(text.getBytes()))); //
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "";

    }

    public static String sha256(final String base) {
        MessageDigest sha = null;
        try{
            byte[] key;
            key = base.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            return new String(key);
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    public static String sha2562(final String base) {
        MessageDigest sha = null;
        try{
            byte[] key;
            key = base.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 32);
            return new String(key);
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    public static String sha256RealGoogle(String t) {
        return Hashing.sha256().hashString("12a7150c1688bf2b86c549c966c6c68cc33411d8accc397bcad1ca26e525a33e", StandardCharsets.UTF_8).toString();
    }
    public static String convertStringToHex(String str) {
        StringBuilder stringBuilder = new StringBuilder();

        char[] charArray = str.toCharArray();

        for (char c : charArray) {
            String charToHex = Integer.toHexString(c);
            stringBuilder.append(charToHex);
        }

        return stringBuilder.toString();
    }
}
