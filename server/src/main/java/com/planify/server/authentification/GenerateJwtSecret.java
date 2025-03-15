package com.planify.server.authentification;

import java.util.Base64;
import java.security.SecureRandom;

public class GenerateJwtSecret {
    public static void main(String[] args) {
        byte[] key = new byte[32]; // 32 bytes for HS256
        new SecureRandom().nextBytes(key);
        String secret = Base64.getEncoder().encodeToString(key);
        System.out.println("Your JWT Secret Key: " + secret);
    }
}

