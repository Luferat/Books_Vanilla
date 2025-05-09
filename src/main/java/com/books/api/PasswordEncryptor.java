package com.books.api;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncryptor {
    public static void main(String[] args) {
        String rawPassword = "Senha123";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Senha original: " + rawPassword);
        System.out.println("Hash Bcrypt: " + encodedPassword);
    }
}