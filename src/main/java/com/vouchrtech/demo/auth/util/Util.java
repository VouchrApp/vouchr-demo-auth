package com.vouchrtech.demo.auth.util;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;

public class Util {

    private Util() {
        // block instantiation
    }

    public static String base64Hmac256(String str, String key) throws SecurityException {
        try {
            final Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            final SecretKeySpec secretKey = new SecretKeySpec(Base64.getUrlDecoder().decode(key), "HmacSHA256");
            sha256HMAC.init(secretKey);
            final byte[] macData = sha256HMAC.doFinal(str.getBytes("UTF-8"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(macData);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new SecurityException("Unable to get hmac for string: " + str, e);
        }
    }
}
