package com.books;

import ch.qos.logback.classic.util.LogbackMDCAdapter;
import com.books.api.model.Account;
import com.books.api.repository.AccountRepository;
import com.books.api.util.CookieUtil;
import com.books.api.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

public class PasswordEncryptor {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "Senha123";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Hash de Senha123: " + encodedPassword);
    }
}

