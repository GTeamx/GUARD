package guard.license;

import com.google.common.hash.Hashing;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import guard.Guard;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public class Auth {


    public final String appname;
    public final String ownerid;
    public final String version;
    public final String url;
    public String hash;

    protected String sessionid;
    protected String enckey;
    protected String secret;
    protected boolean initialized;
    public boolean auth = false;
    public String iv_key;

    static {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }

            } };

            SSLContext sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            Unirest.setHttpClient(httpclient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Auth(String appname, String ownerid, String secret, String version, String url) {
        this.appname = appname;
        this.ownerid = ownerid;
        this.version = version;
        this.secret = secret;
        this.url = url;
        String[] ivkeys = UUID.randomUUID().toString().split("-");
        iv_key = ivkeys[0];
    }

    public void init() {
        HttpResponse<String> response;
        try {
            enckey = sha256RealGoogle(iv_key); //4b171aaf5d60b10c5d7ccca28c38f9de
            //                         4b171aaf5d60b10c5d7ccca28c38f9de

            setIvKey("12a7150c1688bf2b86c549c966c6c68cc33411d8accc397bcad1ca26e525a33e"); // iv_key
            String init_iv = sha256RealGoogle(iv_key);
            System.out.println(AES.AESEncrypt("1.0", enckey, new String(ivKey)));
            System.out.println("version:  " + AES.AESEncrypt("1.0", "12a7150c1688bf2b86c549c966c6c68cc33411d8accc397bcad1ca26e525a33e", sha256RealGoogle(iv_key)));
            response = Unirest.post(url).field("type", Hex.encodeHex("init".getBytes(StandardCharsets.UTF_8)))
                    .field("ver", AES.AESEncrypt(version, secret, init_iv))
                    .field("hash", hash)
                    .field("enckey", AES.AESEncrypt(enckey, secret, init_iv))
                    .field("name", Hex.encodeHex(appname.getBytes(StandardCharsets.UTF_8)))
                    .field("ownerid", Hex.encodeHex(ownerid.getBytes(StandardCharsets.UTF_8)))
                    .field("init_iv", init_iv)
                    .asString();

            System.out.println("" + response.getBody());
            try {

                JSONObject responseJSON = new JSONObject(response.getBody());

                if (response.getBody().equalsIgnoreCase("KeyAuth_Invalid")) {
                    // Calling the method with a disabled connection
                    // System.exit(0);
                    //System.out.println("invalid"); // a75fffd1b4a41fffd3a4b364f16fffd6f8fffd
                } //                               a75fffd1b4a41fffd3a4b364f16fffd6f8fffd

                if (responseJSON.getBoolean("success")) {
                    sessionid = responseJSON.getString("sessionid");
                    initialized = true;
                    System.out.println("Session ID: " + responseJSON.getString("sessionid"));

                } else if (responseJSON.getString("message").equalsIgnoreCase("invalidver")) {
                    // Calling the method with a disabled version
                    // System.out.println(reponseJSON.getString("download"));

                } else {
                    //System.out.println(responseJSON.getString("message"));
                    // System.exit(0);
                }

            } catch (Exception e) {

            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    public void login(String username, String password) {

        if (!initialized) {
            //System.out.println("\n\n Please initzalize first");
            return;
        }
        setIvKey(iv_key);
        byte[] init_iv = ivKey;
        HttpResponse<String> response;
        try {
            String hwid = HWID.getHWID();

            response = Unirest.post(url).field("type", byte_arr_to_str("login".getBytes(StandardCharsets.UTF_8)))
                    .field("username", AES.encrypt(username, enckey, init_iv))
                    .field("pass", AES.encrypt(password, enckey, init_iv))
                    .field("hwid", AES.encrypt(hwid, enckey, init_iv))
                    .field("sessionid", byte_arr_to_str(sessionid.getBytes(StandardCharsets.UTF_8)))
                    .field("name", byte_arr_to_str(appname.getBytes(StandardCharsets.UTF_8)))
                    .field("ownerid", byte_arr_to_str(ownerid.getBytes(StandardCharsets.UTF_8)))
                    .field("init_iv", init_iv)
                    .asString();

            try {
                JSONObject responseJSON = new JSONObject(response.getBody());
                if (!responseJSON.getBoolean("success")) {
                    System.out.println("Error " + responseJSON);
                    auth = false;
                    // System.exit(0);
                } else {
                    //System.out.println("YAY YOU LOGGED IN!");
                    auth = true;
                    // optional success msg
                }

            } catch (Exception e) {

            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    public void upgrade(String username, String key) {
        if (!initialized) {
            System.out.println("\n\n Please initzalize first");
            return;
        }

        HttpResponse<String> response;
        try {
            String hwid = HWID.getHWID();

            response = Unirest.post(url).field("type", "upgrade").field("username", username).field("key", key)
                    .field("hwid", hwid).field("sessionid", sessionid).field("name", appname).field("ownerid", ownerid)
                    .asString();

            try {
                JSONObject responseJSON = new JSONObject(response.getBody());

                if (!responseJSON.getBoolean("success")) {
                    System.out.println("Error");
                    // System.exit(0);
                } else {

                    // optional success msg
                }

            } catch (Exception e) {

            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    public void license(String key) {
        if (!initialized) {
            //System.out.println("\n\n Please initzalize first");
            return;
        }

        HttpResponse<String> response;
        try {
            String hwid = HWID.getHWID();

            response = Unirest.post(url).field("type", "license").field("key", key).field("hwid", hwid)
                    .field("sessionid", sessionid).field("name", appname).field("ownerid", ownerid).asString();

            try {
                JSONObject responseJSON = new JSONObject(response.getBody());

                if (!responseJSON.getBoolean("success")) {
                    // System.out.println("the license does not exist");
                    // System.exit(0);
                    auth = false;
                } else {
                    // System.out.println("YAY LOGGED IN WITH LICENSE");
                    auth = true;
                    // optional success msg
                }

            } catch (Exception e) {

            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    public void register(String key) {
        if (!initialized) {
            //System.out.println("\n\n Please initzalize first");
            return;
        }
        setIvKey(iv_key);
        byte[] init_iv = ivKey;
        HttpResponse<String> response;
        try {
            String hwid = HWID.getHWID();

            response = Unirest.post(url)
                    .field("type", byte_arr_to_str("register".getBytes(StandardCharsets.UTF_8)))
                    .field("username", AES.encrypt(key + "1", enckey, init_iv))
                    .field("pass", AES.encrypt(key + "2", enckey, init_iv))
                    .field("key", AES.encrypt(key, enckey, init_iv))
                    .field("hwid", AES.encrypt(hwid, enckey, init_iv))
                    .field("sessionid", byte_arr_to_str(sessionid.getBytes(StandardCharsets.UTF_8)))
                    .field("name",byte_arr_to_str(appname.getBytes(StandardCharsets.UTF_8)))
                    .field("ownerid", byte_arr_to_str(ownerid.getBytes(StandardCharsets.UTF_8)))
                    .field("init_iv", init_iv)
                    .asString();

            try {
                JSONObject responseJSON = new JSONObject(response.getBody());

                if (!responseJSON.getBoolean("success")) {
                    //System.out.println("the license does not exist " + responseJSON);
                    System.out.println("register:  " + responseJSON);
                    if(responseJSON.getString("message").contains("Already Exists")) {
                        login(key + "1", key + "2");
                        // register();
                    }
                    // System.exit(0);
                } else {
                    //  System.out.println("YAY LOGGED IN WITH LICENSE");
                    auth = true;
                    // optional success msg
                }

            } catch (Exception e) {

            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    public void ban() {
        if (!initialized) {
            // System.out.println("\n\n Please initzalize first");
            return;
        }

        HttpResponse<String> response;
        try {
            String hwid = HWID.getHWID();

            response = Unirest.post(url).field("type", "ban").field("sessionid", sessionid).field("name", appname)
                    .field("ownerid", ownerid).asString();

            try {
                JSONObject responseJSON = new JSONObject(response.getBody());

                if (!responseJSON.getBoolean("success")) {
                    //  System.out.println("Error");
                    // System.exit(0);
                } else {

                    // optional success msg
                }

            } catch (Exception e) {

            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }



    public void webhook(String webid, String param) {
        if (!initialized) {
            // System.out.println("\n\n Please initzalize first");
            return;
        }

        HttpResponse<String> response;
        try {
            String hwid = HWID.getHWID();

            response = Unirest.post(url).field("type", "webhook").field("webid", webid).field("params", param)
                    .field("sessionid", sessionid).field("name", appname).field("ownerid", ownerid).asString();

            try {
                JSONObject responseJSON = new JSONObject(response.getBody());

                if (!responseJSON.getBoolean("success")) {
                    //   System.out.println("Error");
                    // System.exit(0);
                } else {

                    // optional success msg
                }

            } catch (Exception e) {

            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }


    }

    public static String encrypt_string(String algorithm, String input, SecretKey key,
                                        IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        System.out.println("6");
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public static String decrypt_string(String algorithm, String cipherText, SecretKey key,
                                        IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }

    public String byte_arr_to_str(byte[] ba) {

        return new String(ba, StandardCharsets.UTF_8);
    }
    public String sha256(final String base) {
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


    public byte[] str_to_byte_arr(String hex) {
        return hex.getBytes(StandardCharsets.UTF_8);
    }

    public String encrypt(String message, String enc_key, String iv)
    {
        byte[] _key = sha256(enc_key).substring(0, 32).getBytes(StandardCharsets.UTF_8);

        byte[] _iv = sha256(iv).substring(0, 16).getBytes(StandardCharsets.UTF_8);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new SecretKeySpec(_key, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
                    .getEncoded(), "RSA");
            IvParameterSpec ivd = new IvParameterSpec(_iv);
            return encrypt_string("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", message, secret, ivd);
        }catch(Exception i) {
            System.out.println("" + i);
        }
        return null;
    }


    public String decrypt(String message, String enc_key, String iv)
    {
        byte[] _key = sha256(enc_key).substring(0, 32).getBytes(StandardCharsets.UTF_8);

        byte[] _iv = sha256(iv).substring(0, 16).getBytes(StandardCharsets.UTF_8);
        SecretKey d = new SecretKeySpec(_key, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        IvParameterSpec ivd = new IvParameterSpec(_iv);
        try {
            return decrypt_string("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", message, d, ivd);
        }catch(Exception ignored) {

        }
        return null;
    }

    public String sha256RealGoogle(String t) {
        return Hashing.sha256().hashString("12a7150c1688bf2b86c549c966c6c68cc33411d8accc397bcad1ca26e525a33e", StandardCharsets.UTF_8).toString();
    }
    private static byte[] ivKey;
    public void setIvKey(final String myKey) {
        MessageDigest sha = null;
        try {
            ivKey = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-256");
            ivKey = sha.digest(ivKey);
            ivKey = Arrays.copyOf(ivKey, 16);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void printCurrentWorkingDirectory1() throws IOException, NoSuchAlgorithmException {
        String path = Guard.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        System.out.println("" + decodedPath);
        String[] asd = decodedPath.split("/");
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(Paths.get(asd[6])));
        byte[] digest = md.digest();
        String myChecksum = new String(Hex.encodeHex(digest));
        hash = myChecksum;

        //String userDirectory = System.getProperty("user.dir");
        //File path4 = new File(userDirectory);
        //if(path.listFiles() != null) {
        //     for (final File file : Objects.requireNonNull(path.listFiles())) {
        //         file.getName().startsWith("Guard.");
        //     }
        // }

        /*


         */
    }
}
