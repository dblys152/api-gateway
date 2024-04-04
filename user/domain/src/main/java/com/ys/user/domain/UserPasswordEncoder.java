package com.ys.user.domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserPasswordEncoder {
    private static final String HASH_ALGORITHM = "SHA-512";

    public static String encode(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(password.getBytes());
            return bytesToHexString(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(String.format("Not Found %s", HASH_ALGORITHM), ex);
        }
    }

    public static boolean matches(String originPassword, String encodedPassword) {
        return encode(originPassword).equals(encodedPassword);
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
