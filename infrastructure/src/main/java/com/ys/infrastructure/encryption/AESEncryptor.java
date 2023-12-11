package com.ys.infrastructure.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESEncryptor implements TwoWayEncryptor {
    private static AESEncryptor instance;
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    public static AESEncryptor getInstance() {
        if (instance == null) {
            instance = new AESEncryptor();
        }
        return instance;
    }

    @Override
    public String encrypt(String message, String secretKey) {
        if (message == null) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey.getBytes(), ALGORITHM));
            return Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes()));
        } catch (Exception ex) {
            throw new RuntimeException("Error encrypting data", ex);
        }
    }

    @Override
    public String decrypt(String message, String secretKey) {
        if (message == null) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey.getBytes(), ALGORITHM));
            return new String(cipher.doFinal(Base64.getDecoder().decode(message)));
        } catch (Exception ex) {
            throw new RuntimeException("Error decrypting data", ex);
        }
    }
}
