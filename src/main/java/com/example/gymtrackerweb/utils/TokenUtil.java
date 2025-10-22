package com.example.gymtrackerweb.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

// TokenUtil.java
public final class TokenUtil {
    private static final String SECRET = System.getenv().getOrDefault("GGYM_SHARE_SECRET", "cambia-esto");
    private static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder B64D = Base64.getUrlDecoder();

    public static String signOwner(String ownerCi, Duration ttl) {
        long iat = System.currentTimeMillis() / 1000L;
        long exp = iat + ttl.toSeconds();
        String payload = ownerCi + "|" + iat + "|" + exp;
        String sig = hmac(payload, SECRET);
        return B64.encodeToString(payload.getBytes(StandardCharsets.UTF_8)) + "." + sig;
    }

    public static String verifyAndGetOwner(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 2) throw new IllegalArgumentException("bad token");
        String payload = new String(B64D.decode(parts[0]), StandardCharsets.UTF_8);
        String sig = parts[1];
        if (!hmac(payload, SECRET).equals(sig)) throw new SecurityException("bad sig");

        String[] fields = payload.split("\\|");
        if (fields.length != 3) throw new IllegalArgumentException("bad payload");
        String ownerCi = fields[0];
        long exp = Long.parseLong(fields[2]);
        long now = System.currentTimeMillis() / 1000L;
        if (now > exp) throw new SecurityException("expired");
        return ownerCi;
    }

    private static String hmac(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] out = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(out.length * 2);
            for (byte b : out) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

