package guard.license;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Auth {

    public final String appname;
    public final String ownerid;
    public final String version;
    public final String url;

    protected String sessionid;
    protected boolean initialized;
    public boolean auth = false;


    public Auth(String appname, String ownerid, String version, String url) {
        this.appname = appname;
        this.ownerid = ownerid;
        this.version = version;
        this.url = url;
    }

    public void init() {
        HttpResponse<String> response;
        try {
            response = Unirest.post(url).field("type", "init").field("ver", version).field("name", appname)
                    .field("ownerid", ownerid).asString();

            // System.out.println(response.getBody());
            try {
                JSONObject responseJSON = new JSONObject(response.getBody());

                if (response.getBody().equalsIgnoreCase("KeyAuth_Invalid")) {
                    // Calling the method with a disabled connection
                    // System.exit(0);
                    //System.out.println("invalid");
                }

                if (responseJSON.getBoolean("success")) {
                    sessionid = responseJSON.getString("sessionid");
                    initialized = true;
                    //System.out.println("Session ID: " + responseJSON.getString("sessionid"));

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

        HttpResponse<String> response;
        try {
            String hwid = HWID.getHWID();

            response = Unirest.post(url).field("type", "login").field("username", username).field("pass", password)
                    .field("hwid", hwid).field("sessionid", sessionid).field("name", appname).field("ownerid", ownerid)
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

        HttpResponse<String> response;
        try {
            String hwid = HWID.getHWID();

            response = Unirest.post(url).field("type", "register").field("username", key + "1").field("pass", key + "2").field("key", key).field("hwid", hwid).field("sessionid", sessionid).field("name",appname).field("ownerid", ownerid).asString();

            try {
                JSONObject responseJSON = new JSONObject(response.getBody());

                if (!responseJSON.getBoolean("success")) {
                    //System.out.println("the license does not exist " + responseJSON);
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

    public static String encrypt(String algorithm, String input, SecretKey key,
                                 IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public static String decrypt(String algorithm, String cipherText, SecretKey key,
                                 IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }
}
