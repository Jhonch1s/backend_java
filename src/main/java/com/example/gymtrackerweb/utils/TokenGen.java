package com.example.gymtrackerweb.utils;
// TokenGen.java

import java.security.SecureRandom;

public final class TokenGen {
    private static final String ALPH = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RNG = new SecureRandom();

    public static String base62(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(ALPH.charAt(RNG.nextInt(ALPH.length())));
        return sb.toString();
    }

    private TokenGen() {}
}
