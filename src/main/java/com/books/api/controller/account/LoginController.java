package com.books.api.controller.account;

import com.books.api.model.Account;
import com.books.api.repository.AccountRepository;
import com.books.api.util.ApiResponse;
import com.books.api.util.CookieUtil;
import com.books.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class LoginController {

    private final AccountRepository accountRepository;
    private final JwtUtil jwt;
    private final CookieUtil cookieUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpServletRequest request, HttpServletResponse response) {

        String getToken = cookieUtil.getTokenFromRequest(request);
        if (getToken != null && jwt.isTokenValid(getToken)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "Você já está logado."));
        }

        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("400", "E-mail e senha são obrigatórios."));
        }

        Optional<Account> opt = accountRepository.findByEmail(email.trim().toLowerCase());
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("404", "Conta não encontrada."));
        }

        Account account = opt.get();

        if (!BCrypt.checkpw(password, account.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("401", "Senha incorreta. "));
        }

        if (!account.getStatus().name().equals("ON")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("403", "Conta desativada ou não autorizada."));
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", account.getId());

        String token = jwt.generateToken(String.valueOf(account.getId()), claims);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", account.getId());
        data.put("name", account.getName());
        data.put("photo", account.getPhoto());
        data.put("role", account.getRole().name());
        data.put("token", token);

        // JWT cookie (HttpOnly)
        cookieUtil.cookieToken(token, response);

        // Dados do usuário em JSON (visível para JS)
        cookieUtil.cookieUser(account, token, response);

        return ResponseEntity.ok(ApiResponse.success("200", "Login realizado com sucesso.", data));
    }
}