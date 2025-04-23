package com.testing13.service;

import java.security.SecureRandom;
import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {
    public static void main(String[] args) {
        byte[] key = new byte[64];
        new SecureRandom().nextBytes(key);
        String secret = Base64.getEncoder().encodeToString(key);
        System.out.println("Generated JWT Secret: " + secret);
    }
}

