package org.example;

import com.google.gson.Gson;
import org.apache.commons.lang3.ArrayUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class RSAEncryptionExample {

    public static void main(String[] args) throws Exception {

        DingerPayload payload = DingerPayload.create();

        // Convert the object to a JSON string using Gson
        String jsonString = convertObjectToJsonString(payload);

        System.out.println(jsonString);

        byte[] encryptedData = encrypt(jsonString, DingerConstant.paymentEncryptionKey);

        String encodedPayload =  Base64.getEncoder().encodeToString(encryptedData);
        String urlEncodedPayload = urlEncode(encodedPayload);
        String hash = calculateHmacSha256(urlEncodedPayload, DingerConstant.secretKey);
        String urlEncodedHash = urlEncode(hash);

        System.out.println();
        System.out.println("Payload - " + encodedPayload);
        System.out.println("UrlEncodedPayload - " + urlEncodedPayload);
        System.out.println("Hash - " + hash);
        System.out.println("UrlEncodedHash - " + urlEncodedHash);

        String url = "https://prebuilt.dinger.asia?payload="+ urlEncodedPayload + "&hashValue=" + hash;
        System.out.println("Url - " + url);

    }

    public static byte[] encrypt(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        byte[] enBytes = null;
        int i = 0;
        while (i < data.length()) {
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data.getBytes(), i, i + 64));
            enBytes = ArrayUtils.addAll(enBytes, doFinal);
            i += 64;
        }
        return enBytes;
    }

    public static PublicKey getPublicKey(String base64PublicKey) {
        PublicKey publicKey = null;
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    private static String calculateHmacSha256(String data, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKeySpec);

        byte[] hashBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // Convert the hash to Base64 for a string representation
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    private static String convertObjectToJsonString(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    private static String urlEncode(String data) throws UnsupportedEncodingException {
        return URLEncoder.encode(data, "UTF-8");
    }
}