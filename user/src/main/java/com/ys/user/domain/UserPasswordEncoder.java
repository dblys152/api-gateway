package com.ys.user.domain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserPasswordEncoder {
    private static final int STRENGTH_OF_256 = 12;

    public static String encode(String password) {
        return new BCryptPasswordEncoder(STRENGTH_OF_256).encode(password);
    }

    public static boolean matches(String originPassword, String encodedPassword) {
        return new BCryptPasswordEncoder().matches(originPassword, encodedPassword);
    }
}
