package com.books.api.controller;

import com.books.api.model.Account;
import com.books.api.repository.AccountRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class ProfileController {

    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        String token = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null || !jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Token inválido ou ausente."));
        }

        Long userId = jwtUtil.getUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Token inválido."));
        }

        Account user = accountRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Usuário não encontrado."));
        }

        // Cria o mapa apenas com os campos desejados
        Map<String, Object> safeUser = new HashMap<>();
        safeUser.put("id", user.getId());
        safeUser.put("createdAt", user.getCreatedAt());
        safeUser.put("name", user.getName());
        safeUser.put("email", user.getEmail());
        safeUser.put("photo", user.getPhoto());
        safeUser.put("cpf", user.getCpf());
        safeUser.put("tel", user.getTel());
        safeUser.put("birth", user.getBirth());
        safeUser.put("address", user.getAddress());
        safeUser.put("role", user.getRole());

        return ResponseEntity.ok(
                ApiResponse.success("200", "Perfil carregado com sucesso.", safeUser)
        );
    }
}
