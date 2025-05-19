package com.books.api.util;

import com.books.api.config.Config;
import com.books.api.model.Account;
import com.books.api.repository.AccountRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final Config config;
    private SecretKey key;
    private long expiration;

    @PostConstruct
    public void init() {
        String secret = config.getSecretKey();
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = config.getTokenMaxAge() * 3600L * 1000L; // Convertendo para milissegundos
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parseToken(String token) throws JwtException {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseToken(token).getBody();
            String subject = claims.getSubject();  // Aqui seria o ID do usuário
            // Verifica se o 'subject' é um número válido
            long userId = Long.parseLong(subject);
            if (userId <= 0) {
                return false;
            }
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        try {
            Claims claims = parseToken(token).getBody();
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    public String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public Account getLoggedUser(HttpServletRequest request, AccountRepository repo) {
        String token = extractTokenFromCookies(request);
        if (token == null || !isTokenValid(token)) {
            return null;
        }
        Long userId = getUserId(token);
        if (userId == null) {
            return null;
        }
        return repo.findById(userId)
                .filter(acc -> acc.getStatus() == Account.Status.ON)
                .orElse(null);
    }
}