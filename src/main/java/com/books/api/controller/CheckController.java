package com.books.api.controller;

import com.books.api.model.Account;
import com.books.api.repository.AccountRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.CookieUtil;
import com.books.api.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class CheckController {

    private final CookieUtil cookieUtil;
    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;

    @GetMapping("/check")
    public ResponseEntity<?> check(HttpServletRequest request) {
        String token = cookieUtil.getTokenFromRequest(request);

        if (token == null || !jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body(ApiResponse.error("401", "Usuário não autenticado."));
        }

        Claims claims;
        try {
            claims = jwtUtil.parseToken(token).getBody();
        } catch (JwtException e) {
            return ResponseEntity.status(401).body(ApiResponse.error("401", "Token inválido."));
        }

        Long id = Long.parseLong(claims.getSubject());
        Account account = accountRepository.findById(id).orElse(null);

        if (account == null || !account.getStatus().name().equals("ON")) {
            return ResponseEntity.status(403).body(ApiResponse.error("403", "Conta inativa ou não encontrada."));
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", account.getId());
        data.put("name", account.getName());
        data.put("photo", account.getPhoto());
        data.put("role", account.getRole().name());

        return ResponseEntity.ok(ApiResponse.success("200", "Sessão válida.", data));
    }
}
