package com.books.api.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class AppUtil {

    // Gera uma senha aleatória [A-Za-z0-9]
    public String generatePassword(int minLen, int maxLen) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom rand = new SecureRandom();
        int length = rand.nextInt((maxLen - minLen) + 1) + minLen;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }

}
